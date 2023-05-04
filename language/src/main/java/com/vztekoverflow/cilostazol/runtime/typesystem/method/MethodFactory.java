package com.vztekoverflow.cilostazol.runtime.typesystem.method;

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
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.ITypeFactory;

import java.util.ArrayList;
import java.util.List;

public class MethodFactory implements IMethodFactory {
    private final CLIFile _file;
    private final ITypeFactory _typeFactory;

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
    //endregion

    //region IMethodFactory
    @Override
    public IMethod create(CLIMethodDefTableRow methodDef, IComponent definingComponent, IType definingType) {
        final MethodDefSig signature = MethodDefSig.parse(new SignatureReader(methodDef.getSignature().read(_file.getBlobHeap())), _file);
        final String name = methodDef.getName().read(_file.getStringHeap());
        final boolean isVirtual = (methodDef.getFlags() & METHODATTRIBUTE_VIRTUAL) == METHODATTRIBUTE_VIRTUAL;

        ByteSequenceBuffer buf = _file.getBuffer(methodDef.getRVA());
        final byte firstByte = buf.getByte();

        final int size;
        final short maxStackSize;
        IParameter[] locals;
        final boolean initLocals;
        final int ilFlags;
        final ITypeParameter[] typeParameters;
        if((firstByte & 3) == CORILMETHOD_TINYFORMAT){
            ilFlags = CORILMETHOD_TINYFORMAT;
            maxStackSize = 8;
            locals = null;
            initLocals = false;
            typeParameters = null;
            size = (firstByte >> 2) & 0xFF;
        }
        else if((firstByte & 3) == CORILMETHOD_FATFORMAT) {
            short firstWord = (short)(firstByte | (buf.getByte() << 8));
            ilFlags = firstWord & 0xFFF;
            byte headerSize = (byte)(firstWord >> 12);
            if(headerSize != 3)
            {
                throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.parser.fatHeader.size"));
            }

            if (signature.getGenParamCount() != 0) {
                typeParameters = new ITypeParameter[signature.getGenParamCount()];
                int idx = 0;
                for(CLIGenericParamTableRow row : _file.getTableHeads().getGenericParamTableHead()) {
                    if (methodDef.getTableId() == row.getOwner().getTableId()
                    && methodDef.getRowNo() == row.getOwner().getRowNo())
                    {
                        typeParameters[idx++] = new TypeParameter(getConstrains(row,typeParameters, definingType.getTypeParameters()), _file, definingComponent);
                    }
                }
            }
            else {
                typeParameters = null;
            }

            maxStackSize = buf.getShort();
            size = buf.getInt();
            int localVarSigTok = buf.getInt();
            if(localVarSigTok == 0){
                locals = null;
            }
            else {
                LocalVarsSig sig = LocalVarsSig.read(new SignatureReader(_file.getTableHeads().getStandAloneSigTableHead().skip(CLITablePtr.fromToken(localVarSigTok)).getSignature().read(_file.getBlobHeap())),_file);
                locals = new IParameter[sig.getVarsCount()];
                for (int i = 0; i < locals.length; i++) {
                    locals[i] = new Parameter(sig.getVars()[i].isByRef(), sig.getVars()[i].isPinned(), _typeFactory.create(sig.getVars()[i].getTypeSig(), typeParameters, definingType.getTypeParameters()));
                }
            }

            initLocals = (ilFlags & CORILMETHOD_INITLOCALS) != 0;
        }
        else {
            throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.parser.CorILMethod.flags.invalid"));
        }

        //TODO: body
        final byte[] body = buf.subSequence(size).toByteArray();

        final IExceptionHandler[] handlers;
        if((firstByte & 3) == CORILMETHOD_FATFORMAT && (ilFlags & CORILMETHOD_MORESECTS) != 0)
        {
            buf.setPosition(buf.getPosition() + size);
            final byte kind = buf.getByte();
            if ((kind & CORILMETHOD_SECT_FATFORMAT) == CORILMETHOD_SECT_FATFORMAT) {
                final int dataSize = buf.getByte() | buf.getByte() << 8 | buf.getByte() << 16;
                final int clauses = dataSize - 4 / 12;
                handlers = new IExceptionHandler[clauses];
                for (int i = 0; i < clauses; i++) {
                    final int flags = buf.getInt();
                    final int tryoffset = buf.getInt();
                    final int trylength = buf.getInt();
                    final int handleroffset = buf.getInt();
                    final int handlerlength = buf.getInt();
                    final int classToken = buf.getInt();
                    buf.getInt(); // filterOffset
                    //handlers[i] = new ExceptionHandler(tryoffset, trylength, handleroffset, handlerlength, CLITablePtr.fromToken(classToken))
                }
            }
            else {
                final byte dataSize = buf.getByte();
                buf.getShort();
                final int clauses = dataSize - 4 / 12;
                handlers = new IExceptionHandler[clauses];
                for (int i = 0; i < clauses; i++) {
                    final short flags = buf.getShort();
                    final short tryoffset = buf.getShort();
                    final byte trylength = buf.getByte();
                    final short handleroffset = buf.getShort();
                    final byte handlerlength = buf.getByte();
                    final int classToken = buf.getInt();
                    buf.getInt(); // filterOffset
                    //handlers[i] = new ExceptionHandler(tryoffset, trylength, handleroffset, handlerlength, CLITablePtr.fromToken(classToken))
                }
            }
        }

        int explicitArgsStart = 0;
        if (signature.hasThis() && !signature.hasExplicitThis())
        {
            if(definingType == null)
            {
                throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.constructor.withoutDefType"));
            }
            explicitArgsStart = 1;
        }

        final int argsCount = signature.getParams().length + explicitArgsStart;
        final IParameter[] parameters = new IParameter[argsCount];
        for (int i = 0; i < argsCount; i++) {
            parameters[i] = new Parameter(signature.getParams()[i].isByRef(), false, getType(signature.getParams()[i], typeParameters, definingType.getTypeParameters()));
        }

        IParameter retType = new Parameter(signature.getRetType().isByRef(), false, getType(signature.getRetType(), typeParameters, definingType.getTypeParameters()));
        final IParameter _this;
        if (signature.hasThis() && !signature.hasExplicitThis()) {
            //TODO: isVirtual and value type -> solve later
            _this = new Parameter(false, false, definingType);
        }
        else {
            _this = null;
        }

        if (typeParameters != null)
            return new OpenGenericMethod(_file, name, signature.hasThis(), signature.hasExplicitThis(), signature.hasVararg(), isVirtual, parameters, locals, typeParameters, retType, _this, null, definingComponent, definingType);
        else
            return new NonGenericMethod(_file, name, signature.hasThis(), signature.hasExplicitThis(),signature.hasVararg(), isVirtual, parameters, locals, retType, _this, null, definingComponent, definingType);
    }

