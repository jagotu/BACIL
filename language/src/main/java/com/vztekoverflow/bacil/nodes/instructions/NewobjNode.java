package com.vztekoverflow.bacil.nodes.instructions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.vztekoverflow.bacil.nodes.BytecodeNode;
import com.vztekoverflow.bacil.nodes.EvaluationStackAwareNode;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.LocationReference;
import com.vztekoverflow.bacil.runtime.StaticObject;
import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.CLIType;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * A Truffle node representing the newobj instruction. (III.4.21 newobj – create a new object)
 * Stores the resolved type and constructor method from the instruction.
 */
public class NewobjNode extends EvaluationStackAwareNode {

  protected final BACILMethod method;
  protected final int top;
  protected final CLIType objType;
  @Child private DirectCallNode directCallNode;

  /**
   * Create a new node representing the newobj instruction (constructor call).
   *
   * @param method the constructor method
   * @param top stack top when running this instruction
   */
  public NewobjNode(BACILMethod method, int top) {
    this.method = method;
    this.top = top;
    this.objType = (CLIType) method.getDefiningType();
    directCallNode = DirectCallNode.create(this.method.getMethodCallTarget());
  }

  @Override
  public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
    Object retval;
    Object arg;
    // Create a new storage for the type
    if (objType.getStorageType() == Type.STORAGE_VALUETYPE) {
      LocationsHolder storage =
          LocationsHolder.forDescriptor(objType.getInstanceFieldsDescriptor());
      arg = new LocationReference(storage, 0, 0, objType);
      retval = storage;
    } else {
      retval = new StaticObject(objType);
      arg = retval;
    }

    // Call the constructor with the new object as "this"
    Object[] args = BytecodeNode.prepareArgs(primitives, refs, top, method, 1);
    args[0] = arg;
    directCallNode.call(args);

    // Put the new object on the evaluation stack as a result
    final int firstArg = top - method.getArgsCount() + 1;
    refs[firstArg] = retval;
    return firstArg + 1;
  }
}
