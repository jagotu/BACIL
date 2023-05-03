package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.signature.LocalVarsSig;
import com.vztekoverflow.cil.parser.cli.signature.MethodDefSig;
import com.vztekoverflow.cil.parser.cli.signature.ParamSig;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodDefTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.ITypeFactory;

public class MethodFactory implements IMethodFactory {
    private final CLIFile _file;
    private final ITypeFactory _typeFactory;

    //region Constants
    private static final byte CORILMETHOD_TINYFORMAT = 2;
    private static final byte CORILMETHOD_FATFORMAT = 3;
    private static final byte CORILMETHOD_INITLOCALS = 0x10;
    private static final byte CORILMETHOD_MORESECTS = 0x8;
    public static final int METHODATTRIBUTE_VIRTUAL = 0x0040;
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

            //TODO: type parameters
            typeParameters = null;

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
                    locals[i] = new Parameter(sig.getVars()[i].isByRef(), sig.getVars()[i].isPinned(), _typeFactory.create(sig.getVars()[i].getTypeSig()));
                }
            }

            initLocals = (ilFlags & CORILMETHOD_INITLOCALS) != 0;
        }
        else {
            throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.parser.CorILMethod.flags.invalid"));
        }

        //TODO: body
        final byte[] body = buf.subSequence(size).toByteArray();

        //TODO: exceptions
        if((firstByte & 3) == CORILMETHOD_FATFORMAT && (ilFlags & CORILMETHOD_MORESECTS) != 0)
        {
            throw new NotImplementedException();
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
            parameters[i] = new Parameter(signature.getParams()[i].isByRef(), false, getType(signature.getParams()[i]));
        }

        IParameter retType = new Parameter(signature.getRetType().isByRef(), false, getType(signature.getRetType()));
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

    private IType getType(ParamSig signature) {
        if (signature.isVoid())
            return _typeFactory.createVoid();
        else if (signature.isTypedByRef())
            return _typeFactory.createTypedRef();
        else
            return _typeFactory.create(signature.getTypeSig());
    }
    //endregion

    public MethodFactory(CLIFile file, ITypeFactory factory) {
        _file = file;
        _typeFactory = factory;
    }
}
