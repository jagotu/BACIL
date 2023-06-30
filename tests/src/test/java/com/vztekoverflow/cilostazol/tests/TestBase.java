package com.vztekoverflow.cilostazol.tests;

import junit.framework.TestCase;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class TestBase extends TestCase {
  protected static final String _directory = "src/test/resources/BasicTests";
  protected static final String _configuration = "Release";
  protected static final String _dotnetVersion = "net7.0";

  protected Path getDllPath(String projectName) {
    return Paths.get(
        _directory,
        projectName,
        String.format("bin/%s/%s", _configuration, _dotnetVersion),
        projectName + ".dll");
  }
}
