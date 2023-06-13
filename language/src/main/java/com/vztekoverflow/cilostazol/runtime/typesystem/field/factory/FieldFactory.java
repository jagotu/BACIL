package com.vztekoverflow.cilostazol.runtime.typesystem.field.factory;

import com.vztekoverflow.cil.parser.cli.signature.FieldSig;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIFieldTableRow;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.Field;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.factory.TypeFactory;

public final class FieldFactory {
    public static IField create(CILOSTAZOLContext context, CLIFieldTableRow fieldRow, IType[] mvars, IType[] vars, CLIComponent component) {
        final String name = component.getNameFrom(fieldRow);
        final var signature = fieldRow.getSignatureHeapPtr().read(component.getDefiningFile().getBlobHeap());

        final FieldSig fieldSig = FieldSig.parse(new SignatureReader(signature));
        final IType type = TypeFactory.create(fieldSig.getType(), mvars, vars, component);
        boolean isStatic = (fieldRow.getFlags() & 0x0010) != 0;
        boolean isInitOnly = (fieldRow.getFlags() & 0x0010) != 0;
        boolean isLiteral = (fieldRow.getFlags() & 0x0040) != 0;
        boolean isNotSerialized = (fieldRow.getFlags() & 0x0080) != 0;
        boolean isSpecialName = (fieldRow.getFlags() & 0x0200) != 0;
        int visibilityFlags = fieldRow.getFlags() & 0x0007;

        return new Field(name, type, isStatic, isInitOnly, isLiteral, isNotSerialized, isSpecialName, visibilityFlags, component.getDefiningFile());
    }

    private static boolean getStaticness(short flags) {
        return (flags & 0x0010) != 0;
    }
}
