package com.vztekoverflow.bacil.nodes.instructions;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.nodes.EvaluationStackAwareNode;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.SZArray;
import com.vztekoverflow.bacil.runtime.types.CLIType;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * A Truffle node representing the stelem instruction. (III.4.26 stelem – store element to array)
 * Stores the resolved element type from the instruction.
 */
public class StelemNode extends EvaluationStackAwareNode {

  private final Type elementType;
  private final int top;
  private final int primitiveSize;
  private final int refSize;

  /**
   * Create a new node representing the stelem instruction.
   *
   * @param elementType resolved element type from the instruction
   * @param top stack top when running this instruction
   */
  public StelemNode(Type elementType, int top) {
    this.elementType = elementType;
    this.top = top;

    if (elementType.getStorageType() == Type.STORAGE_VALUETYPE) {
      CLIType cliType = (CLIType) elementType;
      primitiveSize = cliType.getInstanceFieldsDescriptor().getPrimitiveCount();
      refSize = cliType.getInstanceFieldsDescriptor().getRefCount();
    } else {
      primitiveSize = 1;
      refSize = 1;
    }
  }

  @Override
  public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
    // Breaks standard: We should also support native int as the index here, but for us
    // native int is 64-bit, and Java arrays only use 32-bit indexers.
    if (refs[top - 2] != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      throw new BACILInternalError("Only INT32 supported as SZArray index");
    }

    int index = (int) primitives[top - 2];
    SZArray array = (SZArray) refs[top - 3];
    elementType.stackToLocation(
        array.getFieldsHolder(),
        index * primitiveSize,
        index * refSize,
        refs[top - 1],
        primitives[top - 1]);
    return top - 3;
  }
}
