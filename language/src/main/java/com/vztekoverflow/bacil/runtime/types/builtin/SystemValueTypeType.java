package com.vztekoverflow.bacil.runtime.types.builtin;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.LocationReference;
import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.ByRefWrapped;
import com.vztekoverflow.bacil.runtime.types.CLIType;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypedField;

/**
 * Implementation of the System.ValueType type. Replaces the reference-style operations of {@link
 * Type} with stubs so that children have to override them with proper primitive-style handlers.
 */
public class SystemValueTypeType extends CLIType {
  public SystemValueTypeType(CLITypeDefTableRow type, CLIComponent component) {
    super(type, component);
  }

  @Override
  public int getStorageType() {
    return STORAGE_VALUETYPE;
  }

  @Override
  public Object initialValue() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void stackToLocation(
      LocationsHolder holder, int primitiveOffset, int refOffset, Object ref, long primitive) {
    objectToLocation(holder, primitiveOffset, refOffset, ref);
  }

  @Override
  public void locationToStack(
      LocationsHolder holder,
      int primitiveOffset,
      int refOffset,
      Object[] refs,
      long[] primitives,
      int slot) {
    refs[slot] = locationToObject(holder, primitiveOffset, refOffset);
  }

  @Override
  public Object stackToObject(Object ref, long primitive) {
    return ref;
  }

  @Override
  public void objectToStack(Object[] refs, long[] primitives, int slot, Object value) {
    refs[slot] = value;
  }

  @Override
  public Object locationToObject(LocationsHolder holder, int primitiveOffset, int refOffset) {
    LocationsHolder val = LocationsHolder.forDescriptor(instanceFieldsDescriptor);
    System.arraycopy(
        holder.getPrimitives(),
        primitiveOffset,
        val.getPrimitives(),
        0,
        instanceFieldsDescriptor.getPrimitiveCount());
    System.arraycopy(
        holder.getRefs(), refOffset, val.getRefs(), 0, instanceFieldsDescriptor.getRefCount());
    return val;
  }

  @Override
  public void objectToLocation(
      LocationsHolder holder, int primitiveOffset, int refOffset, Object value) {
    LocationsHolder val = (LocationsHolder) value;
    System.arraycopy(
        val.getPrimitives(),
        0,
        holder.getPrimitives(),
        primitiveOffset,
        instanceFieldsDescriptor.getPrimitiveCount());
    System.arraycopy(
        val.getRefs(), 0, holder.getRefs(), refOffset, instanceFieldsDescriptor.getRefCount());
  }

  @Override
  public Type getThisType() {
    return new ByRefWrapped(this);
  }

  @Override
  public void instanceFieldFromStackVar(
      Object object, TypedField field, Object ref, long primitive) {
    LocationReference locationReference = (LocationReference) object;
    LocationsHolder holder = locationReference.getHolder();
    int i = field.getIndex();
    instanceFieldsDescriptor
        .getType(i)
        .stackToLocation(
            holder,
            locationReference.getPrimitiveOffset() + instanceFieldsDescriptor.getPrimitiveOffset(i),
            locationReference.getRefOffset() + instanceFieldsDescriptor.getRefOffset(i),
            ref,
            primitive);
  }

  @Override
  public void instanceFieldToStackVar(
      Object object, TypedField field, Object[] refs, long[] primitives, int slot) {
    // unfortunately we can load from both a managed pointer (type &) to a valuetype and a bare
    // valuetype instance

    if (object instanceof LocationReference) {
      LocationReference locationReference = (LocationReference) object;
      LocationsHolder holder = locationReference.getHolder();
      int i = field.getIndex();
      instanceFieldsDescriptor
          .getType(i)
          .locationToStack(
              holder,
              locationReference.getPrimitiveOffset()
                  + instanceFieldsDescriptor.getPrimitiveOffset(i),
              locationReference.getRefOffset() + instanceFieldsDescriptor.getRefOffset(i),
              refs,
              primitives,
              slot);
    } else {
      LocationsHolder holder = (LocationsHolder) object;
      instanceFieldsDescriptor.locationToStack(holder, field.getIndex(), refs, primitives, slot);
    }
  }

  @Override
  public LocationReference getInstanceFieldReference(TypedField field, Object object) {
    LocationReference locationReference = (LocationReference) object;
    LocationsHolder holder = locationReference.getHolder();
    int i = field.getIndex();
    return new LocationReference(
        holder,
        locationReference.getPrimitiveOffset() + instanceFieldsDescriptor.getPrimitiveOffset(i),
        locationReference.getRefOffset() + instanceFieldsDescriptor.getRefOffset(i),
        instanceFieldsDescriptor.getType(i));
  }
}
