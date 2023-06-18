package com.vztekoverflow.cilostazol.runtime.symbols;

public abstract class ConstructedNamedTypeSymbol extends NamedTypeSymbol {
    protected final NamedTypeSymbol constructedFrom;
    protected final NamedTypeSymbol originalDefinition;
    protected final TypeSymbol[] typeArguments;

    protected ConstructedNamedTypeSymbol(NamedTypeSymbol constructedFrom, NamedTypeSymbol originalDefinition, TypeSymbol[] typeArguments) {
        super(constructedFrom.definingModule, constructedFrom.flags, constructedFrom.name, constructedFrom.namespace, constructedFrom.typeParameters);
        this.constructedFrom = constructedFrom;
        this.originalDefinition = originalDefinition;
        this.typeArguments = typeArguments;
    }
}
