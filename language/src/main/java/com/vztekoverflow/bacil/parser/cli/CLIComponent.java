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
import com.vztekoverflow.bacil.parser.signatures.SignatureReader;
import com.vztekoverflow.bacil.parser.signatures.TypeSig;
import com.vztekoverflow.bacil.runtime.BACILContext;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.bacil.BACILComponent;
import com.vztekoverflow.bacil.runtime.bacil.MethodStub;
import com.vztekoverflow.bacil.runtime.bacil.internalcall.InternalCallFinder;
import com.vztekoverflow.bacil.runtime.types.CLIType;
import com.vztekoverflow.bacil.runtime.types.Type;
import org.graalvm.polyglot.io.ByteSequence;

/**
 * A class representing a CLI Component, as described in I.9.1 Components and assemblies.
 */
public class CLIComponent extends BACILComponent {

    private final CLIHeader cliHeader;
    private final CLIMetadata cliMetadata;
    private final AssemblyIdentity assemblyIdentity;

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

    /**
     * Get a {@link ByteSequenceBuffer} representing the data of this component starting at the specified RVA.
     * @param RVA the RVA position to start at
     * @return a {@link ByteSequenceBuffer} starting at the specified RVA
     */
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

    /**
     * Parse component metadata from a PE file.
     * @param bytes a {@link ByteSequence} representing the PE bytes
     * @param source a Truffle {@link Source} containing information on the source file
     * @param context the {@link BACILContext} for which to load the component
     * @return a {@link CLIComponent} representation of the component
     */
    public static CLIComponent parseComponent(ByteSequence bytes, Source source, BACILContext context) {
        //Noone should load an assembly in compiled code.
        CompilerAsserts.neverPartOfCompilation();

        PEFile peFile = PEFile.create(bytes);

        final int cliHeaderOffset = peFile.getFileOffsetForCLIHeader();
        if (cliHeaderOffset < 0 || cliHeaderOffset > bytes.length())
            throw new BACILParserException("Unexpected offset of CLI header.");

        ByteSequenceBuffer dataBuffer = new ByteSequenceBuffer(bytes);
        dataBuffer.setPosition(cliHeaderOffset);

        //Read CLI Header (II.25.3.3 CLI header)
        CLIHeader cliHeader = CLIHeader.read(dataBuffer);

        if (cliHeader.getMetaData().getSize() <= 0)
            throw new BACILParserException("Unexpected empty CLI metadata.");

        final int cliMetadataOffset = peFile.getFileOffsetForRVA(cliHeader.getMetaData().getRva());

        if (cliMetadataOffset < 0 || cliMetadataOffset > bytes.length())
            throw new BACILParserException("Unexpected offset of CLI metadata.");

        dataBuffer.setPosition(cliMetadataOffset);

        //Read CLI Metadata (II.24 Metadata physical layout)
        CLIMetadata cliMetadata = CLIMetadata.read(dataBuffer);

        //Create table pointers (II.22 Metadata logical format: tables)
        CLITables tables = new CLITables(cliMetadata.getStream("#~", bytes));

        final byte[] blobHeap = cliMetadata.getStreamBytes("#Blob", bytes);
        final byte[] stringHeap = cliMetadata.getStreamBytes("#Strings", bytes);
        final byte[] guidHeap = cliMetadata.getStreamBytes("#GUID", bytes);
        final byte[] USHeap = cliMetadata.getStreamBytes("#US", bytes);


        return new CLIComponent(cliHeader, cliMetadata, blobHeap, stringHeap, guidHeap, USHeap, tables, peFile, context);

    }

    /**
     * Find a method represented by the specified token. This method can be called from a compiled context,
     * but ONLY for local methods that were already resolved before; it probably shouldn't be on your hot path anyway.
     *
     * The token can be a pointer to MethodDef or MemberRef.
     * @param token token representing the method
     * @return a {@link BACILMethod} representing the method
     */
    public BACILMethod getMethod(CLITablePtr token)
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

