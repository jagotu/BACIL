package com.vztekoverflow.cilostazol.nodes.nodeized;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.cilostazol.runtime.symbols.TypeSymbol;

public abstract class NodeizedNodeBase extends Node {

  /**
   * Execute the node on the given evaluation stack.
   *
   * @param frame the frame of the currently executing guest language method
   * @return the new evaluation stack top
   */
  public abstract int execute(VirtualFrame frame, TypeSymbol[] taggedFrame);
}
