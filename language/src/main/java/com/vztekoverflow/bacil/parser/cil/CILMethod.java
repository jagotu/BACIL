package com.vztekoverflow.bacil.parser.cil;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.nodes.BytecodeNode;
import com.vztekoverflow.bacil.nodes.CILRootNode;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;
import com.vztekoverflow.bacil.parser.signatures.LocalVarSig;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.locations.LocationsDescriptor;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * Class representing a CIL method in an assembly. Holds resolved metadata like signature, location
 * definitions, the type it belongs to, etc.
 */
public class CILMethod implements BACILMethod {

  public static final int METHODATTRIBUTE_VIRTUAL = 0x0040;
  private static final byte CORILMETHOD_TINYFORMAT = 2;
  private static final byte CORILMETHOD_FATFORMAT = 3;
  private static final byte CORILMETHOD_INITLOCALS = 0x10;
  private static final byte CORILMETHOD_MORESECTS = 0x8;
  private static final short METHODIMPL_INTERNALCALL = 0x1000;
  private final CLIComponent component;
  private final CLIMethodDefTableRow methodDef;
  private final int ILflags;
  private final short maxStack;
  private final CallTarget callTarget;
  private final LocalVarSig localVarSig;
  private final MethodDefSig methodDefSig;
  private final String name;
  private final boolean initLocals;

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private final Type[] locationTypes;

  private final LocationsDescriptor locationDescriptor;

  private final int varsCount;
  private final int argsCount;

  private final Type definingType;

  /**
   * Create a CILMethod object representing the metadata of the specified method.
   *
   * @param methodDef the method token to read the information from
   * @param component the component containing this method
   * @param definingType the type defining this method
   */
  public CILMethod(CLIMethodDefTableRow methodDef, CLIComponent component, Type definingType) {
    // read metadata from the methodDef
    this.component = component;
    this.methodDef = methodDef;
    this.methodDefSig =
        MethodDefSig.read(methodDef.getSignature().read(component.getBlobHeap()), component);
    this.definingType = definingType;
    this.name = methodDef.getName().read(component.getStringHeap());

    ByteSequenceBuffer buf = component.getBuffer(methodDef.getRVA());
    byte firstByte = buf.getByte();

    final int size;

    if ((firstByte & 3) == CORILMETHOD_TINYFORMAT) {
      // II.25.4.2 Tiny format
      this.ILflags = CORILMETHOD_TINYFORMAT;
      this.maxStack = 8;
      this.localVarSig = null;
      this.initLocals = false;
      size = (firstByte >> 2) & 0xFF;

    } else if ((firstByte & 3) == CORILMETHOD_FATFORMAT) {
      // II.25.4.3 Fat format
      short firstWord = (short) (firstByte | (buf.getByte() << 8));
      this.ILflags = firstWord & 0xFFF;
      byte headerSize = (byte) (firstWord >> 12);

      if (headerSize != 3) {
        throw new BACILParserException("Unexpected CorILMethod fat header size");
      }

      this.maxStack = buf.getShort();
      size = buf.getInt();

      int localVarSigTok = buf.getInt();
      if (localVarSigTok == 0) {
        this.localVarSig = null;
      } else {
        CLITablePtr localVarSigPtr = CLITablePtr.fromToken(localVarSigTok);
        byte[] localVarSig =
            component
                .getTableHeads()
                .getStandAloneSigTableHead()
                .skip(localVarSigPtr)
                .getSignature()
                .read(component.getBlobHeap());
        this.localVarSig = LocalVarSig.read(localVarSig, component);
      }

      initLocals = (ILflags & CORILMETHOD_INITLOCALS) != 0;
      if ((ILflags & CORILMETHOD_MORESECTS) != 0) {
        throw new BACILParserException(
            "Multiple sections in CIL method header not supported. Does the method contain a try block?");
      }

    } else {
      throw new BACILParserException("Invalid CorILMethod flags");
    }

    final byte[] body = buf.subSequence(size).toByteArray();

    // Create a BytecodeNode to represent the body
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    CILRootNode rootNode = new CILRootNode(frameDescriptor, new BytecodeNode(this, body));
    this.callTarget = rootNode.getCallTarget();

    int explicitArgsStart = 0;
    if (methodDefSig.isHasThis() && !methodDefSig.isExplicitThis()) {
      if (definingType == null) {
        throw new BACILInternalError("Instance method constructor called without definingType!");
      }
      explicitArgsStart = 1;
    }

    argsCount = methodDefSig.getParamTypes().length + explicitArgsStart;

    if (localVarSig != null) {
      varsCount = localVarSig.getVarTypes().length;
    } else {
      varsCount = 0;
    }

    locationTypes = new Type[varsCount + argsCount];

    for (int i = 0; i < varsCount; i++) {
      locationTypes[i] = localVarSig.getVarTypes()[i];
    }

    if (methodDefSig.isHasThis() && !methodDefSig.isExplicitThis()) {
      // I.8.6.1.5
      // if the calling convention specifies this is an instance method and the owning method
      // definition belongs to a type T then the type of the this pointer is
      // given by the first parameter signature, if the calling convention is instance
      // explicit (§II.15.3),
      // o inferred as &T, if T is a value type and the method definition is non-virtual
      //  (§I.8.9.7),
      // o inferred as “boxed” T, if T is a value type and the method definition is virtual
      //  (this includes method definitions from an interface implemented by T)
      //  (§I.8.9.7),
      // o inferred as T, otherwise

      if (isVirtual()) {
        locationTypes[varsCount] = definingType;
      } else {
        locationTypes[varsCount] = definingType.getThisType();
      }
    }

    for (int i = explicitArgsStart; i < argsCount; i++) {
      locationTypes[varsCount + i] = methodDefSig.getParamTypes()[i - explicitArgsStart];
    }

    this.locationDescriptor = new LocationsDescriptor(locationTypes);
  }

