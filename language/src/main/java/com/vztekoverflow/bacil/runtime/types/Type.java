package com.vztekoverflow.bacil.runtime.types;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMemberRefTableRow;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.LocationReference;
import com.vztekoverflow.bacil.runtime.StaticObject;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsDescriptor;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.locations.VtableSlotIdentity;

import java.util.List;

public abstract class Type {

    @CompilerDirectives.CompilationFinal
    protected boolean inited = false;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    protected TypedField[] instanceFields = null;


    @CompilerDirectives.CompilationFinal(dimensions = 1)
    protected TypedField[] allFields = null;

    @CompilerDirectives.CompilationFinal
    protected LocationsDescriptor instanceFieldsDescriptor;



    @CompilerDirectives.CompilationFinal(dimensions = 1)
    protected TypedField[] staticFields = null;

    @CompilerDirectives.CompilationFinal
    protected LocationsDescriptor staticFieldsDescriptor;

    @CompilerDirectives.CompilationFinal
    protected LocationsHolder staticFieldsHolder;

    public void init()
    {
        inited = true;
    }

    public Object initialValue()
    {
        return null;
    }

    public void stackToLocation(LocationsHolder holder, int holderOffset, Object ref, long primitive)
    {
        holder.getRefs()[holderOffset] = ref;
    }

    public void locationToStack(LocationsHolder holder, int holderOffset, Object[] refs, long[] primitives, int slot)
    {
        refs[slot] = holder.getRefs()[holderOffset];
    }

    public Object stackToObject(Object ref, long primitive)
    {
        return ref;
    }

    public void objectToStack(Object[] refs, long[] primitives, int slot, Object value)
    {
        refs[slot] = value;
    }

    public Object locationToObject(LocationsHolder holder, int holderOffset)
    {
        return holder.getRefs()[holderOffset];
    }

    public void objectToLocation(LocationsHolder holder, int holderOffset, Object obj)
    {
        holder.getRefs()[holderOffset] = obj;
    }

    public abstract Type getDirectBaseClass();

    public abstract BACILMethod getMemberMethod(String name, MethodDefSig signature);

    public abstract boolean isByRef();

    public abstract boolean isPinned();

    public abstract List<CustomMod> getMods();

    public boolean isPrimitiveStorage()
    {
        return false;
    }

    public TypedField getTypedField(int index)
    {
        return allFields[index];
    }

    public void staticFieldToStackVar(TypedField field, Object[] refs, long[] primitives, int slot)
    {
        staticFieldsDescriptor.locationToStack(staticFieldsHolder, field.getOffset(), refs, primitives, slot);
    }

    public void staticFieldFromStackVar(TypedField field, Object ref, long primitive)
    {
        staticFieldsDescriptor.stackToLocation(staticFieldsHolder, field.getOffset(), ref, primitive);
    }

    public void instanceFieldToStackVar(StaticObject object, TypedField field, Object[] refs, long[] primitives, int slot)
    {
        instanceFieldsDescriptor.locationToStack(object.getFieldsHolder(), field.getOffset(), refs, primitives, slot);
    }

    public void instanceFieldFromStackVar(StaticObject object, TypedField field, Object ref, long primitive)
    {
        instanceFieldsDescriptor.stackToLocation(object.getFieldsHolder(), field.getOffset(), ref, primitive);
    }



    public TypedField getTypedField(CLITablePtr token, CLIComponent callingComponent)
    {
        if (token.getTableId() == CLITableConstants.CLI_TABLE_MEMBER_REF) {
            CLIMemberRefTableRow row = callingComponent.getTableHeads().getMemberRefTableHead().skip(token);
            return getTypedField(row.getName().read(callingComponent.getStringHeap()));
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new BACILInternalError("Failed to find typed field from token " + token);
    }

    public LocationReference getStaticFieldReference(TypedField field)
    {
        return new LocationReference(staticFieldsHolder, field.getOffset(), field.getType());
    }

    public LocationReference getInstanceFieldReference(TypedField field, StaticObject object)
    {
        return new LocationReference(object.getFieldsHolder(), field.getOffset(), field.getType());
    }


    public TypedField getTypedField(String name)
    {
        for(TypedField f : allFields)
        {
            if(f.getName().equals(name))
            {
                return f;
            }
        }
        return null;
    }

    public VtableSlotIdentity[] getVtableSlots() {
        return null;
    }

    public BACILMethod[] getVtable() {
        return null;
    }

}
