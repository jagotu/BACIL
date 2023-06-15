package com.vztekoverflow.cilostazol.utils;

public class Wrapper<T> {
  T ref;

  public Wrapper(T value) {
    ref = value;
  }

  public T get() {
    return ref;
  }

  public void set(T value) {
    ref = value;
  }

  public String toString() {
    return ref.toString();
  }
}