    /**
     * Get a method defined in another component.
     * @param token token representing the method
     * @return a {@link BACILMethod} representing the method
     */
    private BACILMethod getForeignMethod(CLITablePtr token)
    {
        //As we potentially have to load an assembly, make sure we are not in compiled code.
        CompilerAsserts.neverPartOfCompilation();

        //Find the type defining the method
        CLIMemberRefTableRow memberRef = getTableHeads().getMemberRefTableHead().skip(token);
        CLITypeRefTableRow typeRef = getTableHeads().getTypeRefTableHead().skip(memberRef.getKlass());
        Type type = getForeignType(typeRef);

        //Stub the method if specified by command-line args
        if(context.isStubbed(memberRef.getName().read(stringHeap)))
        {
            return new MethodStub(getBuiltinTypes(), getLanguage(),
                    MethodDefSig.read(memberRef.getSignature().read(blobHeap), this),
                    type);
        }

        //Get the method from the type
        return type.getMemberMethod(memberRef.getName().read(stringHeap), MethodDefSig.read(memberRef.getSignature().read(blobHeap), this));
    }

    /**
     * Find a (local) type which defines a method by walking the types table.
     * @param methodDef the method to find the type for
     * @return the defining {@link Type} or null if not found
     */
    public Type findDefiningType(CLIMethodDefTableRow methodDef)
    {
        if(getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF) == 0) {
            return null;
        }

        //Walk the TypeDef table and find the row, for which the methodList is before the target methodDef
        //and the methodList of the next type is after the target methodDef.
        CLITypeDefTableRow previous = getTableHeads().getTypeDefTableHead();
        if(methodDef.getRowNo() < previous.getMethodList().getRowNo() )
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


    /**
     * Find a (local) type which defines a field by walking the types table.
     * @param field the field to find the type for
     * @return the defining {@link Type} or null if not found
     */
    public Type findDefiningType(CLIFieldTableRow field)
    {
        if(getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF) == 0) {
            return null;
        }

        //Walk the TypeDef table and find the row, for which the fieldList is before the target field
        //and the fieldList of the next type is after the target field.
        CLITypeDefTableRow previous = getTableHeads().getTypeDefTableHead();
        if(field.getRowNo() < previous.getFieldList().getRowNo() )
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

    /**
     * Get a method defined in this component.
     * @param token token representing a local method in the MethodDef table
     * @return a {@link BACILMethod} representing the method
     */
    public BACILMethod getLocalMethod(CLITablePtr token)
    {

        assert(token.getTableId() == CLITableConstants.CLI_TABLE_METHOD_DEF);

        //Check if we already have the method loaded
        if(localMethods[token.getRowNo()-1] == null)
        {
            //it's the responsibility of the caller to not be in compilation when this can fail
            CompilerAsserts.neverPartOfCompilation();
            CLIMethodDefTableRow methodDef = tables.getTableHeads().getMethodDefTableHead().skip(token);
            return getLocalMethod(methodDef, findDefiningType(methodDef));
        }

        return localMethods[token.getRowNo()-1];
    }

    /**
     * Get a method defined in this component and in the specified type.
     * @param method a local method in the MethodDef table
     * @return a {@link BACILMethod} representing the method
     */
    public BACILMethod getLocalMethod(CLIMethodDefTableRow method, Type type)
    {

        if(localMethods[method.getRowNo()-1] == null)
        {
            //it's the responsibility of the caller to not be in compilation when this can fail
            CompilerAsserts.neverPartOfCompilation();

            //internal call methods aren't in the component and we must provide the implementation ourselves
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
                localMethods[method.getRowNo()-1] = new CILMethod(method, this, type);
            }

        }

