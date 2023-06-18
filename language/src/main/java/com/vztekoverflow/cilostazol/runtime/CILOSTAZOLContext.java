package com.vztekoverflow.cilostazol.runtime;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.CILOSTAZOLEngineOption;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.meta.Meta;
import com.vztekoverflow.cilostazol.runtime.other.AppDomain;
import com.vztekoverflow.cilostazol.runtime.symbols.AssemblySymbol;
import com.vztekoverflow.cilostazol.runtime.symbols.NamedTypeSymbol;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.io.ByteSequence;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.nio.file.Files;

public class CILOSTAZOLContext {
  public static final TruffleLanguage.ContextReference<CILOSTAZOLContext> CONTEXT_REF =
      TruffleLanguage.ContextReference.create(CILOSTAZOLLanguage.class);
  private final Path[] _libraryPaths;
  private final CILOSTAZOLLanguage _language;
  private final TruffleLanguage.Env _env;
  @CompilerDirectives.CompilationFinal private Meta meta;
  private final AppDomain appDomain;

  public CILOSTAZOLContext(CILOSTAZOLLanguage lang, TruffleLanguage.Env env) {
    _language = lang;
    _env = env;
    getLanguage().initializeGuestAllocator(env);
    _libraryPaths =
        Arrays.stream(CILOSTAZOLEngineOption.getPolyglotOptionSearchPaths(env))
            .filter(
                p -> {
                  TruffleFile file = getEnv().getInternalTruffleFile(p.toString());
                  return file.isDirectory();
                })
            .distinct()
            .toArray(Path[]::new);
    appDomain = new AppDomain();
  }

  // For test propose only
  public CILOSTAZOLContext(CILOSTAZOLLanguage lang, Path[] libraryPaths) {
    _language = lang;
    _env = null;
    _libraryPaths = libraryPaths;
    appDomain = new AppDomain();
  }

  public static CILOSTAZOLContext get(Node node) {
    return CONTEXT_REF.get(node);
  }

  public CILOSTAZOLLanguage getLanguage() {
    return _language;
  }

  public Meta getMeta() {
    return meta;
  }

  public GuestAllocator getAllocator() {
    return getLanguage().getAllocator();
  }

  public TruffleLanguage.Env getEnv() {
    return _env;
  }

  public Path[] getLibsPaths() {
    return _libraryPaths;
  }

  public void setBootstrapMeta(Meta meta) {
    this.meta = meta;
  }

  //region Symbols
  public NamedTypeSymbol getType(String name, String namespace, AssemblyIdentity assembly)
  {
    //TODO: caching
    AssemblySymbol assemblySymbol = appDomain.getAssembly(assembly);
    if (assemblySymbol == null)
    {
      assemblySymbol = findAssembly(assembly);
    }

    if (assemblySymbol != null)
      return assemblySymbol.getLocalType(name, namespace);

    return null;
  }

  public AssemblySymbol findAssembly(AssemblyIdentity assemblyIdentity) {
    //Loading assemblies is an expensive task which should be never compiled
    CompilerAsserts.neverPartOfCompilation();

    //TODO: resolve and load PrimitiveTypes

    //Locate dlls in paths

    for (Path path : _libraryPaths) {
      File file = new File(path.toString() + "/" + assemblyIdentity.getName() + ".dll");
      if (file.exists()) {
        try {
          return loadAssembly(Source.newBuilder(
                  CILOSTAZOLLanguage.ID,
                  ByteSequence.create(Files.readAllBytes(file.toPath())),
                  file.getName()).build());
        } catch (Exception e) {
          throw new RuntimeException("Error loading assembly " + assemblyIdentity.getName() + " from " + path.toString(), e);
        }
      }
    }
    return null;
  }

  public AssemblySymbol loadAssembly(Source source) {
    return AssemblySymbol.AssemblySymbolFactory.create(source);
  }
  //endregion
}
