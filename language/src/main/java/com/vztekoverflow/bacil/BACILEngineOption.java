package com.vztekoverflow.bacil;

import com.oracle.truffle.api.Option;
import com.oracle.truffle.api.TruffleLanguage;
import org.graalvm.options.OptionCategory;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionStability;

import java.util.Arrays;
import java.util.List;

public final class BACILEngineOption {

    public static final String OPTION_ARRAY_SEPARATOR = ";";

    public static final String LIBRARY_PATH_NAME = "cil.libraryPath";
    @Option(name = LIBRARY_PATH_NAME,
            category = OptionCategory.USER,
            stability = OptionStability.STABLE,
            help = "A list of paths where BACIL will search for relative libraries. " +
                    "Paths are delimited by a semicolon \'" + OPTION_ARRAY_SEPARATOR + "\'.")
    public static final OptionKey<String> LIBRARY_PATH = new OptionKey<>("");

    public static List<String> getPolyglotOptionSearchPaths(TruffleLanguage.Env env) {
        String libraryPathOption = env.getOptions().get(LIBRARY_PATH);
        String[] libraryPath = "".equals(libraryPathOption) ? new String[0] : libraryPathOption.split(OPTION_ARRAY_SEPARATOR);
        return Arrays.asList(libraryPath);
    }
}
