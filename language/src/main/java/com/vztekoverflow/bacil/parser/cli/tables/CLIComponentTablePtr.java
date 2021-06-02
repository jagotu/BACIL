package com.vztekoverflow.bacil.parser.cli.tables;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;

public class CLIComponentTablePtr
{
    private final CLITablePtr ptr;
    private final CLIComponent component;

    public CLIComponentTablePtr(CLITablePtr ptr, CLIComponent component) {
        this.ptr = ptr;
        this.component = component;
    }

    public CLITablePtr getPtr() {
        return ptr;
    }

    public CLIComponent getComponent() {
        return component;
    }
}
