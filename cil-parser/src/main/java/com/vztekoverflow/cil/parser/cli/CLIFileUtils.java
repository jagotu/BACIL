package com.vztekoverflow.cil.parser.cli;

import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodDefTableRow;
import java.util.ArrayList;

public class CLIFileUtils {
  public static CLIMethodDefTableRow[] getMethodByName(String name, CLIFile file) {
    ArrayList<CLIMethodDefTableRow> result = new ArrayList<>();

    for (CLIMethodDefTableRow row : file.getTableHeads().getMethodDefTableHead()) {
      if (row.getNameHeapPtr().read(file.getStringHeap()).equals(name)) result.add(row);
    }

    return result.toArray(new CLIMethodDefTableRow[result.size()]);
  }
}
