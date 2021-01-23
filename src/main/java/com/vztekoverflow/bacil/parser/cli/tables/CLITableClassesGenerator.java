package com.vztekoverflow.bacil.parser.cli.tables;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CLITableClassesGenerator {

    private final static String PACKAGE = "com.vztekoverflow.bacil.parser.cli.tables.generated";

    private static class TableDefinition {
        public byte id;
        public String name;
        public ArrayList<String> fields = new ArrayList<>();

        public TableDefinition(byte id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static class HeapPenalties {
        private int byteOffset = 0;
        private int stringHeapPenalty = 0;
        private int GUIDHeapPenalty = 0;
        private int blobHeapPenalty = 0;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: table_classes_generator <input definition> <output directory>");
            System.exit(64);
        }

        ArrayList<TableDefinition> tables = new ArrayList<>();
        TableDefinition currentTable = null;

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0)
                    continue;

                switch (line.charAt(0)) {
                    case '#':
                    case '-':
                        assert currentTable != null;
                        currentTable.fields.add(line);
                        break;
                    default: {
                        String name = line.split(":")[0];
                        byte id = Byte.valueOf(line.split(":")[1], 16);
                        currentTable = new TableDefinition(id, name);
                        tables.add(currentTable);
                    }
                }
            }
        }
        tables.sort((o1, o2) -> (o1.id - o2.id));

        generateConstantsClass(tables, args[1]);
        for(TableDefinition t : tables)
        {
            generateTableClass(t, args[1]);
        }
        generateHeadsClass(tables, args[1]);

    }

    public static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String decapitalize(String input) {
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    public static void generatePenaltiesCode(HeapPenalties penalties, PrintWriter writer) {
        writer.println("\t\tint offset = " + penalties.byteOffset + ";");
        if (penalties.stringHeapPenalty != 0) {
            writer.println("\t\tif (tables.isStringHeapBig()) offset += " + penalties.stringHeapPenalty + ";");
        }

        if (penalties.GUIDHeapPenalty != 0) {
            writer.println("\t\tif (tables.isGUIDHeapBig()) offset += " + penalties.GUIDHeapPenalty + ";");
        }

        if (penalties.blobHeapPenalty != 0) {
            writer.println("\t\tif (tables.isBlobHeapBig()) offset += " + penalties.blobHeapPenalty + ";");
        }
    }

    public static void generateConstantsClass(ArrayList<TableDefinition> tables, String outputDir) throws FileNotFoundException, UnsupportedEncodingException {
        String path = outputDir + "/CLITableConstants.java";
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writer.println("package " + PACKAGE + ";");
            writer.println();
            writer.println("public class CLITableConstants {");
            writer.println("");

            for (TableDefinition tableDefinition : tables) {
                writer.println(String.format("\tpublic static final byte CLI_TABLE%s = %d;", nameToConstName(tableDefinition.name), tableDefinition.id));
            }

            writer.println(String.format("\tpublic static final byte CLI_TABLE_MAX_ID = %d;", tables.get(tables.size() - 1).id));

            writer.println("}");
        }
    }

    public static void generateHeadsClass(ArrayList<TableDefinition> tables, String outputDir) throws FileNotFoundException, UnsupportedEncodingException {
        String path = outputDir + "/CLITableHeads.java";
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writer.println("package " + PACKAGE + ";");
            writer.println();
            writer.println("import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;");
            writer.println("import com.vztekoverflow.bacil.parser.cli.tables.CLITables;");
            writer.println();
            writer.println("public class CLITableHeads {");
            writer.println();

            //fields
            for (TableDefinition table : tables) {
                String className = "CLI" + table.name + "TableRow";
                writer.println(String.format("\tprivate final %s %s;", className, decapitalize(table.name) + "TableHead"));
            }

            //getTableLength
            writer.println("\tpublic static int getTableLength(CLITables tables, CLITableRow<?> row) {");
            writer.println("\t\treturn row.getLength()*tables.getTablesHeader().getRowCount(row.getTableId());");
            writer.println("\t}");

            //constructor
            writer.println();
            writer.println("\tpublic CLITableHeads(CLITables tables) {");
            writer.println("\t\tint cursor = 0;");
            for (TableDefinition table : tables) {
                String className = "CLI" + table.name + "TableRow";
                writer.println(String.format("\t\t%s = new %s(tables, cursor, 0);", decapitalize(table.name) + "TableHead", className));
                writer.println(String.format("\t\tcursor += getTableLength(tables, %s);", decapitalize(table.name) + "TableHead"));
            }
            writer.println("\t}");

            //getters
            for (TableDefinition table : tables) {
                String className = "CLI" + table.name + "TableRow";
                writer.println(String.format("\tpublic %s get%s() { return %s; }", className, table.name + "TableHead", decapitalize(table.name) + "TableHead"));
            }

            writer.println("}");
        }
    }

    public static void generateGetColumn(HeapPenalties penalties, String fieldName, String fieldType, PrintWriter writer) {
        int returnOffset = penalties.byteOffset;
        switch (fieldType.charAt(0)) {
            case 'c': {
                String type = "";
                switch (fieldType.charAt(1)) {
                    case '1':
                        type = "byte";
                        returnOffset += 1;
                        break;
                    case '2':
                        type = "short";
                        returnOffset += 2;
                        break;
                    case '4':
                        type = "int";
                        returnOffset += 4;
                        break;
                    case '8':
                        type = "long";
                        returnOffset += 8;
                        break;
                }
                writer.println(String.format("\tpublic final %s get%s() {", type, fieldName));
                generatePenaltiesCode(penalties, writer);
                writer.println(String.format("\t\treturn get%s(offset);", capitalize(type)));
                writer.println("\t}\n");
                break;
            }

            case 'h': {
                String streamName = fieldType.substring(1);
                writer.println(String.format("\tpublic final CLI%sHeapPtr get%s() {", streamName, fieldName));
                generatePenaltiesCode(penalties, writer);
                writer.println("\t\tint heapOffset=0;");
                writer.println(String.format("\t\tif (tables.is%sHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }", streamName));
                writer.println(String.format("\t\treturn new CLI%sHeapPtr(heapOffset);", streamName));
                writer.println("\t}\n");

                returnOffset += 2;
                switch (streamName) {
                    case "Blob":
                        penalties.blobHeapPenalty += 2;
                        break;
                    case "String":
                        penalties.stringHeapPenalty += 2;
                        break;
                    case "GUID":
                        penalties.GUIDHeapPenalty += 2;
                        break;
                }
                break;
            }

            case 'i': {

                if (!fieldType.contains("|")) {
                    writer.println(String.format("\tpublic final CLITablePtr get%s() { ", fieldName));
                    generatePenaltiesCode(penalties, writer);
                    //Non-conforming SIMPLIFICATION - expect all tables have 2byte indices
                    writer.println(String.format("\t\treturn new CLITablePtr(CLITableConstants.CLI_TABLE%s, getShort(offset));", nameToConstName(fieldType.substring(1))));
                } else {
                    //coded index
                    String[] tables = fieldType.substring(1).split("\\|");
                    writer.print(String.format("\tprivate static final byte[] MAP%s_TABLES = new byte[] { ", nameToConstName(fieldName)));

                    writer.print(Arrays.stream(tables).map(s -> s.equals("_") ? "-1" : "CLITableConstants.CLI_TABLE" + nameToConstName(s)).collect(Collectors.joining(", ")));
                    writer.println("} ;");

                    writer.println(String.format("\tpublic final CLITablePtr get%s() { ", fieldName));
                    generatePenaltiesCode(penalties, writer);
                    writer.println("\t\tshort codedValue = getShort(offset);");

                    //Non-conforming SIMPLIFICATION - expect all tables have 2byte indices

                    int mask = 0;
                    int shift = 0;
                    if (tables.length <= 2) {
                        mask = 1;
                        shift = 1;
                    } else if (tables.length <= 4) {
                        mask = 3;
                        shift = 2;
                    } else if (tables.length <= 8) {
                        mask = 7;
                        shift = 3;
                    } else if (tables.length <= 16) {
                        mask = 15;
                        shift = 4;
                    } else {
                        mask = 31;
                        shift = 5;
                    }

                    writer.println(String.format("\t\treturn new CLITablePtr(MAP%s_TABLES[codedValue & %d], codedValue >> %d);", nameToConstName(fieldName), mask, shift));


                }
                writer.println("\t}\n");
                returnOffset += 2;
                break;
            }

            default:
                throw new RuntimeException("unreachable");
        }
        penalties.byteOffset = returnOffset;
    }

    public static void generateTableClass(TableDefinition table, String outputDir) throws FileNotFoundException, UnsupportedEncodingException {
        String className = "CLI" + table.name + "TableRow";
        String path = outputDir + "/" + className + ".java";
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writer.println("package " + PACKAGE + ";");
            writer.println();
            writer.println("import com.vztekoverflow.bacil.parser.cli.tables.*;");
            writer.println(String.format("public class %s extends CLITableRow<%s> {", className, className));
            writer.println("");

            //constructor
            writer.println(String.format("\tpublic %s(CLITables tables, int cursor, int rowIndex) {", className));
            writer.println("\t\tsuper(tables, cursor, rowIndex);");
            writer.println("\t}");
            writer.println("");

            //columns methods
            int byteOffset = 0;
            HeapPenalties penalties = new HeapPenalties();
            for (String field : table.fields) {
                switch (field.charAt(0)) {
                    case '-':
                        String[] parts = field.substring(1).split(":");
                        generateGetColumn(penalties, parts[0], parts[1], writer);
                        break;
                    case '#':
                        penalties.byteOffset += Integer.parseInt(field.substring(1));
                        break;
                }
            }

            //size
            writer.println("\t@Override\n\tpublic int getLength() {");
            generatePenaltiesCode(penalties, writer);
            writer.println("\t\treturn offset;\n\t}\n");

            //getTableId
            writer.println(String.format("\t@Override\n\tpublic byte getTableId() {\n\t\treturn CLITableConstants.CLI_TABLE%s;\n\t}\n", nameToConstName(table.name)));

            //createNew
            writer.println(String.format("\t@Override\n\tprotected %s createNew(CLITables tables, int cursor, int rowIndex) {\n\t\treturn new %s(tables, cursor, rowIndex);\n\t}\n", className, className));

            writer.println("}");
        }
    }

    public static String nameToConstName(String name) {
        StringBuilder result = new StringBuilder();
        boolean lastBig = false;
        for (int i = 0; i < name.length(); i++) {

            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (!lastBig) {
                    lastBig = true;
                    result.append('_');
                }
            } else {
                lastBig = false;
            }

            result.append(Character.toUpperCase(c));
        }
        return result.toString();
    }
}
