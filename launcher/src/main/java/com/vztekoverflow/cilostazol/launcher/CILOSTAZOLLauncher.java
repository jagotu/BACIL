package com.vztekoverflow.cilostazol.launcher;

import org.graalvm.launcher.AbstractLanguageLauncher;
import org.graalvm.options.OptionCategory;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CILOSTAZOLLauncher extends AbstractLanguageLauncher {

    private static final String LANGUAGE_ID = "cil";
    private String inputFile = null;
    private int returnValue = 0;

    public static void main(String[] args) {
        CILOSTAZOLLauncher launcher = new CILOSTAZOLLauncher();
        launcher.launch(args);
        System.exit(launcher.getReturnValue());
    }

    public int getReturnValue() {
        return returnValue;
    }

    @Override
    protected List<String> preprocessArguments(List<String> arguments, Map<String, String> polyglotOptions) {
        ArrayList<String> unrecognized = new ArrayList<>();
        for (String arg : arguments) {
            if (!arg.startsWith("-")) {
                inputFile = arg;
            } else {
                unrecognized.add(arg);
            }
        }

        if (inputFile == null) {
            printUsage();
            System.exit(getReturnValue());
        }

        return unrecognized;
    }

    @Override
    protected void launch(Context.Builder contextBuilder) {
        contextBuilder.out(new FileOutputStream(FileDescriptor.out));
        contextBuilder.err(new FileOutputStream(FileDescriptor.err));
        contextBuilder.allowAllAccess(true);

        try (Context context = contextBuilder.build()) {
            final Source source = Source.newBuilder(LANGUAGE_ID, new File(inputFile)).build();
            final long start = System.currentTimeMillis();
            returnValue = context.eval(source).asInt();
            System.err.println(LauncherBundle.message("launcher.output.return.value", returnValue));
            final long done = System.currentTimeMillis();
            System.err.println(LauncherBundle.message("launcher.output.return.time", done));
        } catch (IOException e) {
            printFileNotFound();
        }
        catch (PolyglotException e) {
            handlePolyglotException(e);
        }
    }

    @Override
    protected String getLanguageId() {
        return LANGUAGE_ID;
    }

    @Override
    protected void printHelp(OptionCategory maxCategory) {
    }

    private void handlePolyglotException(PolyglotException e) {
        if (e.isIncompleteSource()) {
            printIncompleteSource();
            return;
        }

        if (e.isInternalError() && e.getMessage().contains("entry point")) {
            printEntryPointNotFound();
            return;
        }

        printPolyglotException(e);
    }

    private void printPolyglotException(PolyglotException e) {
        System.err.println("Exception: " + e.getMessage());
        System.err.println("Stack trace:");
        e.printStackTrace();
    }

    private void printUsage() {
        System.out.println();
        System.out.println("Usage: cilostazol [options] [path-to-application]");
        printDefaultHelp(OptionCategory.USER);
    }

    private void printFileNotFound() {
        System.out.println("Could not execute because the specified file was not found.");
        System.out.println("Possible reasons for this include:");
        System.out.println("\t* You intended to execute a .NET program, but dotnet-" + inputFile + " does not exist.");
    }

    private void printEntryPointNotFound() {
        System.out.println("Could not execute because the specified file does not contain a valid entry point.");
        System.out.println("Possible reasons for this include:");
        System.out.println("\t* You intended to execute a .NET program, but " + inputFile + " is not a valid .NET assembly.");
        System.out.println("\t* You intended to execute a .NET program, but " + inputFile + " is a class library.");
    }

    private void printIncompleteSource() {
        System.out.println("Could not execute because the specified file contains an incomplete source.");
        System.out.println("Possible reasons for this include:");
        System.out.println("\t* You intended to execute a .NET program, but " + inputFile + " is not a valid .NET assembly.");
    }
}
