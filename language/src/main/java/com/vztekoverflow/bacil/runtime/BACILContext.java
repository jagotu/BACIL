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
import com.oracle.truffle.api.nodes.Node;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Context of BACIL, storing all currently loaded assemblies.
 */
public class BACILContext {
    private final BACILLanguage language;
    private final TruffleLanguage.Env env;
    private final List<Path> libraryPaths = new ArrayList<>();
    private final ArrayList<BACILComponent> loadedAssemblies = new ArrayList<>();

    @CompilerDirectives.CompilationFinal
    private BuiltinTypes builtinTypes = null;

    @CompilerDirectives.CompilationFinal
    private boolean builtinInitalizing = false;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final String[] stubbedMethods;

    private static final TruffleLanguage.ContextReference<BACILContext> REFERENCE =
            TruffleLanguage.ContextReference.create(BACILLanguage.class);

    public static BACILContext get(Node node) {
        return REFERENCE.get(node);
    }

    /**
     * Get the instance of {@link BACILLanguage} this context belongs to.
     */
    public BACILLanguage getLanguage() {
        return language;
    }

    /**
     * Get the truffle environment of this context.
     */
    public TruffleLanguage.Env getEnv() {
        return env;
    }

    /**
     * Create a BACIL context for the specified {@link BACILLanguage} and environment.
     * @param language the {@link BACILLanguage} this context belongs to
     * @param env the truffle environment
     */
    public BACILContext(BACILLanguage language, TruffleLanguage.Env env) {
        this.language = language;
        this.env = env;

        addLibraryPaths(BACILEngineOption.getPolyglotOptionSearchPaths(env));
        this.stubbedMethods = BACILEngineOption.getPolyglotOptionStubbedMethods(env);

    }

    /**
     * Add all paths into the library paths, using them to locate assemblies.
     * @param paths paths to add to library paths
     */
    public void addLibraryPaths(List<String> paths) {
        for (String p : paths) {
            addLibraryPath(p);
        }
    }

    /**
     * Add a paths into the library paths, using it to locate assemblies.
     * @param p the path to add
     */
    public void addLibraryPath(String p) {
        Path path = Paths.get(p);
        TruffleFile file = getEnv().getInternalTruffleFile(path.toString());
        if (file.isDirectory()) {
            if (!libraryPaths.contains(path)) {
                libraryPaths.add(path);
            }
        }
    }

    /**
     * Find and load an assembly resolving the specified assembly reference.
     * @param reference identity of the assembly to find
     * @return the loaded assembly
     */
    private BACILComponent findAssembly(AssemblyIdentity reference)
    {
        //Loading assemblies is an expensive task and should never be compiled.
        CompilerAsserts.neverPartOfCompilation();

        //Resolve BACILHelpers as our internal component
        if(reference.getName().equals("BACILHelpers"))
        {
            BACILComponent helpers = new BACILHelpersComponent(builtinTypes, getLanguage());
            loadedAssemblies.add(helpers);
            return helpers;
        }


        //Locate dll in libraryPaths
        for (Path p : libraryPaths) {
            Path absPath = Paths.get(p.toString(), reference.getName() + ".dll");
            TruffleFile file = getEnv().getInternalTruffleFile(absPath.toUri());
            if (file.exists()) {
                try {
                    CLIComponent c = loadAssembly(Source.newBuilder(BACILLanguage.ID, file).build());
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

    /**
     * Load an assembly from the specified source file.
     * @param source the source file to load
     * @return the loaded assembly as a {@link CLIComponent}
     */
    public CLIComponent loadAssembly(Source source)
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

        //If we didn't yet initialize builtin types, use the fact that the first reference in an assembly is
        //always the corelib reference, and resolve them from there.
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

    /**
     * Get an assembly resolving the reference, loading it if it wasn't loaded yet.
     * @param reference identity of the assembly to get
     * @return the assembly
     */
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

    /**
     * Get the builtin types resolved in this context.
     */
    public BuiltinTypes getBuiltinTypes() {
        return builtinTypes;
    }

    /**
     * Check whether a method with the specified name should be stubbed.
     * @param methodName the method name to check
     * @return whether the method should be stubbed
     */
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
