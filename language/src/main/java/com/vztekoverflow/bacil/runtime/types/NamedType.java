package com.vztekoverflow.bacil.runtime.types;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIFieldTableRow;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.parser.signatures.FieldSig;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.types.builtin.*;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsDescriptor;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.locations.VtableSlotIdentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NamedType extends Type {

    private final int flags;
    private final String name;
    private final String namespace;
    private final Type extendz;

    private final CLIMethodDefTableRow methods;
    private final int methodsEnd;


    private final CLIFieldTableRow fieldRows;
    private final int fieldsRowsStart;
    private final int fieldRowsEnd;


    private final CLIComponent definingComponent;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private VtableSlotIdentity[] vtableSlots;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private BACILMethod[] vtable;


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



    public void init()
    {
        if(!inited)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            super.init();
            if(extendz != null)
            {
                extendz.init();
            }


            //Init fields

            final int totalFields = fieldRowsEnd-fieldsRowsStart;


            int instanceFieldsCount = 0;
            int staticFieldsCount = 0;
            CLIFieldTableRow currField = fieldRows;
            for(int i = 0; i < totalFields; i++)
            {
                if((currField.getFlags() & TypedField.FIELD_ATTRIBUTE_STATIC) != 0)
                {
                    staticFieldsCount++;
                } else {
                    instanceFieldsCount++;
                }
                currField = currField.next();
            }

            instanceFields = new TypedField[instanceFieldsCount];
            staticFields = new TypedField[staticFieldsCount];
            allFields = new TypedField[totalFields];
            int staticFieldsIndex = 0;
            int instanceFieldsIndex = 0;
            int parentInstanceFields = 0;
            if(extendz != null)
            {
                parentInstanceFields = extendz.instanceFieldsDescriptor.getSize();
            }

            currField = fieldRows;
            for(int i = 0; i < totalFields; i++) {
                TypedField field;
                if ((currField.getFlags() & TypedField.FIELD_ATTRIBUTE_STATIC) != 0) {
                    field = new TypedField(currField.getFlags(), currField.getName().read(definingComponent.getStringHeap()), FieldSig.read(currField.getSignature().read(definingComponent.getBlobHeap()), definingComponent), staticFieldsIndex);
                    staticFields[staticFieldsIndex++] = field;
                } else {
                    field = new TypedField(currField.getFlags(), currField.getName().read(definingComponent.getStringHeap()), FieldSig.read(currField.getSignature().read(definingComponent.getBlobHeap()), definingComponent), instanceFieldsIndex+parentInstanceFields);
                    instanceFields[instanceFieldsIndex++] = field;
                }
                allFields[i] = field;
                currField = currField.next();
            }
            this.instanceFieldsDescriptor = LocationsDescriptor.forFields(extendz == null ? null : extendz.instanceFieldsDescriptor, instanceFields);
            this.staticFieldsDescriptor = LocationsDescriptor.forFields(staticFields);
            this.staticFieldsHolder = LocationsHolder.forDescriptor(staticFieldsDescriptor);

            //Create vtable slots
            //TODO respect NewSlot/ReuseSlot flags

            ArrayList<VtableSlotIdentity> identities = new ArrayList<VtableSlotIdentity>();
            ArrayList<BACILMethod> vtableList = new ArrayList<BACILMethod>();
            if(extendz != null && extendz.getVtableSlots() != null)
            {
                identities.addAll(Arrays.asList(extendz.getVtableSlots()));
                vtableList.addAll(Arrays.asList(extendz.getVtable()));
            }


            CLIMethodDefTableRow currMethod = methods;

            while(currMethod.getRowNo() < methodsEnd)
            {
                if((currMethod.getFlags() & CILMethod.METHODATTRIBUTE_VIRTUAL) == CILMethod.METHODATTRIBUTE_VIRTUAL)
                {
                    BACILMethod virtualMethod = definingComponent.getLocalMethod(currMethod, this);
                    boolean found = false;
                    for(int i = 0; i<identities.size(); i++)
                    {
                        if(identities.get(i).resolves(virtualMethod))
                        {
                            vtableList.set(i, virtualMethod);
                            found = true;
                            break;
                        }
                    }

                    if(!found)
                    {
                        //New vtable slot
                        identities.add(new VtableSlotIdentity(virtualMethod.getName(), virtualMethod.getSignature()));
                        vtableList.add(virtualMethod);
                    }
                }

                currMethod = currMethod.next();
            }

            vtableSlots = identities.toArray(new VtableSlotIdentity[0]);
            vtable = vtableList.toArray(new BACILMethod[0]);

            MethodDefSig CCTOR_SIGNATURE = new MethodDefSig(false, false, (byte)0, 0, definingComponent.getBuiltinTypes().getVoidType(), new Type[0]);

            BACILMethod cctor = getMemberMethod(".cctor", CCTOR_SIGNATURE);
            if(cctor != null)
            {
                cctor.getMethodCallTarget().call();
            }
        }
    }

    private int getFieldIndex(CLITablePtr token)
    {
        return token.getRowNo() - fieldsRowsStart;
    }

    public TypedField getTypedField(CLITablePtr token, CLIComponent callingComponent)
    {
        if(token.getTableId() == CLITableConstants.CLI_TABLE_FIELD)
        {
            return getTypedField(getFieldIndex(token));
        } else {
            return super.getTypedField(token, callingComponent);
        }

    }

    public int getFieldsCount()
    {
        return fieldRowsEnd-fieldsRowsStart;
    }


    @Override
    public Type getDirectBaseClass() {
        return extendz;
    }

    @Override
    public BACILMethod getMemberMethod(String name, MethodDefSig signature) {
        CompilerAsserts.neverPartOfCompilation();

        CLIMethodDefTableRow curr = methods;
        int end;


        while(curr.getRowNo() < methodsEnd)
        {
            if(curr.getName().read(definingComponent.getStringHeap()).equals(name)
                    && MethodDefSig.read(curr.getSignature().read(definingComponent.getBlobHeap()), definingComponent).compatibleWith(signature))
                return definingComponent.getLocalMethod(curr, this);

            curr = curr.next();
        }

        return null;
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

    public TypedField[] getInstanceFields() {
        return instanceFields;
    }

    public LocationsDescriptor getInstanceFieldsDescriptor() {
        return instanceFieldsDescriptor;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public VtableSlotIdentity[] getVtableSlots() {
        return vtableSlots;
    }

    @Override
    public BACILMethod[] getVtable() {
        return vtable;
    }
}

