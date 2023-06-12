package com.vztekoverflow.cilostazol;

import java.util.ResourceBundle;

public class CILOSTAZOLBundle {
  // region Singleton
  private static CILOSTAZOLBundle instance = null;
  final ResourceBundle bundle;
  // endregion
  private final String bundleName = "CILOSTAZOLBundle";

  private CILOSTAZOLBundle() {
    bundle = ResourceBundle.getBundle(bundleName);
  }

  private static CILOSTAZOLBundle getInstance() {
    if (instance == null) instance = new CILOSTAZOLBundle();

    return instance;
  }

  public static String message(String name, Object... args) {
    return String.format(getInstance().bundle.getString(name), args);
  }
}
