package com.vztekoverflow.cilostazol.runtime.context;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.CILOSTAZOLEngineOption;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.meta.Meta;
import com.vztekoverflow.cilostazol.runtime.other.AppDomain;
import com.vztekoverflow.cilostazol.runtime.other.GuestAllocator;
import com.vztekoverflow.cilostazol.runtime.other.TypeSymbolCacheKey;
import com.vztekoverflow.cilostazol.runtime.symbols.AssemblySymbol;
import com.vztekoverflow.cilostazol.runtime.symbols.NamedTypeSymbol;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.io.ByteSequence;
import org.jetbrains.annotations.TestOnly;

public class CILOSTAZOLContext {
  public static final TruffleLanguage.ContextReference<CILOSTAZOLContext> CONTEXT_REF =
      TruffleLanguage.ContextReference.create(CILOSTAZOLLanguage.class);
  private final Path[] libraryPaths;
  private final CILOSTAZOLLanguage language;
  private final TruffleLanguage.Env env;

  private final Map<TypeSymbolCacheKey, NamedTypeSymbol> typeSymbolCache = new HashMap<>();
  @CompilerDirectives.CompilationFinal private Meta meta;
  private final AppDomain appDomain;

  public CILOSTAZOLContext(CILOSTAZOLLanguage lang, TruffleLanguage.Env env) {
    language = lang;
    this.env = env;
    getLanguage().initializeGuestAllocator(env);
    libraryPaths =
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
  @TestOnly
  public CILOSTAZOLContext(CILOSTAZOLLanguage lang, Path[] libraryPaths) {
    language = lang;
    env = null;
    this.libraryPaths = libraryPaths;
    appDomain = new AppDomain();
  }

  public static CILOSTAZOLContext get(Node node) {
    return CONTEXT_REF.get(node);
  }

  public CILOSTAZOLLanguage getLanguage() {
    return language;
  }

  public Meta getMeta() {
    return meta;
  }

  public GuestAllocator getAllocator() {
    return getLanguage().getAllocator();
  }

  public TruffleLanguage.Env getEnv() {
    return env;
  }

  public Path[] getLibsPaths() {
    return libraryPaths;
  }

  public void setBootstrapMeta(Meta meta) {
    this.meta = meta;
  }

  // region Symbols
  public NamedTypeSymbol getType(String name, String namespace, AssemblyIdentity assembly) {
    var cacheKey = hardCodedForwarding(new TypeSymbolCacheKey(name, namespace, assembly));

    return typeSymbolCache.computeIfAbsent(
        cacheKey,
        k -> {
          AssemblySymbol assemblySymbol = appDomain.getAssembly(assembly);
          if (assemblySymbol == null) {
            assemblySymbol = findAssembly(assembly);
          }

          if (assemblySymbol != null) return assemblySymbol.getLocalType(name, namespace);

          return null;
        });
  }

  public TypeSymbolCacheKey hardCodedForwarding(TypeSymbolCacheKey cacheKey) {
    if (cacheKey.assemblyIdentity() == AssemblyIdentity.SystemRuntimeLib()) {
      if (cacheKey.namespace() == "System") {
        if (cacheKey.name() == "Int32")
          return new TypeSymbolCacheKey(
              cacheKey.name(), cacheKey.namespace(), AssemblyIdentity.SystemPrivateCoreLib());
        else return cacheKey;
      } else {
        return cacheKey;
      }
    } else {
      return cacheKey;
    }
  }

  public AssemblySymbol findAssembly(AssemblyIdentity assemblyIdentity) {
    // Loading assemblies is an expensive task which should be never compiled
    CompilerAsserts.neverPartOfCompilation();
    // Locate dlls in paths
    for (Path path : libraryPaths) {
      File file = new File(path.toString() + "/" + assemblyIdentity.getName() + ".dll");
      if (file.exists()) {
        try {
          return loadAssembly(
              Source.newBuilder(
                      CILOSTAZOLLanguage.ID,
                      ByteSequence.create(Files.readAllBytes(file.toPath())),
                      file.getName())
                  .build());
        } catch (Exception e) {
          throw new RuntimeException(
              "Error loading assembly " + assemblyIdentity.getName() + " from " + path.toString(),
              e);
        }
      }
    }
    return null;
  }

  public AssemblySymbol loadAssembly(Source source) {
    var result = AssemblySymbol.AssemblySymbolFactory.create(source);
    appDomain.loadAssembly(result);
    return result;
  }
  // endregion
}
