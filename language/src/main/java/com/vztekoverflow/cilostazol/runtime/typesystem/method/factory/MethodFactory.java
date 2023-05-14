package com.vztekoverflow.cilostazol.runtime.typesystem.method.factory;

import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.signature.LocalVarsSig;
import com.vztekoverflow.cil.parser.cli.signature.MethodDefSig;
import com.vztekoverflow.cil.parser.cli.signature.ParamSig;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodDefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodImplTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodSemanticsTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodSpecTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.NonGenericMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.OpenGenericMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.ExceptionHandler;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.ExceptionHandlerType;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.IExceptionHandler;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.IParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.Parameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.NonGenericType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.factory.TypeFactory;

public final class MethodFactory {
    //region Constants
    private static final byte CORILMETHOD_TINYFORMAT = 2;
    private static final byte CORILMETHOD_FATFORMAT = 3;
    private static final byte CORILMETHOD_INITLOCALS = 0x10;
    private static final byte CORILMETHOD_MORESECTS = 0x8;
    private static final int METHODATTRIBUTE_VIRTUAL = 0x0040;
    private static final byte CORILMETHOD_SECT_EHTABLE = 0x1;
    private static final byte CORILMETHOD_SECT_OPTILTABLE = 0x2;
    private static final byte CORILMETHOD_SECT_FATFORMAT = 0x40;
    private static final int CORILMETHOD_SECT_MORESECTS = 0x80;
    private static final int COR_ILEXCEPTION_CLAUSE_EXCEPTION = 0x0;
    private static final int COR_ILEXCEPTION_CLAUSE_FILTER = 0x1;
    private static final int COR_ILEXCEPTION_CLAUSE_FINALLY = 0x2;
    private static final int COR_ILEXCEPTION_CLAUSE_FAULT = 0x4;
    //endregion

    public static IMethod create(CLIMethodDefTableRow mDef, IType definingType) {
        final IType[] definingTypeParameters = (definingType instanceof NonGenericType) ? new IType[0] : definingType.getTypeParameters();
        final CLIFile file = definingType.getDefiningFile();
        final MethodDefSig mSignature = MethodDefSig.parse(new SignatureReader(mDef.getSignatureHeapPtr().read(file.getBlobHeap())), file);
        final String name = mDef.getNameHeapPtr().read(file.getStringHeap());
        final boolean isVirtual = (mDef.getFlags() & METHODATTRIBUTE_VIRTUAL) == METHODATTRIBUTE_VIRTUAL;
        final ITypeParameter[] typeParameters = FactoryUtils.getTypeParameters(mSignature.getGenParamCount(), mDef.getPtr(), definingTypeParameters, (CLIComponent)definingType.getDefiningComponent());

        final int size;
        final short maxStackSize;
        final IParameter[] locals;
        final boolean initLocals;
        final int ilFlags;

        final ByteSequenceBuffer buf = file.getBuffer(mDef.getRVA());
        final byte firstByte = buf.getByte();

        if((firstByte & 3) == CORILMETHOD_TINYFORMAT) {
            ilFlags = CORILMETHOD_TINYFORMAT;
            maxStackSize = 8;
            locals = null;
            initLocals = false;
            size = (firstByte >> 2) & 0xFF;
        }
        else if((firstByte & 3) == CORILMETHOD_FATFORMAT) {
            final short firstWord = (short)(firstByte | (buf.getByte() << 8));
            ilFlags = firstWord & 0xFFF;
            final byte headerSize = (byte)(firstWord >> 12);
            if(headerSize != 3)
            {
                throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.parser.fatHeader.size"));
            }

            maxStackSize = buf.getShort();
            size = buf.getInt();
            final int localVarSigTok = buf.getInt();

            locals = (localVarSigTok == 0)
                    ? new IParameter[0]
                    : createLocals(
                        LocalVarsSig.read(new SignatureReader(file.getTableHeads().getStandAloneSigTableHead().skip(CLITablePtr.fromToken(localVarSigTok)).getSignatureHeapPtr().read(file.getBlobHeap())),file),
                        typeParameters,
                        definingTypeParameters,
                        definingType.getDefiningComponent()
                        );

            initLocals = (ilFlags & CORILMETHOD_INITLOCALS) != 0;
        }
        else
        {
            throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.parser.method.general"));
        }

        final byte[] body = buf.subSequence(size).toByteArray();

        final IExceptionHandler[] handlers;
        if((firstByte & 3) == CORILMETHOD_FATFORMAT && (ilFlags & CORILMETHOD_MORESECTS) != 0)
        {
            buf.setPosition(buf.getPosition() + size);
            buf.align(4);

            handlers = createHandlers(buf, typeParameters, definingTypeParameters, definingType.getDefiningComponent());
        }
        else
        {
            handlers = new IExceptionHandler[0];
        }

        int explicitArgsStart = 0;
        if (mSignature.hasThis() && mSignature.hasExplicitThis())
        {
            explicitArgsStart = 1;
        }

        final IParameter[] parameters = createParameters(mSignature.getParams(), typeParameters, definingTypeParameters, definingType.getDefiningComponent());

        IParameter retType = new Parameter(mSignature.getRetType().isByRef(), false, createType(mSignature.getRetType(), typeParameters, definingTypeParameters, definingType.getDefiningComponent()));

        final IParameter _this;
        if (mSignature.hasThis() && !mSignature.hasExplicitThis()) {
            //TODO: isVirtual and value type -> solve later
            _this = new Parameter(false, false, definingType);
        }
        else {
            _this = null;
        }

        //TODO: Parse flags

        if (typeParameters.length > 0)
            return new OpenGenericMethod(file, name, mSignature.hasThis(), mSignature.hasExplicitThis(), mSignature.hasVararg(), isVirtual, parameters, locals, typeParameters, retType, _this, handlers, definingType.getDefiningComponent(), definingType, body, maxStackSize);
        else
            return new NonGenericMethod(file, name, mSignature.hasThis(), mSignature.hasExplicitThis(),mSignature.hasVararg(), isVirtual, parameters, locals, retType, _this, handlers, definingType.getDefiningComponent(), definingType, body, maxStackSize);
    }

