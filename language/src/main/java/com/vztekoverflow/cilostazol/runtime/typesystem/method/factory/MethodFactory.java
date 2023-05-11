package com.vztekoverflow.cilostazol.runtime.typesystem.method.factory;

import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.signature.LocalVarsSig;
import com.vztekoverflow.cil.parser.cli.signature.MethodDefSig;
import com.vztekoverflow.cil.parser.cli.signature.ParamSig;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.*;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.*;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.NonGenericType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.factory.FactoryUtils;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.factory.TypeFactory;

import java.util.ArrayList;
import java.util.List;

public class MethodFactory {
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
            byte headerSize = (byte)(firstWord >> 12);
            if(headerSize != 3)
            {
                throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.parser.fatHeader.size"));
            }

            maxStackSize = buf.getShort();
            size = buf.getInt();
            int localVarSigTok = buf.getInt();
            if(localVarSigTok == 0){
                locals = null;
            }
            else {
                LocalVarsSig sig = LocalVarsSig.read(new SignatureReader(file.getTableHeads().getStandAloneSigTableHead().skip(CLITablePtr.fromToken(localVarSigTok)).getSignatureHeapPtr().read(file.getBlobHeap())),file);
                locals = new IParameter[sig.getVarsCount()];
                for (int i = 0; i < locals.length; i++) {
                    locals[i] = new Parameter(sig.getVars()[i].isByRef(), sig.getVars()[i].isPinned(), FactoryUtils.create(sig.getVars()[i].getTypeSig(), typeParameters, definingType.getTypeParameters(), definingType.getDefiningComponent()));
                }
            }

            initLocals = (ilFlags & CORILMETHOD_INITLOCALS) != 0;
        }
        else
        {
            throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.parser.method.general"));
        }

        //TODO: body
        final byte[] body = buf.subSequence(size).toByteArray();

        final IExceptionHandler[] handlers;
        if((firstByte & 3) == CORILMETHOD_FATFORMAT && (ilFlags & CORILMETHOD_MORESECTS) != 0)
        {
            buf.setPosition(buf.getPosition() + size);
            buf.align(4);
            final byte kind = buf.getByte();
            if ((kind & CORILMETHOD_SECT_FATFORMAT) == CORILMETHOD_SECT_FATFORMAT) {
                final int dataSize = buf.getByte() | buf.getByte() << 8 | buf.getByte() << 16;
                final int clauses = (dataSize - 4) / 12;
                handlers = new IExceptionHandler[clauses];
                for (int i = 0; i < clauses; i++) {
                    final int flags = buf.getInt();
                    if ((flags & COR_ILEXCEPTION_CLAUSE_FILTER) == COR_ILEXCEPTION_CLAUSE_FILTER)
                        throw new NotImplementedException();
                    final int tryoffset = buf.getInt();
                    final int trylength = buf.getInt();
                    final int handleroffset = buf.getInt();
                    final int handlerlength = buf.getInt();
                    final int classTokenOrFilterOffset = buf.getInt();
                    final IType klass = (flags == COR_ILEXCEPTION_CLAUSE_EXCEPTION) ? FactoryUtils.createType(CLITablePtr.fromToken(classTokenOrFilterOffset), typeParameters, definingTypeParameters, definingType.getDefiningComponent()) : null;
                    handlers[i] = new ExceptionHandler(tryoffset, trylength, handleroffset, handlerlength, klass, (flags == COR_ILEXCEPTION_CLAUSE_EXCEPTION)? ExceptionHandlerType.TypeBased : ExceptionHandlerType.Finally);
                }
            }
            else {
                final byte dataSize = buf.getByte();
                buf.getShort();
                final int clauses = (dataSize - 4) / 12;
                handlers = new IExceptionHandler[clauses];
                for (int i = 0; i < clauses; i++) {
                    final short flags = buf.getShort();
                    if ((flags & COR_ILEXCEPTION_CLAUSE_FILTER) == COR_ILEXCEPTION_CLAUSE_FILTER)
                        throw new NotImplementedException();
                    final short tryoffset = buf.getShort();
                    final byte trylength = buf.getByte();
                    final short handleroffset = buf.getShort();
                    final byte handlerlength = buf.getByte();
                    final int classTokenOrFilterOffset = buf.getInt();
                    final IType klass = (flags == COR_ILEXCEPTION_CLAUSE_EXCEPTION) ? FactoryUtils.createType(CLITablePtr.fromToken(classTokenOrFilterOffset), typeParameters, definingTypeParameters, definingType.getDefiningComponent()) : null;
                    handlers[i] = new ExceptionHandler(tryoffset, trylength, handleroffset, handlerlength, klass, (flags == COR_ILEXCEPTION_CLAUSE_EXCEPTION)? ExceptionHandlerType.TypeBased : ExceptionHandlerType.Finally);
                }
            }
        }
        else
        {
            handlers = null;
        }

        int explicitArgsStart = 0;
        if (mSignature.hasThis() && mSignature.hasExplicitThis())
        {
            explicitArgsStart = 1;
        }

        final int argsCount = mSignature.getParams().length + explicitArgsStart;
        final IParameter[] parameters = new IParameter[argsCount];
        for (int i = 0; i < argsCount; i++) {
            parameters[i] = new Parameter(mSignature.getParams()[i].isByRef(), false, getType(mSignature.getParams()[i], typeParameters, definingTypeParameters, definingType.getDefiningComponent()));
        }

        IParameter retType = new Parameter(mSignature.getRetType().isByRef(), false, getType(mSignature.getRetType(), typeParameters, definingTypeParameters, definingType.getDefiningComponent()));
        final IParameter _this;
        if (mSignature.hasThis() && !mSignature.hasExplicitThis()) {
            //TODO: isVirtual and value type -> solve later
            _this = new Parameter(false, false, definingType);
        }
        else {
            _this = null;
        }

        if (typeParameters != null)
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
    private static IType getType(ParamSig signature, IType[] mvars, IType[] vars, IComponent component) {
        if (signature.isVoid())
            return TypeFactory.createVoid(component);
        else if (signature.isTypedByRef())
            return TypeFactory.createTypedRef(component);
        else
            return FactoryUtils.create(signature.getTypeSig(), mvars, vars, component);
    }
    //endregion
}
