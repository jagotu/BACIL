package com.vztekoverflow.cilostazol.runtime.typesystem.type.builtin.system;

import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;
import org.graalvm.polyglot.io.ByteSequence;

public class CLITableSystemTypeRow extends CLITableRow<CLITableSystemTypeRow> {

    public static final CLITableSystemTypeRow INSTANCE = new CLITableSystemTypeRow();

    /**
     * Create a new dummy {@link CLITableRow} used to mock system types.
     */
    private CLITableSystemTypeRow() {
        super(new CLITables(new ByteSequence() {
            @Override
            public int length() {
                return 0;
            }

            @Override
            public byte byteAt(int index) {
                return 0;
            }
        }), 0, 0);
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public byte getTableId() {
        return 0;
    }

    @Override
    protected CLITableSystemTypeRow createNew(CLITables tables, int cursor, int rowIndex) {
        return INSTANCE;
    }
}
