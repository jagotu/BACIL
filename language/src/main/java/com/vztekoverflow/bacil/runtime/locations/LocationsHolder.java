package com.vztekoverflow.bacil.runtime.locations;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.runtime.types.CLIType;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * Class holding values stored in locations. The uses are described in I.12.1.6.1 Homes for values.
 */
public final class LocationsHolder {

  private final Object[] refs;
  private final long[] primitives;

  /**
   * Create a new locations holder with the specified amount of references and primitives.
   *
   * @param refCount amount of references to store
   * @param primitiveCount amount of primitives to store
   */
  private LocationsHolder(int refCount, int primitiveCount) {
    refs = new Object[refCount];
    primitives = new long[primitiveCount];
  }

  /**
   * Create a new locations holder holding values specified by the descriptor.
   *
   * @param descriptor the locations descriptor for which to hold the values
   */
  public static LocationsHolder forDescriptor(LocationsDescriptor descriptor) {
    return new LocationsHolder(descriptor.getRefCount(), descriptor.getPrimitiveCount());
  }

  /**
   * Create a new locations holder holding elements of an array.
   *
   * @param type the type of the element
   * @param count array size
   */
  public static LocationsHolder forArray(Type type, int count) {
    switch (type.getStorageType()) {
      case Type.STORAGE_PRIMITIVE:
        return new LocationsHolder(0, count);
      case Type.STORAGE_REF:
        return new LocationsHolder(count, 0);
      case Type.STORAGE_VALUETYPE:
        CLIType cliType = (CLIType) type;
        type.init();
        return new LocationsHolder(
            count * cliType.getInstanceFieldsDescriptor().getRefCount(),
            count * cliType.getInstanceFieldsDescriptor().getPrimitiveCount());
      default:
        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new BACILInternalError("Invalid storage type");
    }
  }

  /** Get the array of stored references. */
  public Object[] getRefs() {
    return refs;
  }

  /** Get the array of stored primitives. */
  public long[] getPrimitives() {
    return primitives;
  }
}
