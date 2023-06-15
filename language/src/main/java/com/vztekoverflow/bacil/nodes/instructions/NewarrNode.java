package com.vztekoverflow.bacil.nodes.instructions;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.nodes.EvaluationStackAwareNode;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.SZArray;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * A Truffle node representing the newarr instruction. (III.4.20 newarr – create a zero-based,
 * one-dimensional array) Stores the resolved element type from the instruction.
 */
public class NewarrNode extends EvaluationStackAwareNode {

  protected final Type elementType;
  private final int top;

  /**
   * Create a new node representing the newarr instruction.
   *
   * @param elementType the type of the element resolved from the instruction
   * @param top stack top when running this instruction
   */
  public NewarrNode(Type elementType, int top) {
    this.elementType = elementType;
    this.top = top;
  }

  @Override
  public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
    if (refs[top - 1] != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      throw new BACILInternalError("Only INT32 supported as SZArray length");
    }

    refs[top - 1] = new SZArray(elementType, (int) primitives[top - 1]);
    return top;
  }
}
