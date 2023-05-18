package com.vztekoverflow.cilostazol.runtime.typesystem.method.factory;

import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.signature.*;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.*;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.NonGenericMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.OpenGenericMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.ExceptionClauseFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.ExceptionHandler;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.IExceptionHandler;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodHeaderFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodImplFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodSectionFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.local.ILocal;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.local.Local;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.IParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.ParamFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.Parameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.returnType.IReturnType;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.returnType.ReturnType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.CLIType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.NonGenericType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.factory.TypeFactory;

public final class MethodFactory {
    public static IMethod create(CLIMethodDefTableRow mDef, CLIType definingType) {
        final IType[] definingTypeParameters = (definingType instanceof NonGenericType) ? new IType[0] : definingType.getTypeParameters();
        final CLIFile file = definingType.getDefiningFile();
        final MethodDefSig mSignature = MethodDefSig.parse(new SignatureReader(mDef.getSignatureHeapPtr().read(file.getBlobHeap())), file);
        final String name = mDef.getNameHeapPtr().read(file.getStringHeap());
        final MethodFlags flags = new MethodFlags(mDef.getFlags());


        // Type parameters parsing
        final ITypeParameter[] typeParameters = FactoryUtils.getTypeParameters(mSignature.getGenParamCount(), mDef.getPtr(), definingTypeParameters, definingType.getCLIComponent());

        final int codeSize;
        final short maxStackSize;
        final ILocal[] locals;
        final int ilFlags;
        final MethodHeaderFlags methodHeaderFlags;
        final int headerSize;
        final byte[] _cil;
        final IExceptionHandler[] handlers;

        //Method header parsing
        if (!flags.hasFlag(MethodFlags.Flag.ABSTRACT)) {
            final ByteSequenceBuffer buf = file.getBuffer(mDef.getRVA());

            final byte firstByte = buf.getByte();
            final MethodHeaderFlags pom = new MethodHeaderFlags(firstByte);
            if (pom.hasFlag(MethodHeaderFlags.Flag.CORILMETHOD_TINYFORMAT)) {
                maxStackSize = 8;
                locals = new ILocal[0];
                methodHeaderFlags = new MethodHeaderFlags(firstByte & 0x3);
                headerSize = 1;
                codeSize = (firstByte >> 2) & 0xFF;
            } else if (pom.hasFlag(MethodHeaderFlags.Flag.CORILMETHOD_FATFORMAT)) {
                final short firstWord = (short) (firstByte | (buf.getByte() << 8));
                methodHeaderFlags = new MethodHeaderFlags(firstWord & 0xFFF);
                headerSize = (firstWord >> 12);
                maxStackSize = buf.getShort();
                codeSize = buf.getInt();
                final int localTok = buf.getInt();
                // Locals parsing
                if (localTok == 0)
                {
                    locals = new ILocal[0];
                }
                else
                {
                    locals = createLocals(
                            LocalVarsSig.read(new SignatureReader(file.getTableHeads().getStandAloneSigTableHead().skip(CLITablePtr.fromToken(localTok)).getSignatureHeapPtr().read(file.getBlobHeap())), file),
                            typeParameters,
                            definingTypeParameters,
                            definingType.getCLIComponent());
                }
                if (headerSize != 3) {
                    throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.parser.fatHeader.size"));
                }
            } else {
                throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.parser.method.general"));
            }

             _cil = buf.subSequence(codeSize).toByteArray();

            //Exception handlers parsing
            if (methodHeaderFlags.hasFlag(MethodHeaderFlags.Flag.CORILMETHOD_FATFORMAT) && methodHeaderFlags.hasFlag(MethodHeaderFlags.Flag.CORILMETHOD_MORESECTS)) {
                buf.setPosition(buf.getPosition() + codeSize);
                buf.align(4);

                handlers = createHandlers(buf, typeParameters, definingTypeParameters, definingType.getCLIComponent());
            } else {
                handlers = new IExceptionHandler[0];
            }
        }
        else
        {
            codeSize = 0;
            maxStackSize = 0;
            locals = new ILocal[0];
            ilFlags = 0;
            methodHeaderFlags = null;
            headerSize = 0;
            _cil = new byte[0];
            handlers = new IExceptionHandler[0];
        }

        //Sort params
        CLIParamTableRow[] params = new CLIParamTableRow[mSignature.getParams().length];
        CLIParamTableRow paramRow = file.getTableHeads().getParamTableHead().skip(mDef.getParamListTablePtr());
        for (int i = 0; i < params.length; i++) {
            params[paramRow.getSequence()-1] = paramRow;
            paramRow = paramRow.next();
        }

        //Return type parsing
        IReturnType retType = new ReturnType(mSignature.getRetType().isByRef(), TypeFactory.create(mSignature.getRetType().getTypeSig(), typeParameters, definingTypeParameters, definingType.getCLIComponent()));

        //Parameters parsing
        final IParameter[] parameters = createParameters(mSignature.getParams(), params, typeParameters, definingTypeParameters, definingType.getCLIComponent(), file);

        if (mSignature.getMethodDefFlags().hasFlag(MethodDefFlags.Flag.GENERIC))
            return new OpenGenericMethod(
                    file,
                    name,
                    definingType.getCLIComponent(),
                    definingType,
                    mSignature.getMethodDefFlags(),
                    flags,
                    new MethodImplFlags(mDef.getImplFlags()),
                    methodHeaderFlags,
                    parameters,
                    locals,
                    retType,
                    handlers,
                    _cil,
                    maxStackSize,
                    typeParameters);
        else
            return new NonGenericMethod(
                    file,
                    name,
                    definingType.getCLIComponent(),
                    definingType,
                    mSignature.getMethodDefFlags(),
                    flags ,
                    new MethodImplFlags(mDef.getImplFlags()),
                    methodHeaderFlags,
                    parameters,
                    locals,
                    retType,
                    handlers,
                    _cil,
                    maxStackSize);
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
    private static ILocal[] createLocals(LocalVarsSig signatures, IType[] mvars, IType[] vars, CLIComponent component)
    {
        ILocal[] locals = new ILocal[signatures.getVarsCount()];
        for (int i = 0; i < locals.length; i++) {
            locals[i] = new Local(
                    signatures.getVars()[i].isByRef(),
                    signatures.getVars()[i].isPinned(),
                    TypeFactory.create(signatures.getVars()[i].getTypeSig(), mvars, vars, component)
            );
        }
        return locals;
    }

    private static IExceptionHandler[] createHandlers(ByteSequenceBuffer buf, IType[] mvars, IType[] vars, CLIComponent component)
    {
        final IExceptionHandler[] handlers;
        final byte kind = buf.getByte();

        final MethodSectionFlags sectionFlags = new MethodSectionFlags(kind);
        final int dataSize;
        final int clauses;

        if (!sectionFlags.hasFlag(MethodSectionFlags.Flag.CORILMETHOD_SECT_EHTABLE))
            return new IExceptionHandler[0];

        if (sectionFlags.hasFlag(MethodSectionFlags.Flag.CORILMETHOD_SECT_FATFORMAT))
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
            final ExceptionClauseFlags flags;
            final int tryoffset, trylength, handleroffset, handlerlength, classTokenOrFilterOffset;
            if (sectionFlags.hasFlag(MethodSectionFlags.Flag.CORILMETHOD_SECT_FATFORMAT))
            {
                flags = new ExceptionClauseFlags(buf.getInt());
                tryoffset = buf.getInt();
                trylength = buf.getInt();
                handleroffset = buf.getInt();
                handlerlength = buf.getInt();
                classTokenOrFilterOffset = buf.getInt();
            }
            else
            {
                flags = new ExceptionClauseFlags(buf.getShort());
                tryoffset = buf.getShort();
                trylength = buf.getByte();
                handleroffset = buf.getShort();
                handlerlength = buf.getByte();
                classTokenOrFilterOffset = buf.getInt();
            }
            final IType klass = (flags.hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION))
                    ? TypeFactory.create(CLITablePtr.fromToken(classTokenOrFilterOffset), mvars, vars, component)
                    : null;
            handlers[i] = new ExceptionHandler(tryoffset, trylength, handleroffset, handlerlength, klass, flags);
        }
        return  handlers;
    }

    private static IParameter[] createParameters(ParamSig[] params, CLIParamTableRow[] rows, IType[] mvars, IType[] vars, CLIComponent component, CLIFile file)
    {
        final IParameter[] parameters = new IParameter[params.length];
        for (int i = 0; i < params.length; i++) {
            parameters[i] = new Parameter(params[i].isByRef(), TypeFactory.create(params[i].getTypeSig(), mvars, vars, component), rows[i].getNameHeapPtr().read(file.getStringHeap()), rows[i].getSequence(), new ParamFlags(rows[i].getFlags()));
        }

        return parameters;
    }
    //endregion
}
