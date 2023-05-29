package com.vztekoverflow.cilostazol.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage;
import com.vztekoverflow.cilostazol.CILOSTAZOLEngineOption;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.meta.Meta;

import java.nio.file.Path;
import java.util.Arrays;

public class CILOSTAZOLContext {
    private final Path[] _libraryPaths;
    private final CILOSTAZOLLanguage _language;
    private final TruffleLanguage.Env _env;

    @CompilerDirectives.CompilationFinal
    private Meta meta;

    public CILOSTAZOLContext(CILOSTAZOLLanguage lang, TruffleLanguage.Env env) {
        _language = lang;
        _env = env;
        _libraryPaths = (Path[]) Arrays.stream(CILOSTAZOLEngineOption.getPolyglotOptionSearchPaths(env)).filter(p -> {
            TruffleFile file = getEnv().getInternalTruffleFile(p.toString());
            return file.isDirectory();
        }).distinct().toArray();
    }

    //For test propose only
    public CILOSTAZOLContext(CILOSTAZOLLanguage lang, Path[] libraryPaths) {
        _language = lang;
        _env = null;
        _libraryPaths = libraryPaths;
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
}
