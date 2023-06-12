package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;

/**
 * Represents a Truffle node that is aware of the BACIL evaluation stack, taking {@code long[]
 * primitives} and {@code Object[] refs} as explicit arguments.
 */
public abstract class EvaluationStackAwareNode extends Node {

  /**
   * Execute the node on the given evaluation stack.
   *
   * @param frame the frame of the currently executing guest language method
   * @param primitives array representing the primitives on the stack
   * @param refs array representing references and {@link EvaluationStackPrimitiveMarker}s on the
   *     stack
   * @return the new evaluation stack top
   */
  public abstract int execute(VirtualFrame frame, long[] primitives, Object[] refs);
}
