package com.vztekoverflow.bacil;

import org.graalvm.launcher.AbstractLanguageLauncher;
import org.graalvm.options.OptionCategory;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BACILLauncher extends AbstractLanguageLauncher {

    private static final String LANGUAGE_ID = "cil";
    private String inputFile = null;

    public static void main(String[] args) {
        new BACILLauncher().launch(args);
    }

    @Override
    protected List<String> preprocessArguments(List<String> arguments, Map<String, String> polyglotOptions) {
        ArrayList<String> unrecognized = new ArrayList<>();
        for (int i = 0; i < arguments.size(); i++) {
            String arg = arguments.get(i);
            if(!arg.startsWith("-"))
            {
                inputFile = arg;
            } else {
                unrecognized.add(arg);
            }
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
            System.out.println("Returned: " + context.eval(source));
            final long done = System.currentTimeMillis();
            System.out.println("Runtime: " + (done-start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getLanguageId() {
        return LANGUAGE_ID;
    }

    @Override
    protected void printHelp(OptionCategory maxCategory) {

    }
}
