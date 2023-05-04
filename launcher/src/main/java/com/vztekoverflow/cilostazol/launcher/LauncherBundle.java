package com.vztekoverflow.cilostazol.launcher;

import java.util.ResourceBundle;

public class LauncherBundle {

    //region Singleton
    private static LauncherBundle instance = null;

    private static LauncherBundle getInstance()
    {
        if (instance == null)
            instance = new LauncherBundle();

        return instance;
    }
    //endregion

    private final String bundleName = "LauncherBundle";
    final ResourceBundle bundle;

    private LauncherBundle()
    {
        bundle = ResourceBundle.getBundle(bundleName);
    }

    public static String message(String name, Object... args) {
        return String.format(getInstance().bundle.getString(name), args);
    }

}