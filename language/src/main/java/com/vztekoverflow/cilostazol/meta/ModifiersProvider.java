package com.vztekoverflow.cilostazol.meta;

// TODO
public interface ModifiersProvider {

  default boolean isInterface() {
    return false;
  }

  default boolean isStatic() {
    return false;
  }

  default boolean isPublic() {
    return false;
  }

  default boolean isPrivate() {
    return false;
  }

  default boolean isProtected() {
    return false;
  }

  default boolean isAbstract() {
    return false;
  }
}
