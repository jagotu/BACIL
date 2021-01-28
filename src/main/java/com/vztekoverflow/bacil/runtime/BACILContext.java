package com.vztekoverflow.bacil.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.source.Source;
import com.vztekoverflow.bacil.BACILEngineOption;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import org.graalvm.collections.EconomicMap;
import org.graalvm.polyglot.io.ByteSequence;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BACILContext {
    private final BACILLanguage language;
    private final TruffleLanguage.Env env;
    private final List<Path> libraryPaths = new ArrayList<>();
    private final ArrayList<CLIComponent> loadedAssemblies = new ArrayList<>();

    public BACILLanguage getLanguage() {
        return language;
    }

    public TruffleLanguage.Env getEnv() {
        return env;
    }

    public BACILContext(BACILLanguage language, TruffleLanguage.Env env) {
        this.language = language;
        this.env = env;

        addLibraryPaths(BACILEngineOption.getPolyglotOptionSearchPaths(env));
    }

    public void addLibraryPaths(List<String> paths) {
        for (String p : paths) {
            addLibraryPath(p);
        }
    }

    private void addLibraryPath(String p) {
        Path path = Paths.get(p);
        TruffleFile file = getEnv().getInternalTruffleFile(path.toString());
        if (file.isDirectory()) {
            if (!libraryPaths.contains(path)) {
                libraryPaths.add(path);
            }
        }
    }

    private CLIComponent findAssembly(AssemblyIdentity reference)
    {
        for (Path p : libraryPaths) {
            Path absPath = Paths.get(p.toString(), reference.getName() + ".dll");
            TruffleFile file = getEnv().getInternalTruffleFile(absPath.toUri());
            if (file.exists()) {
                try {
                    CLIComponent c = loadAssembly(Source.newBuilder(BACILLanguage.ID, file).build(), language);
                    if(c.getAssemblyIdentity().resolvesRef(reference))
                    {
                        return c;
                    }
                } catch (IOException e) {
                    throw new BACILInternalError(e.toString());
                }
            }
        }
        throw new BACILInternalError("Failed to locate assembly " + reference.getName());
    }

    public CLIComponent loadAssembly(Source source, BACILLanguage language)
    {
        ByteSequence bytes;

        if (source.hasBytes()) {
            bytes = source.getBytes();
        } else if (source.hasCharacters()) {
            throw new BACILParserException("Unexpected character-based source with mime type: " + source.getMimeType());
        } else {
            throw new BACILParserException("Should not reach here: Source is neither char-based nor byte-based!");
        }

        CLIComponent c = CLIComponent.parseComponent(bytes, source, this);
        if(c.getAssemblyIdentity() != null)
        {
            loadedAssemblies.add(c);
        }
        return c;
    }


    public CLIComponent getAssembly(AssemblyIdentity reference)
    {
        CompilerAsserts.neverPartOfCompilation();
        for (CLIComponent assemblyComponent: loadedAssemblies) {
            assert assemblyComponent.getAssemblyIdentity() != null;
            if(assemblyComponent.getAssemblyIdentity().resolvesRef(reference))
            {
                return assemblyComponent;
            }
        }

        return findAssembly(reference);
    }

}
