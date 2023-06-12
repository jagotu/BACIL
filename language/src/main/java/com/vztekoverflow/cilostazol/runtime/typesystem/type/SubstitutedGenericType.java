package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import java.util.Arrays;

public class SubstitutedGenericType extends CLIType {
  protected final IType _constructedFrom;
  protected final IType _definition;
  protected final ISubstitution<IType> _substitution;

  public SubstitutedGenericType(
      CILOSTAZOLContext context,
      IType _constructedFrom,
      IType _definition,
      ISubstitution<IType> substitution) {
    super(context);
    this._constructedFrom = _constructedFrom;
    this._definition = _definition;
    this._substitution = substitution;
  }

  // region IType

  @Override
  public IType[] getTypeParameters() {
    return Arrays.stream(_constructedFrom.getTypeParameters())
        .map(x -> x.substitute(_substitution))
        .toArray(IType[]::new);
  }

  @Override
  public String getName() {
    return _definition.getName();
  }

  @Override
  public String getNamespace() {
    return _definition.getNamespace();
  }

  @Override
  public IType getDirectBaseClass() {
    return _constructedFrom.getDirectBaseClass().substitute(_substitution);
  }

  @Override
  public IType[] getInterfaces() {
    return Arrays.stream(_constructedFrom.getInterfaces())
        .map(x -> x.substitute(_substitution))
        .toArray(IType[]::new);
  }

  @Override
  public IMethod[] getMethods() {
    return Arrays.stream(_constructedFrom.getMethods())
        .map(x -> x.substitute(_substitution))
        .toArray(IMethod[]::new);
  }

  @Override
  public IMethod[] getVTable() {
    return Arrays.stream(_constructedFrom.getVTable())
        .map(x -> x.substitute(_substitution))
        .toArray(IMethod[]::new);
  }

  @Override
  public IField[] getFields() {
    return Arrays.stream(_constructedFrom.getFields())
        .map(x -> x.substitute(_substitution))
        .toArray(IField[]::new);
  }

  @Override
  public CLIComponent getCLIComponent() {
    return null;
  }

  @Override
  protected int getHierarchyDepth() {
    return 0;
  }

  @Override
  protected CLIType[] getSuperTypes() {
    return new CLIType[0];
  }

  @Override
  public IComponent getDefiningComponent() {
    return _constructedFrom.getDefiningComponent();
  }

  @Override
  public IType substitute(ISubstitution<IType> substitution) {
    return new SubstitutedGenericType(getContext(), this, _definition, substitution);
  }

  @Override
  public IType getDefinition() {
    return _definition;
  }

  @Override
  public IType getConstructedFrom() {
    return _constructedFrom;
  }

  @Override
  public boolean isInterface() {
    return super.isInterface();
  }

  @Override
  public boolean isAbstract() {
    return super.isAbstract();
  }

  // endregion
}