  /**
   * Check whether the method definition specifies an {@code internalcall} method. {@code
   * internalcall} methods are used by the standard library for methods implemented natively.
   *
   * @param method the method definition to check
   * @return whether the defined method is {@code internalcall} or not.
   */
  public static boolean isInternalCall(CLIMethodDefTableRow method) {
    return (method.getImplFlags() & METHODIMPL_INTERNALCALL) == METHODIMPL_INTERNALCALL;
  }

  /** Get the component where the method is defined in. */
  public CLIComponent getComponent() {
    return component;
  }

  /** Get the call target which executes this method. */
  @Override
  public CallTarget getMethodCallTarget() {
    return callTarget;
  }

  /** Get the type of the return value of the method. */
  @Override
  public Type getRetType() {
    return methodDefSig.getRetType();
  }

  /** Get the evaluation stack size required by this method. */
  public short getMaxStack() {
    return maxStack;
  }

  /**
   * Get the parsed method signature. The signature describes the arguments, return value type,
   * calling convention etc.
   */
  @Override
  public MethodDefSig getSignature() {
    return methodDefSig;
  }

  /** Get the name of the method. */
  @Override
  public String getName() {
    return name;
  }

  /** Get whether the method is defined as a virtual method. */
  @Override
  public boolean isVirtual() {
    return (methodDef.getFlags() & METHODATTRIBUTE_VIRTUAL) == METHODATTRIBUTE_VIRTUAL;
  }

  /**
   * Get whether the method requests locals initialization.
   *
   * <p>II.25.4.4: Call default constructor on all local variables.
   */
  public boolean isInitLocals() {
    return initLocals;
  }

  /** Get types of all locations (arguments and variables combined). */
  @Override
  public Type[] getLocationsTypes() {
    return locationTypes;
  }

  /** Get count of method local variables. */
  @Override
  public int getVarsCount() {
    return varsCount;
  }

  /** Get count of method arguments. */
  @Override
  public int getArgsCount() {
    return argsCount;
  }

  /** Returns a string representation of the method, in a format of type::methodName. */
  @Override
  public String toString() {
    return definingType.toString() + "::" + getName();
  }

  /** Get the type in which this method is defined. */
  @Override
  public Type getDefiningType() {
    return definingType;
  }

  /** Get a descriptor for all locations of this method (arguments and variables combined). */
  public LocationsDescriptor getLocationDescriptor() {
    return locationDescriptor;
  }
}
