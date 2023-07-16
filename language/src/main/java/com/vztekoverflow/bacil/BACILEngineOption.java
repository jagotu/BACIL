package com.vztekoverflow.bacil;

import com.oracle.truffle.api.TruffleLanguage;
import java.util.Arrays;
import java.util.List;
import org.graalvm.options.OptionKey;

/** Options for the BACIL Engine. */
public final class BACILEngineOption {

  public static final String OPTION_ARRAY_SEPARATOR = ";";

  public static final String LIBRARY_PATH_NAME = "cil.libraryPath";
  //    @Option(name = LIBRARY_PATH_NAME,
  //            category = OptionCategory.USER,
  //            stability = OptionStability.STABLE,
  //            help = "A list of paths where BACIL will search for relative libraries. " +
  //                    "Paths are delimited by a semicolon \'" + OPTION_ARRAY_SEPARATOR + "\'.")
  public static final OptionKey<String> LIBRARY_PATH = new OptionKey<>("");

  public static final String STUBBED_METHODS_NAME = "cil.stubMethods";

  //  @Option(
  //      name = STUBBED_METHODS_NAME,
  //      category = OptionCategory.USER,
  //      stability = OptionStability.STABLE,
  //      help =
  //          "A list of methods to be replaced with stubs returning a default value. "
  //              + "Delimited by a semicolon \'"
  //              + OPTION_ARRAY_SEPARATOR
  //              + "\'.")
  public static final OptionKey<String> STUBBED_METHODS = new OptionKey<>("");

  public static List<String> getPolyglotOptionSearchPaths(TruffleLanguage.Env env) {
    String libraryPathOption = env.getOptions().get(LIBRARY_PATH);
    String[] libraryPath =
        "".equals(libraryPathOption)
            ? new String[0]
            : libraryPathOption.split(OPTION_ARRAY_SEPARATOR);
    return Arrays.asList(libraryPath);
  }

  public static String[] getPolyglotOptionStubbedMethods(TruffleLanguage.Env env) {
    String stubbedMethodsOption = env.getOptions().get(STUBBED_METHODS);
    return "".equals(stubbedMethodsOption)
        ? new String[0]
        : stubbedMethodsOption.split(OPTION_ARRAY_SEPARATOR);
  }
}