    private IType[] getConstrains(CLIGenericParamTableRow row, IType[] mvars, IType[] vars) {
        List<IType> constrains = new ArrayList<IType>();
        for(CLIGenericParamConstraintTableRow r : _file.getTableHeads().getGenericParamConstraintTableHead()) {
            if (row.getTableId() == r.getOwner().getTableId()
                    && row.getRowNo() == r.getOwner().getRowNo())
            {
                if (r.getConstraint().getTableId() == CLITableConstants.CLI_TABLE_TYPE_DEF)
                    constrains.add(_typeFactory.create(_file.getTableHeads().getTypeDefTableHead().skip(r.getConstraint()), mvars, vars));
                else if (r.getConstraint().getTableId() == CLITableConstants.CLI_TABLE_TYPE_REF)
                    constrains.add(_typeFactory.create(_file.getTableHeads().getTypeRefTableHead().skip(r.getConstraint()), mvars, vars));
                else if (r.getConstraint().getTableId() == CLITableConstants.CLI_TABLE_TYPE_SPEC)
                    constrains.add(_typeFactory.create(_file.getTableHeads().getTypeSpecTableHead().skip(r.getConstraint()), mvars, vars));
            }
        }

        return (IType[])constrains.toArray();
    }
    private IType getType(ParamSig signature, IType[] mvars, IType[] vars) {
        if (signature.isVoid())
            return _typeFactory.createVoid();
        else if (signature.isTypedByRef())
            return _typeFactory.createTypedRef();
        else
            return _typeFactory.create(signature.getTypeSig(), mvars, vars);
    }
    //endregion

    public MethodFactory(CLIFile file, ITypeFactory factory) {
        _file = file;
        _typeFactory = factory;
    }
}
