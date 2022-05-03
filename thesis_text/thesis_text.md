## Designing a CLI Component parser

Before being able to execute code, it is necessary to read the code from the assemblies. Prior to starting the work, we expected some open source parsers for this format to exist for various languages, including Java. However, the only alternative stand-alone parser (not a component of a full CLI implementation) we found was [dnlib](https://github.com/0xd4d/dnlib) targeting .NET framework itself. If even for such a popular runtime there are no suitable parsers implemented in Java, we feel that the parser implementation step is an important part to consider in the whole "Building an experimental runtime" picture.

In the end, designing and implementing the parser took a non-trivial chunk of the development time. Even though most code-size indicators are arbitrary (_TODO citation_), we feel that the parser (excluding generated code) consisting of 124131 bytes over 3625 lines of code and the rest of the language package consisting of 256063 over 7103 lines is indicative of the substantiality of the parser.

### Design goals

Before we started designing and implementing the parser, we considered what additional constraints have to be put on a parser in order for it to be partial-evaluation friendly. For partial-evaluation friendliness, the key metric is how trivial can every piece of code (that can be called a hot path) be partially evaluated to. While our goal was for the parser to never be called on a hot path, for some possible scenarios including reflection it would be necessary.

There are two possible extremes for parser design: "fully lazy" where every query for the file causes it to be parsed from the start, and "fully preloaded" where all the data from the file is immediatelly fully parsed into hierarchies of objects and structures. Practical parsers usually choose a compromise between those two approaches, mainly because the extremes lead to extremely slow runtime or bootup respectively.

Driven by the goal of partial-evaluation friendliness, we decided to design the initial parsing such that:

* trivial queries, e.g. queries for a metadata item at a constant index, would only result in a compilation constant,
* simple queries, e.g. queries for a metadata item at a variable index, would result in a simple offset calculation (multiply and add) and reads from a compilation constant byte[],
* all further parsing necessary for more complex queries (creating objects representing metadata concepts etc.) would be performed lazily, callers should make sure to cache their Objects themselves.



### Definition of important CLI component structures

Apart from various headers used for locating it, all metadata is stored in streams. There are two basic types of streams: heaps and tables.

Heaps contain sequences of bytes, the meaning of which changes based on the specific heap. The specification defines 4 heaps:

* `#Strings` containing values of identifier strings.
* `#US` containing values of strings used by the program code itself during runtime.
* `#Blob` containing variable-length metadata as binary blobs.
* `#GUID` containing GUIDs.

The tables are stored in a stream called `#~`. This is the root of all metadata information. The specification describes 38 tables. Cell values can be either  a constant or an index. Indices can point to heaps (the value is a byte offset),
another table (the value is a row number), or one of multiple tables (the value is a "coded index" specifying both the table and row number).

For an example of references between these structures, this is what a single row looks like in the metadata table `TypeDef`, which contains definitions of types:

| Column        | Raw value | Comment                                                                                                                                                                             |
|---------------|-----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Flags         | 0x100000  | Constant bitmask specifying TypeAttributes.                                                                                                                                         |
| TypeName      | 0x01A9    | An offset into the `#String` heap, where the name of the type can be found. In this example, `Program` was written there.                                                           |
| TypeNamespace | 0x01DE    | An offset into the `#String` heap, where the namespace of the type can be found. In this example, `SampleProject` was written there.                                                |
| Extends       | 0x0031    | A coded index into `TypeDef`, `TypeRef`, or `TypeSpec`. In this example an index to `TypeRef` table index 12, which is a reference to `System.Object`.                              |
| FieldList     | 0x0002    | An index into the `Field` table where fields for this type start. As in this case the type has no fields, the index points past the end of the `Field` table, which has only 1 row. |
| MethodList    | 0x0002    | An index into the `Method` table where methods for this type start. As this type has multiple methods, row 2 of `Method` contains information about `Main`, other methods follow.   |


### Complexities of the CLI component format

Subjectively, we felt the format used by CLI components is not designed well in regards to supporting different parsing approaches and platforms. To substantiate this claim, we want to highlight several factors that complicate parsing the components.

#### PE Wrapping

As stated in ECMA-335 _II.25 File format extensions to PE_:
> The file format for CLI components is a strict extension of the current Portable Executable (PE) File Format. [...] The PE format frequently uses the term RVA (Relative Virtual Address). An RVA is the address of an
item _once loaded into memory_ [...].
> 
> The RVA of an item will almost always differ from its
position within the file on disk. To compute the file position of an item with RVA r, search all the sections in the PE file to find the section with RVA _s_, length _l_ and file position _p_ in which the RVA lies, ie _s ≤ r < s+l_. The file position of the item is then given by _p+(r-s)_.

On Windows (and other theoretical platforms where PE parsing is a service provided by the operating system) this allows for the component to be loaded into virtual memory as any other executable files. RVA addresses can then be resolved transparently by the CPU's and operating system's virtual memory mappings. For all other platforms this adds one more level of indirection that needs to be handled.

As our parser is platform-agnostic and written in Java, we can't use any of those services. Therefore, we need to manually do the sections search and RVA calculations as described in the standard.

#### Parsing metadata tables

The biggest complexity we encountered during parser design was the format of metadata tables. These tables contain most of the metadata information of the CLI component. 

The data of the tables is stored in the `#~` stream. This stream consists of a header followed by a simple concatenation of values of all rows of all tables with no additional metadata in between. 

The header itself contains only the following fields relevant for locating data in the tables (shortened, for full structure see _II.24.2.6 #~ stream_):

| Offset | Size | Field     | Description                                                                              |
|--------|------|-----------|------------------------------------------------------------------------------------------|
| 6      | 1    | HeapSizes | Bit vector of heap sizes                                                                 |
| 8      | 8    | Valid     | Bit vectors of present tables                                                            |
| 24     | 4*n  | Rows      | Array of n 4-byte unsigned integers indicating the number of rows for each present table |

Herein lies the first issue: no information about table lengths are present. This results in **every single parser implementing the format having to implement the format for every single metadata table**, as skipping a table requires knowing the byte length of its rows. This completely prohibits an iterative developement cycle, adding support for only the necessary tables. For example, to implement an utility that only outputs names of all the types available in the component, while only data from the TypeDef table is necessary, all 38 tables defined in ECMA-335 must be implemented. The BACIL implementation described here only accesses 11 of these tables.

The second caveat comes in _II.22 Metadata logical format: tables_ and _II.24.2.6 #~ stream_:

> Each entry in each column of each table is either a constant or an index.
> 
> [...]
> 
> Each index is either 2 or 4 bytes wide. The index points into the same or another table, or into one of the four heaps. The size of each index column in a table is only made 4 bytes if it needs to be for that particular module. So, if a particular column indexes a table, or tables, whose highest row number fits
in a 2-byte value, the indexer column need only be 2 bytes wide. Conversely, for tables containing 64K or more rows, an indexer of that table will be 4 bytes wide. 
> 
> [...]
> 
> If _e_ is a _coded index_ that points into table _t<sub>i</sub>_ out of _n_ possible tables _t<sub>0</sub>, … t<sub>n-1</sub>_, then it is stored as e << (log n) | tag{ t0, …tn-1}[ t<sub>i</sub>] using 2 bytes if the maximum number of rows of tables  _t<sub>0</sub>, … t<sub>n-1</sub>_, is less than 2<sup>(16 – (log n))</sup>, and using 4 bytes otherwise. 

While this decision saves storage size it means that table row length can't be considered a constant and is dependant on the row count of other tables. For example, a `TypeDef` table row can be from 14 up to 24 bytes in size.

This means that the parser can't workaround the first issue by expecting the table row length be constant.

If we were to try to improve the format to alleviate these issues, we would add information about the row length of present tables into the header. Even if each size was stored as a full byte (which all tables defined in the standard fit into), this would at most increase the binary size by 38 bytes and allow for skipping tables without dealing with their internal row format.

#### Extensive normalisation

File format design is often a compromise between several engineering goals[^1]. One of the design concepts that apply is normalisation, a concept that each information should be stored only once, removing all redundancy. While such a goal can be beneficiary for other uses of the file format (like writing and modifying), from the point of view of a lightly-preloading consumer it results in non-ideal structures.

* In parent-child relationships, only one of the nodes has a direct reference to the other. Traversing the edge from the other side involves enumerating all the possible nodes and searching for one with the appropriate reference. If such queries are performance sensitive, the invoker has to make sure that it caches the answers.

* When referencing a sequence of items in a table, only information about the beginning of the sequence is directly stored. The end of the sequence is either the last row of the table or the start of the next sequence, as specified by the next row, whichever comes first.

    While the complexity this adds usually amounts to a single if statement, it crosses the border between cell value semantics and metadata logical format internals - either the parser has to understand the semantics of cells as "sequence indices" to encapsulate resolving the sequence length, or the invoker has to understand the file format view of row numbers. We decided to leave the responsibility on the invoker, resulting in code like this (from [CLIType](https://github.com/jagotu/BACIL/blob/master/language/src/main/java/com/vztekoverflow/bacil/runtime/types/CLIType.java#L72)):

    ```Java
    if(type.hasNext())
    {
        methodsEnd = type.next().getMethodList().getRowNo();
        fieldRowsEnd = type.next().getFieldList().getRowNo();
    } else {
        methodsEnd = component.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_METHOD_DEF)+1;
        fieldRowsEnd = component.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_FIELD)+1;
    }
    ```

[^1]: [A brief look at file format design](http://decoy.iki.fi/texts/filefd/filefd)

### Parsing sequence details

Parsing source files in Truffle starts with a [ByteSequence](https://www.graalvm.org/truffle/javadoc/org/graalvm/polyglot/io/ByteSequence.html) representing the content of the file being loaded. From there, we will:

1. Parse necessary PE file structures as specified in _II.25 File format extensions to PE_, specifically all of _II.25.2 PE headers_ and the _II.25.3 Section headers_ used for resolving RVAs.

2. Resolve CLI Header RVA from PE header data directories, and parse it as described in _II.25.3.3 CLI header_.

3. Resolve physical metadata RVA from CLI Header and parse it as described in _II.24.2.1 Metadata root_.

4. Read all 4 heaps (`#Strings`, `#US`, `#Blob`, `#GUID`) into byte arrays for easy access.

5. Read the metadata tables header from the `#~` stream and pre-calculate offsets of each table.

### Table parsing

TODO
* table parser code generator
* the fact we decided to break standard by not handling tables with more than 65535 rows as we want simple indices to always have 2 bytes
* the API of tables and tableptrs


## Dynamicity of references

One of the additional things to consider when implementing a partial-evaluation friendly interpreter is dynamicity of references, where by dynamicity we mean how often the reference changes its state. This metric is important because effectively the dynamicity of a chain of references will be equal to the most dynamic of the references. As a result, what would usually be considered bad design patterns is sometimes necessary to divide the chain into more direct references, such that each object is reachable with the lowest dynamicity possible.

The following reference graph shows the refactoring in a generic case:

![alt](dynamicity_generic.drawio.svg)

_Scenario 1: Reference chain results in class B being accessible with high dynamicity and therefore not being effectively partially evaluated_

![alt](dynamicity_generic_good.drawio.svg)

_Scenario 2: Class B is accessible with a low dynamicity reference, resulting in more effective partial evaluation_

For a case study from the BACIL implementation, let's consider the design decisions behind `LocationDescriptor` and `LocationHolder`. Each location has a type and a value. While the value itself (and the type of the value) changes based on the running code, the type of the location never changes. This is a perfect example of two pieces of information with different dynamicity. 

Even from regular developement patterns, it makes sense to divide location values and location types into separate classes - store the location type information in the metadata as a "prototype" for then creating the value storage based on it. In BACIL, `LocationDescriptor` contains the Type information and `LocationHolder` contains the actual values.

To work with the values, it is always necessary to know the location type (mainly to differentiate between ValueTypes and references). The rule of encapsulation would dictate that the final caller doesn't need to know that there's a `LocationDescriptor` tied to the `LocationHolder`, as it's an internal detail. Such an implementation would look something like this:

```Java 
public class LocationHolder {
    private final LocationDescriptor descriptor;

    private final Object[] refs;
    private final long[] primitives;

    public LocationsHolder(LocationDescriptor descriptor) {
        this.descriptor = descriptor;
        refs = new Object[descriptor.getRefCount()];
        primitives = new long[descriptor.getPrimitiveCount()];
    }

    public Object locationToObject(int locationIndex)
    {
        return descriptor.locationToObject(this, locationIndex);
    }

}

//Accessing a field of an object
Object fieldValue = ((StaticObject)object).getLocationsHolder().locationToObject(0);
```

However, using this code results in an unoptimal dynamicity chain and uneffective partial evaluation:

![alt](dynamicity_case.drawio.svg)

_Scenario 1: As a `LocationHolder` is unique per object instance/method invocation/ etc., the reference to it is highly dynamic. The `LocationDescriptor` is only unique per object type/method definiton, but can only be reached through a dynamic chain._

To make this more effective, we have to hold a separate reference to a `LocationDescriptor`. As every location-accessing instruction (in the implemented subset of .NET) will always use the same `LocationDescriptor`, this results in effective partial evaluation. The new implementation looks like this:

```Java 
public class LocationHolder {

    private final Object[] refs;
    private final long[] primitives;

    public LocationsHolder(int refCount, int primitiveCount) {
        refs = new Object[refCount];
        primitives = new long[primitiveCount];
    }
}

//Accessing a field of an object
//objectType for an instruction never changes!
Object fieldValue = objectType.getLocationsDescriptor().locationToObject(((StaticObject)object).getLocationsHolder(), 0);
```

![alt](dynamicity_case_good.drawio.svg)

_Scenario 2: While the `LocationHolder` remains accessible from a highly dynamic chain, the `LocationDescriptor` is accessible through a static chain. This means the bottom chain will be partially evaluated._