        return localMethods[method.getRowNo()-1];
    }

    /**
     * Get a type defined in another component.
     * @param typeRef a type reference from the TypeRef table
     * @return a {@link Type} representing the type
     */
    public Type getForeignType(CLITypeRefTableRow typeRef)
    {
        //Resolve assembly
        CLIAssemblyRefTableRow assemblyRef = getTableHeads().getAssemblyRefTableHead().skip(typeRef.getResolutionScope());
        AssemblyIdentity assemblyRefIdentity = AssemblyIdentity.fromAssemblyRefRow(stringHeap, assemblyRef);
        BACILComponent assembly = context.getAssembly(assemblyRefIdentity);

        //Resolve type
        String typeName = typeRef.getTypeName().read(stringHeap);
        String typeNamespace = typeRef.getTypeNamespace().read(stringHeap);
        return assembly.findLocalType(typeNamespace, typeName);
    }

    /**
     * Find a type represented by the specified token.
     *
     * The token can be a pointer to a TypeDef, TypeSpec or TypeRef.
     * @param token token representing the type
     * @return a {@link Type} representing the type
     */
    public Type getType(CLITablePtr token)
    {
        if(token.getTableId() == CLITableConstants.CLI_TABLE_TYPE_REF)
        {
            return getForeignType(getTableHeads().getTypeRefTableHead().skip(token));
        } else {
            return getLocalType(token);
        }
    }

    /**
     * Find a type in this component by namespace and name.
     * @param namespace the namespace of the target type
     * @param name the name of the target type
     * @return the {@link Type} if found or null
     */
    @Override
    public Type findLocalType(String namespace, String name)
    {
        //Check typeDefs
        for(CLITypeDefTableRow row : getTableHeads().getTypeDefTableHead())
        {
            if(row.getTypeNamespace().read(stringHeap).equals(namespace) && row.getTypeName().read(stringHeap).equals(name))
                return getLocalType(row);
        }

        //Check exported types (II.6.8 Type forwarders)
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

    /***
     * Get a local type defined in the TypeDef table.
     * @param typeDef the typeDef table row
     * @return the {@link Type} representing the type
     */
    public Type getLocalType(CLITypeDefTableRow typeDef)
    {
        if(localDefTypes[typeDef.getRowNo()-1] == null)
        {
            CompilerAsserts.neverPartOfCompilation();
            localDefTypes[typeDef.getRowNo()-1] = CLIType.fromTypeDef(typeDef, this);
        }
        return localDefTypes[typeDef.getRowNo()-1];
    }

    /***
     * Get a local type defined in the TypeSpec table.
     * @param typeSpec the typeSpec table row
     * @return the {@link Type} representing the type
     */
    public Type getLocalType(CLITypeSpecTableRow typeSpec)
    {
        if(localSpecTypes[typeSpec.getRowNo()-1] == null)
        {
            CompilerAsserts.neverPartOfCompilation();
            SignatureReader reader = new SignatureReader(typeSpec.getSignature().read(blobHeap));
            localSpecTypes[typeSpec.getRowNo()-1] = TypeSig.read(reader, this);
        }
        return localSpecTypes[typeSpec.getRowNo()-1];
    }


    /**
     * Get a type defined in this component.
     *
     * Token can point to a TypeDef or TypeSpec row.
     * @param token token representing a local type in the MethodDef table
     * @return a {@link Type} representing the type.
     */
    public Type getLocalType(CLITablePtr token)
    {
        if(token.getTableId() == CLITableConstants.CLI_TABLE_TYPE_DEF)
        {
            return getLocalType(tables.getTableHeads().getTypeDefTableHead().skip(token));
        } else if (token.getTableId() == CLITableConstants.CLI_TABLE_TYPE_SPEC)
        {
            return getLocalType(tables.getTableHeads().getTypeSpecTableHead().skip(token));
        } else {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unexpected ptr to table " + token.getTableId());
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

    public BACILLanguage getLanguage() {
        return context.getLanguage();
    }

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

    @Override
    public String toString() {
        return assemblyIdentity.getName();
    }

    private String getFriendlyName(String namespace, String name)
    {
        if(!namespace.equals(""))
        {
            return namespace + "." + name;
        }
        return name;
    }

    @Override
    public String[] getAvailableTypes() {
        final int defCount = tables.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF);
        final int expCount = tables.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_EXPORTED_TYPE);
        String[] types = new String[defCount + expCount];


        for(CLITypeDefTableRow row : getTableHeads().getTypeDefTableHead())
        {
            types[row.getRowNo()-1] = getFriendlyName(row.getTypeNamespace().read(stringHeap), row.getTypeName().read(stringHeap));
        }

        for(CLIExportedTypeTableRow row : getTableHeads().getExportedTypeTableHead())
        {
            types[defCount+row.getRowNo()-1] = getFriendlyName(row.getTypeNamespace().read(stringHeap), row.getTypeName().read(stringHeap));
        }
        return types;
    }
}
