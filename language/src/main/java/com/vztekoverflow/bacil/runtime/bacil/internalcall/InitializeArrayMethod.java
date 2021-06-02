package com.vztekoverflow.bacil.runtime.bacil.internalcall;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLIComponentTablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIFieldRVATableRow;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIFieldTableRow;
import com.vztekoverflow.bacil.runtime.SZArray;
import com.vztekoverflow.bacil.runtime.bacil.JavaMethod;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypeHelpers;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

public class InitializeArrayMethod extends JavaMethod {

    private final Type retType;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] argTypes;

    private final Type definingType;

    private final int HAS_FIELD_RVA = 0x0100;


    public InitializeArrayMethod(BuiltinTypes builtinTypes, TruffleLanguage<?> language, Type definingType) {
        super(language);
        retType = builtinTypes.getVoidType();
        argTypes = new Type[] {builtinTypes.getObjectType(), builtinTypes.getObjectType()}; //Two references
        this.definingType = definingType;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        SZArray arrayRef = (SZArray) frame.getArguments()[0];
        CLIComponentTablePtr token = (CLIComponentTablePtr) frame.getArguments()[1];
        CLIComponent component = token.getComponent();
        BuiltinTypes builtinTypes = component.getBuiltinTypes();

        CLIFieldTableRow field = component.getTableHeads().getFieldTableHead().skip(token.getPtr());
        if ((field.getFlags() & HAS_FIELD_RVA) != HAS_FIELD_RVA) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Field " + field.getName() + " has no RVA");
        }
        CLIFieldRVATableRow foundRVA = null;

        for (CLIFieldRVATableRow fieldRVA : component.getTableHeads().getFieldRVATableHead()) {
            if (fieldRVA.getField().getRowNo() == field.getRowNo()) {
                foundRVA = fieldRVA;
                break;
            }
        }

        if (foundRVA == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Can't find fieldRVA for field " + field.getName());
        }

        ByteSequenceBuffer data = component.getBuffer(foundRVA.getRVA());
        Type arrayElementType = arrayRef.getElementType();

        for (int i = 0; i < arrayRef.getLength(); i++) {
            if (arrayElementType == builtinTypes.getSingleType()) {
                arrayRef.getFieldsHolder().getPrimitives()[i] = Double.doubleToLongBits(Float.intBitsToFloat(data.getInt()));
            }
            else if (arrayElementType == builtinTypes.getInt32Type())
            {
                arrayRef.getFieldsHolder().getPrimitives()[i] = TypeHelpers.signExtend32(data.getInt());
            }
            else if (arrayElementType == builtinTypes.getUInt32Type())
            {
                arrayRef.getFieldsHolder().getPrimitives()[i] = TypeHelpers.zeroExtend32(data.getInt());
            }
            else if (arrayElementType == builtinTypes.getInt64Type() ||
                    arrayElementType == builtinTypes.getUInt64Type() ||
                    arrayElementType == builtinTypes.getDoubleType() )
            {
                arrayRef.getFieldsHolder().getPrimitives()[i] = data.getLong();
            }
            else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new BACILInternalError("InitializeArray for type " + arrayRef.getElementType() + " not implemented.");
            }
        }


        return null;
    }

    @Override
    public Type getRetType() {
        return retType;
    }

    @Override
    public int getArgsCount() {
        return argTypes.length;
    }

    @Override
    public int getVarsCount() {
        return 0;
    }

    @Override
    public Type[] getLocationsTypes() {
        return argTypes;
    }

    @Override
    public Type getDefiningType() {
        return definingType;
    }
}
