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
import com.vztekoverflow.bacil.runtime.locations.LocationsDescriptor;
import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;

import java.util.List;

/**
 * A generic BACIL type. A combination of methods, static and instance fields,
 * type metadata and information on how to move the type between storage states.
 *
 * Every typed value can exist in three storage states: on the evaluation stack,
 * in a location (local variables, arguments, fields) and as a Java object (used for Truffle arguments).
 * Each type must implement transitions from every storage states to every other state.
 *
 */
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

    /**
     * Initialize the type when first used, calling the type initializer etc.
     */
    public void init()
    {
        inited = true;
    }

    /**
     * Get an initial (default) value for this type.
     */
    public Object initialValue()
    {
        return null;
    }

    /**
     * Move a value of this type from the evaluation stack to a location.
     * @param holder location holder to store to
     * @param holderOffset offset of the location to store to
     * @param ref the reference from the evaluation stack
     * @param primitive the primitive from the evaluation stack
     */
    public void stackToLocation(LocationsHolder holder, int holderOffset, Object ref, long primitive)
    {
        holder.getRefs()[holderOffset] = ref;
    }

    /**
     * Move a value of this type from a location to the evaluation stack.
     * @param holder location holder to load from
     * @param holderOffset offset of the location to load from
     * @param refs references currently on the evaluation stack
     * @param primitives primitives currently on the evaluation stack
     * @param slot the evaluation stack slot to put the value into
     */
    public void locationToStack(LocationsHolder holder, int holderOffset, Object[] refs, long[] primitives, int slot)
    {
        refs[slot] = holder.getRefs()[holderOffset];
    }

    /**
     * Get a value of this type from the evaluation stack as a Java object.
     * @param ref the reference from the evaluation stack
     * @param primitive the primitive from the evaluation stack
     * @return the value as a Java object
     */
    public Object stackToObject(Object ref, long primitive)
    {
        return ref;
    }

    /**
     * Put an object holding a value of this type on the evaluation stack.
     * @param refs references currently on the evaluation stack
     * @param primitives primitives currently on the evaluation stack
     * @param slot the evaluation stack slot to store the value into
     * @param value an object holding the value
     */
    public void objectToStack(Object[] refs, long[] primitives, int slot, Object value)
    {
        refs[slot] = value;
    }

    /**
     * Get a value of this type from a location as a Java object.
     * @param holder location holder to load from
     * @param holderOffset offset of the location to load from
     * @return the value as a Java object
     */
    public Object locationToObject(LocationsHolder holder, int holderOffset)
    {
        return holder.getRefs()[holderOffset];
    }

    /**
     * Put an object holding a value of this type into a location.
     * @param holder location holder to store to
     * @param holderOffset offset of the location to store to
     * @param value an object holding the value
     */
    public void objectToLocation(LocationsHolder holder, int holderOffset, Object value)
    {
        holder.getRefs()[holderOffset] = value;
    }

    /**
     * Get the inheritance parent of this class (or null if inheritance root).
     */
    public abstract Type getDirectBaseClass();

    /**
     * Get a method defined by this type.
     * @param name name of the method to find
     * @param signature signature of the method to find
     * @return the found method or null
     */
    public abstract BACILMethod getMemberMethod(String name, MethodDefSig signature);

    /**
     * Check whether this type should be passed by reference (I.12.4.1.5.2 By-reference parameters).
     */
    public abstract boolean isByRef();

    /**
     * Check whether this type is pinned (II.7.1.2 pinned).
     */
    public abstract boolean isPinned();

    /**
     * List of custom modifiers (II.23.2.7 CustomMod).
     */
    public abstract List<CustomMod> getMods();

    /**
     * Whether this type values are stored as a primitive or a reference.
     */
    public boolean isPrimitiveStorage()
    {
        return false;
    }

    /**
     * Get a field defined in this type by index.
     */
    public TypedField getTypedField(int index)
    {
        return allFields[index];
    }

    /**
     * Load a value from a static field defined in this type to the evaluation stack.
     * @param field the field to load from
     * @param refs references currently on the evaluation stack
     * @param primitives primitives currently on the evaluation stack
     * @param slot the evaluation stack slot to put the value into
     */
    public void staticFieldToStackVar(TypedField field, Object[] refs, long[] primitives, int slot)
    {
        staticFieldsDescriptor.locationToStack(staticFieldsHolder, field.getOffset(), refs, primitives, slot);
    }

    /**
     * Store a value from the evaluation stack to a static field defined in this type.
     * @param field the field to śtore to
     * @param ref the reference from the evaluation stack
     * @param primitive the primitive from the evaluation stack
     */
    public void staticFieldFromStackVar(TypedField field, Object ref, long primitive)
    {
        staticFieldsDescriptor.stackToLocation(staticFieldsHolder, field.getOffset(), ref, primitive);
    }

    /**
     * Load a value from an instance field defined in this type to the evaluation stack.
     * @param object the object instance to load the value from
     * @param field the field to load from
     * @param refs references currently on the evaluation stack
     * @param primitives primitives currently on the evaluation stack
     * @param slot the evaluation stack slot to put the value into
     */
    public void instanceFieldToStackVar(StaticObject object, TypedField field, Object[] refs, long[] primitives, int slot)
    {
        instanceFieldsDescriptor.locationToStack(object.getFieldsHolder(), field.getOffset(), refs, primitives, slot);
    }

    /**
     * Store a value from the evaluation stack to an instance field defined in this type.
     * @param object the object instance to store the value to
     * @param field the field to store to
     * @param ref the reference from the evaluation stack
     * @param primitive the primitive from the evaluation stack
     */
    public void instanceFieldFromStackVar(StaticObject object, TypedField field, Object ref, long primitive)
    {
        instanceFieldsDescriptor.stackToLocation(object.getFieldsHolder(), field.getOffset(), ref, primitive);
    }



    /**
     * Get a field defined in this type by a MemberRef token.
     */
    public TypedField getTypedField(CLITablePtr token, CLIComponent callingComponent)
    {
        if (token.getTableId() == CLITableConstants.CLI_TABLE_MEMBER_REF) {
            CLIMemberRefTableRow row = callingComponent.getTableHeads().getMemberRefTableHead().skip(token);
            return getTypedField(row.getName().read(callingComponent.getStringHeap()));
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new BACILInternalError("Failed to find typed field from token " + token);
    }

    /**
     * Get a managed reference ({@code &}) to a static field defined in this type.
     * @param field the field to reference
     * @return a managed reference to the field represented by a {@link LocationReference}
     */
    public LocationReference getStaticFieldReference(TypedField field)
    {
        return new LocationReference(staticFieldsHolder, field.getOffset(), field.getType());
    }

    /**
     * Get a managed reference ({@code &}) to an instance field defined in this type.
     * @param field the field to reference
     * @param object the object to reference
     * @return a managed reference to the field represented by a {@link LocationReference}
     */
    public LocationReference getInstanceFieldReference(TypedField field, StaticObject object)
    {
        return new LocationReference(object.getFieldsHolder(), field.getOffset(), field.getType());
    }


    /**
     * Get a field defined in this type by name.
     */
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

    /**
     * Get the vtable slot identities defined in this type.
     * Each vtable slot represents one virtual method defined in this type and its parents.
     */
    public VtableSlotIdentity[] getVtableSlots() {
        return null;
    }

    /**
     * Get the vtable slots defined in this type.
     * Each vtable slot contains the implementation of a virtual method defined in this type and its parents.
     */
    public BACILMethod[] getVtable() {
        return null;
    }

}
