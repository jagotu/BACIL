package com.vztekoverflow.bacil.runtime.types;

import junit.framework.TestCase;

public class TypeHelpersTest extends TestCase {

  public void testSignExtend8() {
    assertEquals(0x18, TypeHelpers.signExtend8(0x18L));
    assertEquals(-104, TypeHelpers.signExtend8(0x98L));
    assertEquals(0x18, TypeHelpers.signExtend8(0xFF18L));
    assertEquals(-104, TypeHelpers.signExtend8(0xFF98L));
    assertEquals(0x18, TypeHelpers.signExtend8(0xFFFFFFFFFFFFFF18L));
    assertEquals(-104, TypeHelpers.signExtend8(0xFFFFFFFFFFFFFF98L));
  }

  public void testSignExtend16() {
    assertEquals(0x18, TypeHelpers.signExtend16(0x18L));
    assertEquals(0x1818, TypeHelpers.signExtend16(0x1818L));
    assertEquals(-26600, TypeHelpers.signExtend16(0x9818L));
    assertEquals(0x18, TypeHelpers.signExtend16(0xFFFFFFFFFFFF0018L));
    assertEquals(0x1818, TypeHelpers.signExtend16(0xFFFFFFFFFFFF1818L));
    assertEquals(-26600, TypeHelpers.signExtend16(0xFFFFFFFFFFFF9818L));
  }

  public void testSignExtend32() {
    assertEquals(0x18L, TypeHelpers.signExtend32(0x18L));
    assertEquals(0x1818L, TypeHelpers.signExtend32(0x1818L));
    assertEquals(0x181818L, TypeHelpers.signExtend32(0x181818L));
    assertEquals(0x18181818L, TypeHelpers.signExtend32(0x18181818L));
    assertEquals(-1743251432L, TypeHelpers.signExtend32(0x98181818L));
    assertEquals(0x18L, TypeHelpers.signExtend32(0xFFFFFFFF00000018L));
    assertEquals(0x1818L, TypeHelpers.signExtend32(0xFFFFFFFF00001818L));
    assertEquals(0x181818L, TypeHelpers.signExtend32(0xFFFFFFFF00181818L));
    assertEquals(0x18181818L, TypeHelpers.signExtend32(0xFFFFFFFF18181818L));
    assertEquals(-1743251432L, TypeHelpers.signExtend32(0xFFFFFFFF98181818L));
  }

  public void testZeroExtend8() {
    assertEquals(0x18L, TypeHelpers.zeroExtend8(0x18L));
    assertEquals(0x98L, TypeHelpers.zeroExtend8(0x98L));
    assertEquals(0x18L, TypeHelpers.zeroExtend8(0xFF18L));
    assertEquals(0x98L, TypeHelpers.zeroExtend8(0xFF98L));
    assertEquals(0x18L, TypeHelpers.zeroExtend8(0xFFFFFFFFFFFFFF18L));
    assertEquals(0x98L, TypeHelpers.zeroExtend8(0xFFFFFFFFFFFFFF98L));
  }

  public void testZeroExtend16() {
    assertEquals(0x18L, TypeHelpers.zeroExtend16(0x18L));
    assertEquals(0x1818L, TypeHelpers.zeroExtend16(0x1818L));
    assertEquals(0x9818L, TypeHelpers.zeroExtend16(0x9818L));
    assertEquals(0x18L, TypeHelpers.zeroExtend16(0xFFFFFFFFFFFF0018L));
    assertEquals(0x1818L, TypeHelpers.zeroExtend16(0xFFFFFFFFFFFF1818L));
    assertEquals(0x9818L, TypeHelpers.zeroExtend16(0xFFFFFFFFFFFF9818L));
  }

  public void testZeroExtend32() {
    assertEquals(0x18L, TypeHelpers.zeroExtend32(0x18L));
    assertEquals(0x1818L, TypeHelpers.zeroExtend32(0x1818L));
    assertEquals(0x181818L, TypeHelpers.zeroExtend32(0x181818L));
    assertEquals(0x18181818L, TypeHelpers.zeroExtend32(0x18181818L));
    assertEquals(0x98181818L, TypeHelpers.zeroExtend32(0x98181818L));
    assertEquals(0x18L, TypeHelpers.zeroExtend32(0xFFFFFFFF00000018L));
    assertEquals(0x1818L, TypeHelpers.zeroExtend32(0xFFFFFFFF00001818L));
    assertEquals(0x181818L, TypeHelpers.zeroExtend32(0xFFFFFFFF00181818L));
    assertEquals(0x18181818L, TypeHelpers.zeroExtend32(0xFFFFFFFF18181818L));
    assertEquals(0x98181818L, TypeHelpers.zeroExtend32(0xFFFFFFFF98181818L));
  }
}