    public static IMethod create(CLIMethodSpecTableRow mSpec, IComponent component) {
        // A.m<T>()
        throw new NotImplementedException();
    }
    public static IMethod create(CLIMethodImplTableRow mImpl, IType definingType) {
        // I1.m(), I2.m()
        throw new NotImplementedException();
    }

    public static IMethod create(CLIMethodSemanticsTableRow mSem, IType definingType) {
        // getters, setters, etc...
        throw new NotImplementedException();
    }

    //region Helpers
    private static IParameter[] createLocals(LocalVarsSig signatures, IType[] mvars, IType[] vars, IComponent component)
    {
        IParameter[] locals = new IParameter[signatures.getVarsCount()];
        for (int i = 0; i < locals.length; i++) {
            locals[i] = new Parameter(
                    signatures.getVars()[i].isByRef(),
                    signatures.getVars()[i].isPinned(),
                    TypeFactory.create(signatures.getVars()[i].getTypeSig(), mvars, vars, component)
            );
        }
        return locals;
    }

    private static IExceptionHandler[] createHandlers(ByteSequenceBuffer buf, IType[] mvars, IType[] vars, IComponent component)
    {
        final IExceptionHandler[] handlers;
        final byte kind = buf.getByte();
        final boolean fatVersion = (kind & CORILMETHOD_SECT_FATFORMAT) == CORILMETHOD_SECT_FATFORMAT;
        final int dataSize;
        final int clauses;

        if (fatVersion)
        {
            dataSize = buf.getByte() | buf.getByte() << 8 | buf.getByte() << 16;
            clauses = (dataSize - 4) / 12;
            handlers = new IExceptionHandler[clauses];
        }
        else
        {
            dataSize = buf.getByte();
            buf.getShort(); // Reserved
            clauses = (dataSize - 4) / 12;
            handlers = new IExceptionHandler[clauses];
        }

        for (int i = 0; i < clauses; i++)
        {
            final int flags, tryoffset, trylength, handleroffset, handlerlength, classTokenOrFilterOffset;
            if (fatVersion)
            {
                flags = buf.getInt();
                tryoffset = buf.getInt();
                trylength = buf.getInt();
                handleroffset = buf.getInt();
                handlerlength = buf.getInt();
                classTokenOrFilterOffset = buf.getInt();
            }
            else
            {
                flags = buf.getShort();
                tryoffset = buf.getShort();
                trylength = buf.getByte();
                handleroffset = buf.getShort();
                handlerlength = buf.getByte();
                classTokenOrFilterOffset = buf.getInt();
            }
            final ExceptionHandlerType type = getExceptionType(flags);
            final IType klass = (type == ExceptionHandlerType.TypeBased)
                    ? TypeFactory.create(CLITablePtr.fromToken(classTokenOrFilterOffset), mvars, vars, component)
                    : null;
            handlers[i] = new ExceptionHandler(tryoffset, trylength, handleroffset, handlerlength, klass, type );
        }
        return  handlers;
    }

    private static ExceptionHandlerType getExceptionType(int flags)
    {
        switch (flags)
        {
            case COR_ILEXCEPTION_CLAUSE_EXCEPTION:  return ExceptionHandlerType.TypeBased;
            case COR_ILEXCEPTION_CLAUSE_FINALLY:  return ExceptionHandlerType.Finally;
            default: throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.parser.exceptionType"));
        }
    }

    private static IParameter[] createParameters(ParamSig[] params, IType[] mvars, IType[] vars, IComponent component)
    {
        final IParameter[] parameters = new IParameter[params.length];
        for (int i = 0; i < params.length; i++) {
            parameters[i] = new Parameter(params[i].isByRef(), false, createType(params[i], mvars, vars, component));
        }

        return parameters;
    }

    private static IType createType(ParamSig signature, IType[] mvars, IType[] vars, IComponent component) {
        if (signature.isVoid())
            return TypeFactory.createVoid(component);
        else if (signature.isTypedByRef())
            return TypeFactory.createTypedRef(component);
        else
            return TypeFactory.create(signature.getTypeSig(), mvars, vars, component);
    }
    //endregion
}
