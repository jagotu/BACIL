package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeRefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;

public interface ITypeFactory {
    public IType create(CLITypeDefTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters);
    public IType create(CLITypeSpecTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters);
    public IType create(CLITypeRefTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters);
    public IType create(TypeSig signature, IType[] methodTypeParameters, IType[] classTypeParameters);
    public IType createVoid();
    public IType createTypedRef();
}
