package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;

public interface ITypeFactory {
    public IType create(CLITypeDefTableRow type);
    public IType create(TypeSig signature);
    public IType createVoid();
    public IType createTypedRef();
}
