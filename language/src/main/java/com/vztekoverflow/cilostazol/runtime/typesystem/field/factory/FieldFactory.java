package com.vztekoverflow.cilostazol.runtime.typesystem.field.factory;

import com.vztekoverflow.cil.parser.cli.signature.FieldSig;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIFieldTableRow;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.Field;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.factory.TypeFactory;

public final class FieldFactory {
    public static IField create(CLIFieldTableRow fieldRow, IComponent component) {
        final String name = component.getNameFrom(fieldRow);
        final var signature = fieldRow.getSignatureHeapPtr().read(component.getDefiningFile().getBlobHeap());

        final FieldSig fieldSig = FieldSig.parse(new SignatureReader(signature));
        final IType type = TypeFactory.create(fieldSig.getType(), null, null, component);
        boolean isStatic = getStaticness(fieldRow.getFlags());

        return new Field(name, type, isStatic, component.getDefiningFile());
    }

    private static boolean getStaticness(short flags) {
        return (flags & 0x0010) != 0;
    }
}
