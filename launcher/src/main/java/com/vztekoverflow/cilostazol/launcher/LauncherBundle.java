package com.vztekoverflow.cilostazol.launcher;

import java.util.ResourceBundle;

public class LauncherBundle {

  // region Singleton
  private static LauncherBundle instance = null;
  final ResourceBundle bundle;
  // endregion
  private final String bundleName = "LauncherBundle";

  private LauncherBundle() {
    bundle = ResourceBundle.getBundle(bundleName);
  }

  private static LauncherBundle getInstance() {
    if (instance == null) instance = new LauncherBundle();

    return instance;
  }

  public static String message(String name, Object... args) {
    return String.format(getInstance().bundle.getString(name), args);
  }
}
