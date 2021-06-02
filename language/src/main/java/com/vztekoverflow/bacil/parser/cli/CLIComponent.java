package com.vztekoverflow.bacil.parser.cli;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.source.Source;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablesHeader;
import com.vztekoverflow.bacil.parser.cli.tables.generated.*;
import com.vztekoverflow.bacil.parser.pe.PEFile;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.BACILContext;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.bacil.BACILComponent;
import com.vztekoverflow.bacil.runtime.bacil.MethodStub;
import com.vztekoverflow.bacil.runtime.bacil.internalcall.InternalCallFinder;
import com.vztekoverflow.bacil.runtime.types.NamedType;
import com.vztekoverflow.bacil.runtime.types.Type;
import org.graalvm.polyglot.io.ByteSequence;

public class CLIComponent extends BACILComponent {

    private final CLIHeader cliHeader;
    private final CLIMetadata cliMetadata;
    private final AssemblyIdentity assemblyIdentity;

    public BACILLanguage getLanguage() {
        return context.getLanguage();
    }

    private final BACILContext context;

    @CompilationFinal(dimensions = 1)
    private final byte[] blobHeap;
    @CompilationFinal(dimensions = 1)
    private final byte[] stringHeap;
    @CompilationFinal(dimensions = 1)
    private final byte[] guidHeap;
    @CompilationFinal(dimensions = 1)
    private final byte[] USHeap;

    @CompilationFinal(dimensions = 1)
    protected final Type[] localDefTypes;

    @CompilationFinal(dimensions = 1)
    private final Type[] localSpecTypes;

    @CompilationFinal(dimensions = 1)
    private final BACILMethod[] localMethods;

    private final PEFile pe;



    public CLIHeader getCliHeader() {
        return cliHeader;
    }

    public CLIMetadata getCliMetadata() {
        return cliMetadata;
    }

    private final CLITables tables;

    public byte[] getBlobHeap() {
        return blobHeap;
    }

    public byte[] getStringHeap() {
        return stringHeap;
    }

    public byte[] getGuidHeap() {
        return guidHeap;
    }

    public byte[] getUSHeap() {
        return USHeap;
    }

    @Override
    public AssemblyIdentity getAssemblyIdentity() {
        return assemblyIdentity;
    }

    private static final class FindTypeResult
    {
        final CLIComponent definingAssembly;
        final CLITypeDefTableRow typeDef;

        public FindTypeResult(CLIComponent definingAssembly, CLITypeDefTableRow typeDef) {
            this.definingAssembly = definingAssembly;
            this.typeDef = typeDef;
        }

    }

    public int getFileOffsetForRVA(int RVA) {
        return pe.getFileOffsetForRVA(RVA);
    }

    public ByteSequenceBuffer getBuffer(int RVA) {
        ByteSequenceBuffer buf = new ByteSequenceBuffer(pe.getBytes());
        buf.setPosition(pe.getFileOffsetForRVA(RVA));
        return buf;
    }

