package com.vztekoverflow.cilostazol.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public abstract class CILNodeBase extends Node {
  public abstract Object execute(VirtualFrame frame);
}
