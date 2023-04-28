package com.vztekoverflow.cilostazol;

import java.util.ResourceBundle;

public class CILOSTAZOLBundle {
    //region Singleton
    private static CILOSTAZOLBundle instance = null;

    private static CILOSTAZOLBundle getInstance()
    {
        if (instance == null)
            instance = new CILOSTAZOLBundle();

        return instance;
    }
    //endregion

    private final String bundleName = "CILOSTAZOLBundle";
    final ResourceBundle bundle;

    private CILOSTAZOLBundle()
    {
        bundle = ResourceBundle.getBundle(bundleName);
    }

    public static String message(String name, Object... args) {
        return String.format(getInstance().bundle.getString(name), args);
    }

}
