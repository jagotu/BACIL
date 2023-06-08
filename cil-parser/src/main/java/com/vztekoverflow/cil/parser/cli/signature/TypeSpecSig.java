package com.vztekoverflow.cil.parser.cli.signature;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;

public class TypeSpecSig {
    private final ElementTypeFlag flag;
    private final CustomMod[] mod;
    private final TypeSig typeSig;
    private final MethodDefSig mDefSig;
    private final MethodRefSig mRefSig;
    private final ArrayShapeSig arrayShapeSig;
    private final int genArgCount;
    private final TypeSig[] typeArgs;
    private final CLITablePtr genType;

    public ElementTypeFlag getFlag() { return flag; }
    public TypeSig[] getTypeArgs() { return typeArgs; }
    public CLITablePtr getGenType() {return genType;}

    private TypeSpecSig(ElementTypeFlag flag, CustomMod[] mod, TypeSig typeSig, MethodDefSig mDefSig, MethodRefSig mRefSig, ArrayShapeSig arrayShapeSig, int genArgCount, TypeSig[] typeArgs, CLITablePtr genType) {
        this.flag = flag;
        this.mod = mod;
        this.typeSig = typeSig;
        this.mDefSig = mDefSig;
        this.mRefSig = mRefSig;
        this.arrayShapeSig = arrayShapeSig;
        this.genArgCount = genArgCount;
        this.typeArgs = typeArgs;
        this.genType = genType;
    }


    public static TypeSpecSig read(SignatureReader reader, CLIFile file)
    {
        ElementTypeFlag flag = new ElementTypeFlag(reader.getUnsigned());
        CustomMod[] mod = null;
        TypeSig typeSig = null;
        MethodDefSig mDefSig = null;
        MethodRefSig mRefSig = null;
        ArrayShapeSig arrayShapeSig = null;
        int genArgCount = 0;
        TypeSig[] typeArgs = null;
        CLITablePtr genType = null;

        if (flag.getFlag() == ElementTypeFlag.Flag.ELEMENT_TYPE_GENERICINST)
        {
            reader.getUnsigned();
            genType = CLITablePtr.fromTypeDefOrRefOrSpecEncoded(reader.getUnsigned());
            genArgCount = reader.getUnsigned();
            typeArgs = new TypeSig[genArgCount];
            for (int i = 0; i < typeArgs.length; i++) {
                typeArgs[i] = TypeSig.read(reader, file);
            }
        }

        return new TypeSpecSig(flag, mod, typeSig, mDefSig, mRefSig, arrayShapeSig, genArgCount, typeArgs, genType);
    }
}
