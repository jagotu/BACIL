package com.vztekoverflow.cil.parser.bytecode;

/** A collection of utility methods for reading integers from byte arrays. */
public final class ByteUtils {

  /**
   * Gets a signed 1-byte value.
   *
   * @param data the array containing the data
   * @param offset the start index of the value to retrieve
   * @return the signed 1-byte value at index {@code bci} in array {@code data}
   */
  public static byte getByte(byte[] data, int offset) {
    return data[offset];
  }

  /**
   * Gets a signed 2-byte value.
   *
   * @param data the array containing the data
   * @param offset the start index of the value to retrieve
   * @return the signed 2-byte value at index {@code bci} in array {@code data}
   */
  public static short getShort(byte[] data, int offset) {
    short result = (short) (data[offset] & 0xff);
    result |= (data[offset + 1] & 0xff) << 8;
    return result;
  }

  /**
   * Gets a signed 4-byte value.
   *
   * @param data the array containing the data
   * @param offset the start index of the value to retrieve
   * @return the signed 4-byte value at index {@code bci} in array {@code data}
   */
  public static int getInt(byte[] data, int offset) {
    int result = (data[offset] & 0xff);
    result |= (data[offset + 1] & 0xff) << 8;
    result |= (data[offset + 2] & 0xff) << 16;
    result |= (data[offset + 3] & 0xff) << 24;
    return result;
  }

  /**
   * Gets a signed 8-byte value.
   *
   * @param data the array containing the data
   * @param offset the start index of the value to retrieve
   * @return the signed 8-byte value at index {@code bci} in array {@code data}
   */
  public static long getLong(byte[] data, int offset) {
    long result = (data[offset] & 0xff);
    result |= (data[offset + 1] & 0xff) << 8;
    result |= (data[offset + 2] & 0xff) << 16;
    result |= ((long) data[offset + 3] & 0xff) << 24;
    result |= ((long) data[offset + 4] & 0xff) << 32;
    result |= ((long) data[offset + 5] & 0xff) << 40;
    result |= ((long) data[offset + 6] & 0xff) << 48;
    result |= ((long) data[offset + 7] & 0xff) << 56;
    return result;
  }

  /**
   * Gets an unsigned 2-byte value. Returns an int cause Java doesn't support unsigned shorts.
   *
   * @param data the array containing the data
   * @param offset the start index of the value to retrieve
   * @return the unsigned 2-byte value at index {@code bci} in array {@code data}
   */
  public static int getUShort(byte[] data, int offset) {
    int result = (data[offset] & 0xff);
    result |= (data[offset + 1] & 0xff) << 8;
    return result;
  }

  /**
   * Gets an unsigned 4-byte value. Returns a long cause Java doesn't support unsigned shorts.
   *
   * @param data the array containing the data
   * @param offset the start index of the value to retrieve
   * @return the unsigned 4-byte value at index {@code bci} in array {@code data}
   */
  public static long getUInt(byte[] data, int offset) {
    long result = (data[offset] & 0xff);
    result |= (data[offset + 1] & 0xff) << 8;
    result |= (data[offset + 2] & 0xff) << 16;
    result |= ((long) data[offset + 3] & 0xff) << 24;
    return result;
  }
}
