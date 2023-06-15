package com.vztekoverflow.bacil.runtime.types;

/** Helper methods for converting between various integer representations. */
public final class TypeHelpers {

  /** Sign-extend the 8 least significant bits of the value. */
  public static long signExtend8(long value) {
    return (byte) value;
  }

  /** Sign-extend the 16 least significant bits of the value. */
  public static long signExtend16(long value) {
    return (short) value;
  }

  /** Sign-extend the 32 least significant bits of the value. */
  public static long signExtend32(long value) {
    return (int) value;
  }

  /**
   * Sign-extend the 8 least significant bits of the value to the 32 least significant bits, leaving
   * the 32 most significant bits zero.
   */
  public static long signExtend8to32(long value) {
    return truncate32(signExtend8(value));
  }

  /**
   * Sign-extend the 16 least significant bits of the value to the 32 least significant bits,
   * leaving the 32 most significant bits zero.
   */
  public static long signExtend16to32(long value) {
    return truncate32(signExtend16(value));
  }

  /** Zero-extend the 8 least significant bits of the value. */
  public static long zeroExtend8(long value) {
    return value & 0xFFL;
  }

  /** Zero-extend the 16 least significant bits of the value. */
  public static long zeroExtend16(long value) {
    return value & 0xFFFFL;
  }

  /** Zero-extend the 32 least significant bits of the value. */
  public static long zeroExtend32(long value) {
    return value & 0xFFFFFFFFL;
  }

  /** Truncate the value to the 8 least significant bits. */
  public static long truncate8(long value) {
    return value & 0xFFL;
  }

  /** Truncate the value to the 16 least significant bits. */
  public static long truncate16(long value) {
    return value & 0xFFFFL;
  }

  /** Truncate the value to the 32 least significant bits. */
  public static long truncate32(long value) {
    return value & 0xFFFFFFFFL;
  }
}
