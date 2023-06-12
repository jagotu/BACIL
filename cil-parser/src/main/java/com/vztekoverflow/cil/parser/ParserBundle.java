package com.vztekoverflow.cil.parser;

import java.util.ResourceBundle;

public class ParserBundle {

  // region Singleton
  private static ParserBundle instance = null;
  final ResourceBundle bundle;
  // endregion
  private final String bundleName = "ParserBundle";
  private ParserBundle() {
    bundle = ResourceBundle.getBundle(bundleName);
  }

  private static ParserBundle getInstance() {
    if (instance == null) instance = new ParserBundle();

    return instance;
  }

  public static String message(String name, Object... args) {
    return String.format(getInstance().bundle.getString(name), args);
  }
}
