package com.vztekoverflow.cilostazol.tests;

import java.io.IOException;
import java.io.OutputStream;

public class TestOutputStream extends OutputStream {
  private final OutputStream delegate;
  private boolean quiet;

  public TestOutputStream(OutputStream delegate) {
    this.delegate = delegate;
  }

  public void setQuiet(boolean quiet) {
    this.quiet = quiet;
  }

  @Override
  public void write(int b) throws IOException {
    if (!quiet) {
      delegate.write(b);
    }
  }
}
