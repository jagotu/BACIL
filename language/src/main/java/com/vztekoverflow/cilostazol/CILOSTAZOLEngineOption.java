package com.vztekoverflow.cilostazol;

import com.oracle.truffle.api.Option;
import com.oracle.truffle.api.TruffleLanguage;
import org.graalvm.options.OptionCategory;
import org.graalvm.options.OptionDescriptor;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionStability;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public final class CILOSTAZOLEngineOption {

    public static final String OPTION_ARRAY_SEPARATOR = ";";

    public static final String LIBRARY_PATH_NAME = "cil.libraryPath";

    //TODO: undo comment when fully ported from BACIL -> now it causes regression
    @Option(name = LIBRARY_PATH_NAME,
            category = OptionCategory.USER,
            stability = OptionStability.STABLE,
            help = "A list of paths where CILOSTAZOL will search for relative libraries. " +
                    "Paths are delimited by a semicolon \'" + OPTION_ARRAY_SEPARATOR + "\'.")

    public static final OptionKey<String> LIBRARY_PATH = new OptionKey<>("");

    public static Path[] getPolyglotOptionSearchPaths(TruffleLanguage.Env env) {
        if (env.getOptions().getDescriptors().get(LIBRARY_PATH_NAME) == null)
            return new Path[]{Paths.get(".")};
        else
        {
            OptionDescriptor desc = env.getOptions().getDescriptors().get(LIBRARY_PATH_NAME);
            String libraryPathOption = env.getOptions().get((OptionKey<String>) desc.getKey());
            String[] libraryPaths = "".equals(libraryPathOption) ? new String[0] : libraryPathOption.split(OPTION_ARRAY_SEPARATOR);
            return (Path[])Arrays.stream(libraryPaths).map(Paths::get).toArray();
        }
    }
}
