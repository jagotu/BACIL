import com.oracle.truffle.api.TruffleLanguage;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLIHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLIStringHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeRefTableRow;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.io.ByteSequence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BogoMain {

    private static final String CIL = "cil";

    public static void main(String[] args) {
        try {
            final Source source = Source.newBuilder(CIL, new File(args[0])).build();
            final Context context = Context.newBuilder(CIL).allowNativeAccess(true).build();
            context.eval(source);
/*            final String path = "kernel32.dll";
            String loadExpression;
            loadExpression = String.format("load \"%s\"", path);
            final Source source = Source.newBuilder("nfi", loadExpression, "(load " + path + ")").internal(true).build();*/

            /*File file = new File(args[0]);
            ByteSequence bytes = ByteSequence.create(Files.readAllBytes(file.toPath()));
            CLIComponent m = CLIComponent.parseComponent(bytes, null);

            int entryPointToken = m.getCliHeader().getEntryPointToken();
            if(entryPointToken != 0)
            {
                CLITablePtr entryPointPtr = CLITablePtr.fromToken(entryPointToken);
                switch(entryPointPtr.getTableId())
                {
                    case CLITableConstants.CLI_TABLE_METHOD_DEF:
                        CLIMethodDefTableRow entryPoint = m.getTables().getTableHeads().getMethodDefTableHead().skip(entryPointPtr.getRowNo()-1);
                        System.out.printf("%s (0x%x RVA, 0x%x FILE)%n", entryPoint.getName().read(m.getStringHeap()), entryPoint.getRVA(), m.getFileOffsetForRVA(entryPoint.getRVA()));
                        break;
                    default:
                        throw new RuntimeException("Entrypoint in unexpected table: " + entryPointPtr.getTableId());
                }



            }



            //System.out.println(m.getTables().getTableHeads().getAssemblyTableHead().getName().read(m.getStringHeap()));


            /*CLIModuleTableRow row = new CLIModuleTableRow(m.getTables(), 0);
            int sum = 0;
            for(int i = 0; i < 100000000; i++)
            {
                sum += row.getEncId().get(m.getGuidHeap()).version();
            }

            System.out.println(sum);*/


        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}