    public CLIComponent(CLIHeader cliHeader, CLIMetadata cliMetadata, byte[] blobHeap, byte[] stringHeap, byte[] guidHeap, byte[] USHeap, CLITables tables, PEFile pe, BACILContext context) {
        super(context.getLanguage());
        this.cliHeader = cliHeader;
        this.cliMetadata = cliMetadata;
        this.blobHeap = blobHeap;
        this.stringHeap = stringHeap;
        this.guidHeap = guidHeap;
        this.USHeap = USHeap;
        this.tables = tables;
        this.pe = pe;
        this.context = context;

        localMethods = new BACILMethod[tables.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_METHOD_DEF)];
        localDefTypes = new Type[tables.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF)];
        localSpecTypes = new Type[tables.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_SPEC)];

        final int assemblyDefCount = tables.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_ASSEMBLY);
        if(assemblyDefCount > 1)
        {
            throw new BACILParserException("File with more than 1 assembly definition");
        } else if (assemblyDefCount == 1)
        {
            assemblyIdentity = AssemblyIdentity.fromAssemblyRow(stringHeap, tables.getTableHeads().getAssemblyTableHead());
        } else {
            assemblyIdentity = null;
        }

    }

    public static CLIComponent parseComponent(ByteSequence bytes, Source source, BACILContext context, boolean isCoreLib) {
        CompilerAsserts.neverPartOfCompilation();

        PEFile peFile = PEFile.create(bytes);

        final int cliHeaderOffset = peFile.getFileOffsetForCLIHeader();
        if (cliHeaderOffset < 0 || cliHeaderOffset > bytes.length())
            throw new BACILParserException("Unexpected offset of CLI header.");

        ByteSequenceBuffer dataBuffer = new ByteSequenceBuffer(bytes);
        dataBuffer.setPosition(cliHeaderOffset);

        CLIHeader cliHeader = CLIHeader.read(dataBuffer);

        if (cliHeader.getMetaData().getSize() <= 0)
            throw new BACILParserException("Unexpected empty CLI metadata.");

        final int cliMetadataOffset = peFile.getFileOffsetForRVA(cliHeader.getMetaData().getRva());

        if (cliMetadataOffset < 0 || cliMetadataOffset > bytes.length())
            throw new BACILParserException("Unexpected offset of CLI metadata.");

        dataBuffer.setPosition(cliMetadataOffset);

        CLIMetadata cliMetadata = CLIMetadata.read(dataBuffer);


        CLITables tables = new CLITables(cliMetadata.getStream("#~", bytes));

        final byte[] blobHeap = cliMetadata.getStream("#Blob", bytes).toByteArray();
        final byte[] stringHeap = cliMetadata.getStream("#Strings", bytes).toByteArray();
        final byte[] guidHeap = cliMetadata.getStream("#GUID", bytes).toByteArray();
        final byte[] USHeap = cliMetadata.getStream("#US", bytes).toByteArray();


        return new CLIComponent(cliHeader, cliMetadata, blobHeap, stringHeap, guidHeap, USHeap, tables, peFile, context);

    }

    public static CLIComponent parseComponent(ByteSequence bytes, Source source, BACILContext context) {
        return parseComponent(bytes, source, context, false);
    }

    public BACILMethod getMethod(CLITablePtr token, BACILContext context)
    {
        switch(token.getTableId())
        {
            case CLITableConstants.CLI_TABLE_METHOD_DEF:
                return getLocalMethod(token);

            case CLITableConstants.CLI_TABLE_MEMBER_REF:
                return getForeignMethod(token);

            default:
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new BACILInternalError("Cannot resolve method from table " + token.getTableId());
        }
    }

    public BACILMethod getForeignMethod(CLITablePtr token)
    {
        CompilerAsserts.neverPartOfCompilation();

        CLIMemberRefTableRow memberRef = getTableHeads().getMemberRefTableHead().skip(token);
        CLITypeRefTableRow typeRef = getTableHeads().getTypeRefTableHead().skip(memberRef.getKlass());
        Type type = getForeignType(typeRef);

        if(context.isStubbed(memberRef.getName().read(stringHeap)))
        {
            return new MethodStub(getBuiltinTypes(), getLanguage(),
                    MethodDefSig.read(memberRef.getSignature().read(blobHeap), this),
                    type);
        }
        return type.getMemberMethod(memberRef.getName().read(stringHeap), memberRef.getSignature().read(blobHeap));
    }




    public Type findDefiningType(CLIMethodDefTableRow methodDef)
    {
        if(getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF) == 0) {
            return null;
        }

        CLITypeDefTableRow previous = getTableHeads().getTypeDefTableHead();
        if(methodDef.getRowNo() < previous.getMethodList().getRowNo() ) //can not belong to any type only when it's before the first type
        {
            return null;
        } else if (!previous.hasNext())
        {
            return getLocalType(previous);
        }

        CLITypeDefTableRow next = previous.next();
        while(next.hasNext() && methodDef.getRowNo() >= next.getMethodList().getRowNo())
        {
            previous = next;
            next = next.next();
        }

        if(methodDef.getRowNo() < next.getMethodList().getRowNo())
        {
            return getLocalType(previous);
        }
        return getLocalType(next);


    }

    public Type findDefiningType(CLIFieldTableRow field)
    {
        if(getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF) == 0) {
            return null;
        }

        CLITypeDefTableRow previous = getTableHeads().getTypeDefTableHead();
        if(field.getRowNo() < previous.getFieldList().getRowNo() ) //can not belong to any type only when it's before the first type
        {
            return null;
        } else if (!previous.hasNext())
        {
            return getLocalType(previous);
        }

        CLITypeDefTableRow next = previous.next();
        while(next.hasNext() && field.getRowNo() >= next.getFieldList().getRowNo())
        {
            previous = next;
            next = next.next();
        }

        if(field.getRowNo() < next.getFieldList().getRowNo())
        {
            return getLocalType(previous);
        }
        return getLocalType(next);
    }

    public BACILMethod getLocalMethod(CLITablePtr token)
    {


        if(localMethods[token.getRowNo()-1] == null)
        {
            //it's the responsibility of method finder to not be in compilation when this can fail
            CompilerAsserts.neverPartOfCompilation();
            CLIMethodDefTableRow methodDef = tables.getTableHeads().getMethodDefTableHead().skip(token);
            return getLocalMethod(methodDef, findDefiningType(methodDef));
        }

        return localMethods[token.getRowNo()-1];
    }

    public BACILMethod getLocalMethod(CLIMethodDefTableRow method, Type type)
    {


        if(localMethods[method.getRowNo()-1] == null)
        {
            //it's the responsibility of method finder to not be in compilation when this can fail
            CompilerAsserts.neverPartOfCompilation();
            if(CILMethod.isInternalCall(method))
            {
                BACILMethod internalMethod = InternalCallFinder.FindInternalCallMethod(this, method, type);
                if(internalMethod == null)
                {
                    throw new BACILInternalError("Attempted to resolve InternalCall method for which no implementation is available: " + type.toString() + "." + method.getName().read(getStringHeap()));
                } else {
                    localMethods[method.getRowNo()-1] = internalMethod;
                }

            } else {
                localMethods[method.getRowNo()-1] = new CILMethod(this, method, type);
            }

        }

        return localMethods[method.getRowNo()-1];
    }

    public Type getForeignType(CLITypeRefTableRow typeRef)
    {
        CLIAssemblyRefTableRow assemblyRef = getTableHeads().getAssemblyRefTableHead().skip(typeRef.getResolutionScope());
        AssemblyIdentity assemblyRefIdentity = AssemblyIdentity.fromAssemblyRefRow(stringHeap, assemblyRef);

        String typeName = typeRef.getTypeName().read(stringHeap);
        String typeNamespace = typeRef.getTypeNamespace().read(stringHeap);

        BACILComponent assembly = context.getAssembly(assemblyRefIdentity);


       return assembly.findLocalType(typeNamespace, typeName);
    }

    public Type getType(CLITablePtr ptr)
    {
        if(ptr.getTableId() == CLITableConstants.CLI_TABLE_TYPE_REF)
        {
            return getForeignType(getTableHeads().getTypeRefTableHead().skip(ptr));
        } else {
            return getLocalType(ptr);
        }
    }


    @Override
    public Type findLocalType(String namespace, String name)
    {
        for(CLITypeDefTableRow row : getTableHeads().getTypeDefTableHead())
        {
            if(row.getTypeNamespace().read(stringHeap).equals(namespace) && row.getTypeName().read(stringHeap).equals(name))
                return getLocalType(row);
        }

        for(CLIExportedTypeTableRow row : getTableHeads().getExportedTypeTableHead())
        {
            if(row.getTypeNamespace().read(stringHeap).equals(namespace) && row.getTypeName().read(stringHeap).equals(name))
            {
                CLIAssemblyRefTableRow assemblyRef = getTableHeads().getAssemblyRefTableHead().skip(row.getImplementation());
                BACILComponent c = context.getAssembly(AssemblyIdentity.fromAssemblyRefRow(stringHeap, assemblyRef));
                return c.findLocalType(namespace, name);
            }
        }

        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new BACILInternalError(String.format("Type %s.%s not found.", namespace, name));
    }

    public Type getLocalType(CLITypeDefTableRow typeDef)
    {
        if(localDefTypes[typeDef.getRowNo()-1] == null)
        {
            CompilerAsserts.neverPartOfCompilation();
            localDefTypes[typeDef.getRowNo()-1] = NamedType.fromTypeDef(typeDef, this);
        }
        return localDefTypes[typeDef.getRowNo()-1];
    }

    public Type getLocalType(CLITablePtr ptr)
    {
        if(ptr.getTableId() == CLITableConstants.CLI_TABLE_TYPE_DEF)
        {
            return getLocalType(tables.getTableHeads().getTypeDefTableHead().skip(ptr));
        } else if (ptr.getTableId() == CLITableConstants.CLI_TABLE_TYPE_SPEC)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Not yet implemented!");
        } else {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unexpected ptr to table " + ptr.getTableId());
        }
    }

    public CLITableHeads getTableHeads()
    {
        return tables.getTableHeads();
    }

    public CLITablesHeader getTablesHeader()
    {
        return tables.getTablesHeader();
    }

    @Override
    public String toString() {
        return assemblyIdentity.getName();
    }
}
