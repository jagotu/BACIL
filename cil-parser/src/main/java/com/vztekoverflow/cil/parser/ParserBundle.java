package com.vztekoverflow.cil.parser;

import java.util.ResourceBundle;

public class ParserBundle {

    //region Singleton
    private static ParserBundle instance = null;

    private static ParserBundle getInstance()
    {
        if (instance == null)
            instance = new ParserBundle();

        return instance;
    }
    //endregion

    private final String bundleName = "ParserBundle";
    final ResourceBundle bundle;

    private ParserBundle()
    {
        bundle = ResourceBundle.getBundle(bundleName);
    }

    public static String message(String name, Object... args) {
        return String.format(getInstance().bundle.getString(name), args);
    }

}