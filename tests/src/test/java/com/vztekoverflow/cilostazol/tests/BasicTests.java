package com.vztekoverflow.cilostazol.tests;

import com.vztekoverflow.cilostazol.launcher.CILOSTAZOLLauncher;

public class BasicTests extends TestBase {
  public void test() {}

  public void _testInitial() {
    CILOSTAZOLLauncher launcher = new CILOSTAZOLLauncher();
    launcher.test(new String[] {getDllPath("Initial").toString()});
  }
}
