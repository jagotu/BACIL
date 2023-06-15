package com.vztekoverflow.bacil.parser;

/** An interface for classes storing position information. */
public interface Positionable {
  /** Get the current position. */
  public int getPosition();

  /** Set a new position. */
  public void setPosition(int position);
}
