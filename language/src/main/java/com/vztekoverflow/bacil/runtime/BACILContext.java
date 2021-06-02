package com.vztekoverflow.bacil.runtime;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.source.Source;
import com.vztekoverflow.bacil.BACILEngineOption;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.runtime.bacil.BACILComponent;
import com.vztekoverflow.bacil.runtime.bacil.BACILHelpersComponent;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;
import org.graalvm.polyglot.io.ByteSequence;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BACILContext {
    private final BACILLanguage language;
    private final TruffleLanguage.Env env;
    private final List<Path> libraryPaths = new ArrayList<>();
    private final ArrayList<BACILComponent> loadedAssemblies = new ArrayList<>();

    @CompilerDirectives.CompilationFinal
    private BuiltinTypes builtinTypes = null;

    @CompilerDirectives.CompilationFinal
    private boolean builtinInitalizing = false;

    public BACILLanguage getLanguage() {
        return language;
    }

    public TruffleLanguage.Env getEnv() {
        return env;
    }

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final String[] stubbedMethods;

    public BACILContext(BACILLanguage language, TruffleLanguage.Env env) {
        this.language = language;
        this.env = env;

        addLibraryPaths(BACILEngineOption.getPolyglotOptionSearchPaths(env));
        this.stubbedMethods = BACILEngineOption.getPolyglotOptionStubbedMethods(env);

    }

    public void addLibraryPaths(List<String> paths) {
        for (String p : paths) {
            addLibraryPath(p);
        }
    }

    public void addLibraryPath(String p) {
        Path path = Paths.get(p);
        TruffleFile file = getEnv().getInternalTruffleFile(path.toString());
        if (file.isDirectory()) {
            if (!libraryPaths.contains(path)) {
                libraryPaths.add(path);
            }
        }
    }

    private BACILComponent findAssembly(AssemblyIdentity reference)
    {
        CompilerAsserts.neverPartOfCompilation();

        if(reference.getName().equals("BACILHelpers"))
        {
            BACILComponent helpers = new BACILHelpersComponent(builtinTypes, getLanguage());
            loadedAssemblies.add(helpers);
            return helpers;
        }


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
        CompilerAsserts.neverPartOfCompilation();

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

        if(builtinTypes == null && !builtinInitalizing)
        {
            builtinInitalizing = true;
            AssemblyIdentity corLibRef = AssemblyIdentity.fromAssemblyRefRow(c.getStringHeap(), c.getTableHeads().getAssemblyRefTableHead());
            CLIComponent corLib = (CLIComponent) findAssembly(corLibRef);
            builtinTypes = new BuiltinTypes(corLib);

            for(BACILComponent comp : loadedAssemblies)
            {
                comp.setBuiltinTypes(builtinTypes);
            }
        }

        c.setBuiltinTypes(builtinTypes);

        return c;
    }


    public BACILComponent getAssembly(AssemblyIdentity reference)
    {
        CompilerAsserts.neverPartOfCompilation();

        for (BACILComponent assemblyComponent: loadedAssemblies) {
            assert assemblyComponent.getAssemblyIdentity() != null;
            if(assemblyComponent.getAssemblyIdentity().resolvesRef(reference))
            {
                return assemblyComponent;
            }
        }

        return findAssembly(reference);
    }

    public BuiltinTypes getBuiltinTypes() {
        return builtinTypes;
    }

    public boolean isStubbed(String methodName)
    {
        for (String possibility : stubbedMethods)
        {
            if(possibility.equals(methodName))
            {
                return true;
            }
        }
        return false;
    }
}
