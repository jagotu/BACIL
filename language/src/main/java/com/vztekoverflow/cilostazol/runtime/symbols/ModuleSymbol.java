package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

public class ModuleSymbol extends Symbol {
    private final CLIFile definingFile;

    public ModuleSymbol(CLIFile definingFile) {
        super(ContextProviderImpl.getInstance());
        this.definingFile = definingFile;
    }

    public CLIFile getDefiningFile() {
        return definingFile;
    }

    public NamedTypeSymbol getLocalType(String namespace, String name)
    {
        throw new NotImplementedException();
    }

    public static class ModuleSymbolFactory
    {
        public static ModuleSymbol create(CLIFile file) {
            return new ModuleSymbol(file);
        }
    }
}
