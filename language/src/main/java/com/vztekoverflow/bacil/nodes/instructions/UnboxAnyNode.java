package com.vztekoverflow.bacil.nodes.instructions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.nodes.EvaluationStackAwareNode;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * A Truffle node representing the unbox instruction. (III.4.33 unbox.any – convert boxed type to
 * value) Stores the resolved type from the instruction.
 */
public class UnboxAnyNode extends EvaluationStackAwareNode {

  private final Type type;
  private final int top;

  /**
   * Create a new node representing the Box instruction.
   *
   * @param type resolved type from the instruction
   * @param top stack top when running this instruction
   */
  public UnboxAnyNode(Type type, int top) {
    this.top = top;
    this.type = type;
  }

  @Override
  public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
    type.objectToStack(refs, primitives, top - 1, refs[top - 1]);

    return top;
  }
}
