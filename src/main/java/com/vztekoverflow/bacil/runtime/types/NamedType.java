package com.vztekoverflow.bacil.runtime.types;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.*;
import com.vztekoverflow.bacil.parser.signatures.FieldSig;
import com.vztekoverflow.bacil.runtime.types.builtin.*;

import java.util.Arrays;
import java.util.List;

public class NamedType extends Type {

    private final int flags;
    private final String name;
    private final String namespace;
    private final Type extendz;

    private final CLIMethodDefTableRow methods;
    private final int methodsEnd;

    @CompilerDirectives.CompilationFinal
    private boolean fieldsInited = false;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private TypedField[] fields = null;

    private final CLIFieldTableRow fieldRows;
    private final int fieldsRowsStart;
    private final int fieldRowsEnd;


    private final CLIComponent definingComponent;


    protected NamedType(CLITypeDefTableRow type, CLIComponent component)
    {
        this.definingComponent = component;
        this.flags = type.getFlags();
        this.name = type.getTypeName().read(component.getStringHeap());
        this.namespace = type.getTypeNamespace().read(component.getStringHeap());
        if(type.getExtends().getRowNo() == 0)
        {
            this.extendz = null;
        } else {
            this.extendz = component.getType(type.getExtends());
        }
        this.methods = component.getTableHeads().getMethodDefTableHead().skip(type.getMethodList());
        this.fieldRows = component.getTableHeads().getFieldTableHead().skip(type.getFieldList());
        this.fieldsRowsStart = fieldRows.getRowNo();
        if(type.hasNext())
        {
            methodsEnd = type.next().getMethodList().getRowNo();
            fieldRowsEnd = type.next().getFieldList().getRowNo();
        } else {
            methodsEnd = component.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_METHOD_DEF)+1;
            fieldRowsEnd = component.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_FIELD)+1;
        }


    }

    public void initFields()
    {
        if(!fieldsInited)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            fieldsInited = true;
            CLIFieldTableRow curr = fieldRows;


            final int fieldsCount = fieldRowsEnd-fieldsRowsStart;
            fields = new TypedField[fieldsCount];


            for(int i = 0; i < fieldsCount; i++)
            {
                fields[i] = new TypedField(curr.getFlags(), curr.getName().read(definingComponent.getStringHeap()), FieldSig.read(curr.getSignature().read(definingComponent.getBlobHeap()), definingComponent), i);
                curr = curr.next();
            }
        }
    }

    private int getFieldIndex(CLITablePtr token)
    {
        return token.getRowNo() - fieldsRowsStart;
    }

    public TypedField getTypedField(int index)
    {
        return fields[index];
    }

    public TypedField getTypedField(CLITablePtr token, CLIComponent callingComponent)
    {
        switch(token.getTableId())
        {
            case CLITableConstants.CLI_TABLE_FIELD:
                return getTypedField(getFieldIndex(token));
            case CLITableConstants.CLI_TABLE_MEMBER_REF:
            {
                CLIMemberRefTableRow row = callingComponent.getTableHeads().getMemberRefTableHead().skip(token);
                return getTypedField(row.getName().read(callingComponent.getStringHeap()));
            }
            default:
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new BACILInternalError("Failed to find typed field from token " + token);
        }
    }

    public int getFieldsCount()
    {
        return fields.length;
    }

    public TypedField getTypedField(String name)
    {
        for(TypedField f : fields)
        {
            if(f.getName().equals(name))
            {
                return f;
            }
        }
        return null;
    }

    @Override
    public Type getDirectBaseClass() {
        return extendz;
    }

    @Override
    public CILMethod getMemberMethod(String name, byte[] signature) {
        CompilerAsserts.neverPartOfCompilation();

        CLIMethodDefTableRow curr = methods;
        int end;


        while(curr.getRowNo() < methodsEnd)
        {
            if(curr.getName().read(definingComponent.getStringHeap()).equals(name) && Arrays.equals(curr.getSignature().read(definingComponent.getBlobHeap()), signature))
                return definingComponent.getLocalMethod(curr, this);

            curr = curr.next();
        }

        throw new BACILInternalError(String.format("Member method %s not found in %s.%s or has invalid signature.", name, namespace,
                this.name));
    }

    @Override
    public boolean isByRef() {
        return false;
    }

    @Override
    public boolean isPinned() {
        return false;
    }

    @Override
    public List<CustomMod> getMods() {
        return null;
    }

    public static NamedType fromTypeDef(CLITypeDefTableRow type, CLIComponent component)
    {
        //final String name = type.getTypeName().read(component.getStringHeap());
        if(type.getTypeNamespace().read(component.getStringHeap()).equals("System"))
        {
            switch(type.getTypeName().read(component.getStringHeap()))
            {
                case "Boolean":
                    return new SystemBooleanType(type, component);
                case "Char":
                    return new SystemCharType(type, component);
                case "Object":
                    return new SystemObjectType(type, component);
                case "String":
                    return new SystemStringType(type, component);
                case "Single":
                    return new SystemSingleType(type, component);
                case "Double":
                    return new SystemDoubleType(type, component);
                case "Byte":
                    return new SystemByteType(type, component);
                case "SByte":
                    return new SystemSByteType(type, component);
                case "Int16":
                    return new SystemInt16Type(type, component);
                case "UInt16":
                    return new SystemUInt16Type(type, component);
                case "Int32":
                    return new SystemInt32Type(type, component);
                case "UInt32":
                    return new SystemUInt32Type(type, component);
                case "Int64":
                    return new SystemInt64Type(type, component);
                case "UInt64":
                    return new SystemUInt64Type(type, component);
                case "IntPtr":
                    return new SystemIntPtrType(type, component);
                case "UIntPtr":
                    return new SystemUIntPtrType(type, component);
                case "TypedReference":
                    return new SystemTypedReferenceType(type, component);
                case "ValueType":
                    return new SystemValueTypeType(type, component);
                case "Void":
                    return new SystemVoidType(type, component);
            }
        }

        return new NamedType(type, component);
    }

    @Override
    public String toString() {
        return String.format("[%s]%s.%s", definingComponent, namespace, name);
    }
}
