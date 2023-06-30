package com.vztekoverflow.cilostazol.runtime.symbols.utils;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import org.graalvm.collections.Pair;

public class CLIFileUtils {
  public static Pair<Integer, Integer> getMethodRange(CLIFile file, CLITypeDefTableRow row) {
    final var methodTablePtr = row.getMethodListTablePtr();
    final boolean isLastType =
        row.getRowNo() == file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF);
    final int lastIdx =
        isLastType
            ? file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_METHOD_DEF)
            : row.skip(1).getMethodListTablePtr().getRowNo();

    return Pair.create(methodTablePtr.getRowNo(), lastIdx);
  }

  public static Pair<String, String> getNameAndNamespace(CLIFile file, CLITypeDefTableRow row) {
    final var name = row.getTypeNameHeapPtr().read(file.getStringHeap());
    final var namespace = row.getTypeNamespaceHeapPtr().read(file.getStringHeap());
    return Pair.create(name, namespace);
  }
}
