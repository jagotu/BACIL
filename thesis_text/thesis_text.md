# Truffle based .NET IL interpreter and compiler: run C# on Java Virtual Machine

_This is not the official copy of the thesis._

TODO: consistent use of past/present tenses

# Abstract

TODO

# Original thesis goal statement

Interpreted and just-in-time (JIT) compiled languages are becoming more and more prevalent, with JavaScript and Python ranking in TOP 5 most popular programming languages in most surveys and statistics. In the academic and research settings, the ability to quickly prototype programming languages and test their properties also works in favor of interpreted languages, as implementing an interpreter is considerably easier than implementing a compiler. However, simple interpreters suffer from a substantial tradeoff of performance. Traditionally, if an interpreted language is to become effective and run fast, it requires creating a custom infrastructure of JIT compilers and optimizations just for that single specific language.

The Truffle Language Implementation Framework [^1] attempts to alleviate this tradeoff between performance and development complexity by providing a framework for implementing a language using interpreter-style code that is later JIT compiled by the GraalVM compiler [^2]. While straightforward interpreter-style implementations in Truffle might not perform much better than simple interpreters, Truffle provides a framework for writing optimization hints that are later used by the compiler. According to Thomas Würthinger et al. [^3], such implementations can compete with hand-crafted compilers.

The goal of the thesis is to implement an interpreter of a subset of Common Intermediate Language (CIL) using Truffle, such that CIL code can be run on the Java Virtual Machine. CIL is an intermediate language to which .NET applications are typically compiled, including applications written in the C# programming language. The CIL is standardized in ECMA-335 [^4]. The focus of the work will be on the feasibility of implementing an interpreter of an actual language using the Truffle interpreter-style approach in an academic setting, and comparing the resulting performance of said academic implementation with state-of-the-art CIL JIT compilers.

# Introduction

## Problem

Traditionally, when implementing a programming language, achieving a high performance required a significant development effort and resulted in complicated codebases.

While writing an interpreter for even a fairly complicated language is achievable for a single person interested in the topic (as proved by the abundance of language implementation theses available), creating state-of-the-art optimizing compilers usually took several years spent by large teams of developers at the largest IT companies. Not only was kick-starting such a project unthinkable for an individual, but even introducing changes to an existing project is far from simple.

For example, as of 2022, Google's state-of-the-art JavaScript engine V8 has two different JIT compilers and its own internal bytecode. An experiment of adding a single new bytecode instruction to the project can mean several days of just orientating in the codebase. Google provides a [step-by-step tutorial for adding a new WebAssembly opcode to v8](https://v8.dev/docs/webassembly-opcode), which admits that a lot of platform-dependant work is necessary to get a proper implementation:

> The steps required for other architectures are similar: add TurboFan machine operators, use the platform-dependent files for instruction selection, scheduling, code generation, assembler.

As cybersecurity becomes a more important topic, another factor to consider is that creating manual optimizations in JITs is prone to bugs which can have grave security implications. Speculated assumptions of JIT compilers introduce whole new bug families. As [Compile Your Own Type Confusions: Exploiting Logic Bugs in JavaScript JIT Engines](http://phrack.org/issues/70/9.html) says (emphasis added, footnotes stripped):

> JavaScript JIT compilers are commonly implemented in C++ and as such are subject to the usual list of memory- and type-safety violations. These are not specific to JIT compilers and will thus not be discussed further. Instead, the focus will be put on bugs in the compiler which lead to incorrect machine code generation which can then be exploited to cause memory corruption.
>
> Besides bugs in the lowering phases which often result in rather classic vulnerabilities like integer overflows in the generated machine code, many interesting bugs come from the various optimizations. There have been bugs in bounds-check elimination, escape analysis, register allocation, and others. **Each optimization pass tends to yield its own kind of vulnerabilities.**

Implementing a JIT that is not only performant but also secure is proving to be difficult even for state-of-the-art projects.

These factors resulted in academic and hobby experimentation with programming languages being mostly stuck with low-performance simple interpreters. [Qualitative Assessment of Compiled, Interpreted and Hybrid Programming Languages](https://www.researchgate.net/publication/320665812_Qualitative_Assessment_of_Compiled_Interpreted_and_Hybrid_Programming_Languages) concludes that

> Interpreters are very good development tools since it [sic] can be easily edited, and are therefore ideal for beginners in programming and software development. However they are not good for professional developers due to the slow execution nature of the interpreted code.

In recent years, frameworks appeared that promise to deliver performance comparable to state-of-the-art JIT compilers while requiring only a simple interpreter-style implementation. Examples of such frameworks are [RPython](https://rpython.readthedocs.io/) and the [Truffle language implementation framework](https://www.graalvm.org/graalvm-as-a-platform/language-implementation-framework/).
Researchers concluded that Truffle's performance "is competitive with production systems even when they have been heavily optimized for the one language they support"[^5].

As the performance aspects of language implementations made by experts (sometimes even designers of these frameworks themselves) are well understood, in this work we want to focus on testing another claim: the "reduced complexity for implementing languages in our system [that] will enable more languages to benefit from optimizing compilers"[^5].

**Is it feasible to achieve the promised performance benefits with an academic interpreter-style implementation of a language runtime?** In order to answer this question, we implement BACIL, a runtime for .NET.

## .NET/CLI

We chose .NET as a platform to implement, mostly because:

* languages targeting .NET consistently rank high on popularity surveys
* we have experience with .NET internals and the internally used bytecode
* no comparable truffle-based implementations were already published for .NET 

While .NET is a well-recognized name, it is a marketing/brand name whose meaning changed through history. Our implementation follows [ECMA-335 Common Language Infrastructure (CLI)](https://www.ecma-international.org/publications-and-standards/standards/ecma-335/) which doesn't mention the .NET brand at all. We will use the names defined in the standard throughout this work. We include all references to specific implementations/brand names only to aid understanding with no ambition to be accurate, mainly for .NET vs .NET Core vs .NET Framework vs .NET Standard nomenclature.

> The Common Language Infrastructure (CLI) provides a specification for executable code and the execution environment (the Virtual Execution System) in which it runs.

.NET languages (like C#) are compiled into "managed code"[^7] - instead of targeting native processor instruction sets, they target the CLI's execution environment.

Using the definitions of the standard, BACIL is actually a Virtual Execution System (VES):

> The VES is responsible for loading and running programs written for the CLI. It provides the services needed to execute managed code and data, using the metadata to connect separately generated modules together at runtime (late binding).

.NET Framework's VES is called the Common Language Runtime (CLR) and in .NET Core, it's known as CoreCLR.

> To a large extent, the purpose of the VES is to provide the support required to execute the [Common Intermediate Language (CIL)] instruction set.

The CIL, historically also called Microsoft Intermediate Language (MSIL) or simply Intermediate Language (IL), is the instruction set used by the CLI. Interpreting (a subset of) this instruction set was the primary goal of this work.

Another large part of the framework is the standard libraries - the base class library, which has to be supported by all implementations of the CLI, comprises 2370 members over 207 classes. As the focus of the work was on the core interpreter, we largely ignore this part of the standard and delegate to other standard library implementations where possible.

## Truffle and Graal

To implement a high-performance CLI runtime, we employ the [Truffle language implementation framework](https://www.graalvm.org/graalvm-as-a-platform/language-implementation-framework/) (henceforth "Truffle") and the [GraalVM Compiler](https://www.graalvm.org/22.1/docs/introduction). These two components are tightly coupled together and we'll mostly be referring to them interchangeably, as even official sources provide conflicting information on the nomenclature.

The Graal Compiler is a general high-performance just-in-time compiler for Java bytecode that is itself written in Java. It is state-of-the-art in optimization algorithms - according to [official documentation](https://www.graalvm.org/22.1/reference-manual/java/compiler/#compiler-advantages), "the compiler in GraalVM Enterprise includes 62 optimization phases, of which 27 are patented".

Truffle is a framework for implementing languages that will be compiled by Graal. From the outside, it behaves like a compiler: its job is to take guest language code and convert it to the VM's language, preserving as much intrinsic metadata as possible. Unlike a hand-crafted compiler, Truffle takes an interpreter of the guest language as its input and uses [Partial evaluation](#partial-evaluation) to do the compilation, performing a so-called "first Futamura projection".

![alt](truffle.drawio.svg)

Truffle also provides several primitives that the language implementation can use to guide the partial evaluation process, allowing for better results.

We want to mention that GraalVM is distributed in two editions, Community and Enterprise. Supposedly, the Enterprise edition provides even higher performance than the Community one. As we want to avoid all potential licensing issues, we only used the Community edition and can't comment on Enterprise performance at all.

## Previous work

Truffle was originally described as "a novel approach to implementing AST interpreters" in [Self-Optimizing AST Interpreters (2012)](https://dl.acm.org/doi/10.1145/2384577.2384587) and wasn't directly applicable to our bytecode interpreter problem.

[Bringing Low-Level Languages to the JVM: Efficient Execution of LLVM IR on Truffle (2016)](https://dl.acm.org/doi/10.1145/2998415.2998416) implemented Sulong, an LLVM IR (bytecode) runtime, and showed "how a hybrid bytecode/AST interpreter can be implemented in Truffle". This is already very similar to our current work, however, it implemented an unique approach of converting unstructured control flow into AST nodes.

In [Truffle version 0.15 (2016)](https://github.com/oracle/graal/blob/master/truffle/CHANGELOG.md#version-015), the `ExplodeLoop.LoopExplosionKind` enumeration was implemented, providing the [`MERGE_EXPLODE` strategy](#mergeexplode-strategy).

In [GraalVM version 21.0 (2021)](https://www.graalvm.org/release-notes/21_0/), an "experimental Java Virtual Machine implementation based on a Truffle interpreter" was introduced. This project is very similar to our work, using the same approaches for implementing a different language.

While [Truffle CIL Interpreter (2020)](https://epub.jku.at/obvulihs/content/titleinfo/5473678) also implemented the CIL runtime, it chose a completely different approach, building an AST from the text representation of IL code. Also, as it admits in the conclusion, it "didn't focus on performance optimization of the different instructions". The same implementation approach was chosen by [truffleclr](https://github.com/alex4o/truffleclr).

# Theory

## Partial Evaluation

The most important technique allowing Truffle/Graal to reach high performance is Partial Evaluation. It is theoretically known for decades, one of the foundations being [Partial computation of programs (1983)](https://repository.kulib.kyoto-u.ac.jp/dspace/bitstream/2433/103401/1/0482-14.pdf), but only advances in computer performance make it practically usable.

The high-level view of partial evaluation offered by Futamura is "specializing a general program based upon its operating environment into a more efficient program".

Consider a program (or its chunk) as a mapping of inputs into outputs. We can divide those inputs into two sets - dynamic inputs and static inputs - denoting the program as _prog: I<sub>static</sub> × I<sub>dynamic</sub> → O_.

The process of partial evaluation is then transforming _&lt;prog, I<sub>static</sub>&gt;_ into _prog*: I<sub>dynamic</sub> → O_ by incorporating the static input into the code itself. We'll call _prog*_ a specialization of _prog_ for _I<sub>static</sub>_, sometimes it is also referred to as a residual program, intermediate program, or a projection of _prog_ at _I<sub>static</sub>_.

For a simple example, let's consider _f(s,d) = s*(s*(s+1)+d)_. The specialization of _f_ for _s=2_ is then _f<sub>2</sub>(d)=2*(6+d)_, effectively pre-computing one multiplication. An even more interesting specialization is _f<sub>0</sub>(d)=0_, turning the entire program into a constant expression.

The separation between _I<sub>static</sub>_ and _I<sub>dynamic</sub>_ is not rigorous - it is valid both to create a separate specialization for every single input combination or to consider all input dynamic and therefore specialize for an empty set. However, these extremes don't provide any performance benefits. Partial evaluation is therefore usually guided by heuristics that analyze when a specific input value is used _often enough_ to warrant a specialization.

In his work, Futamura formulates so-called Futamura projections. Let's define a generic specializer as _specializer: prog × I<sub>static</sub> → prog*_.

The first Futamura projection is as follows: Let's define an interpreter as _interpreter: source × inputs → outputs_, a program taking two inputs: the source code and the "inner" inputs for the code. Then the result of _specializer(interpreter, source) = executable_ is a fully realized program for the specific source code as if the source code was "compiled" in the traditional sense of the word.

The second Futamura projection observes that _specializer(specializer,interpreter) = compiler_ - we generate a tailored specializer that can transform source code into executables.

The third Futamura projection observes that _specializer(specializer,specializer) = compiler-compiler_, resulting in a tool that takes an _interpreter_ and returns a _compiler_.

In this work, we implement an interpreter and use Truffle to perform the first Futamura projection.

### Tiered compilation

Because more aggressive compilation optimizations result in the compilation taking more time, it is a common practice to use tiered compilation. As a specific code gets called more often, it becomes worth it to recompile it again and better optimize it.

In Truffle/Graal, there is always a fallback of interpreting the code with no partial evaluation and compilation. This fallback is used both before the first compilation happens and when a de-optimization happens (see below).

One reason behind always starting in interpreter before compiling is that the interpreted invocations can already provide observations about the code, for example branch probability, if such observations are implemented. These observations can be used so that the first compilations are already of high quality.

For our project, the difference between compiled tiers is not too interesting, as they usually have a relatively small performance difference between them. The biggest gap occurs between the interpreted code and the first compiled tier, where the execution time can differ by more than an order of magnitude.

### Guards and de-optimizations

For practical partial evaluation, it is valuable to perform speculative optimizations - compiling the code expecting invariants that can be broken during runtime. One common example of such speculation is optimizations of virtual calls: assuming that the method will always be called on objects of a specific type allows replacing the virtual call with a static one and enables a more aggressive specialization.

Also, it is often useful to exclude some exceptional code paths from the compilation - for example, if dividing by zero should cause an immediate crash of the application with a message being printed out, there is no use in spending time compiling and optimizing the error-message printing code, as it will be called no more than once.

To achieve that, Graal uses guards - statements that, when reached by the runtime, result in de-optimization. De-optimization is a process of transferring evaluation from the compiled variant of the method back to the interpreter at the precise point where it was interrupted and throwing away the already compiled variant, as its assumptions no longer hold.

As an example, here's a pseudo-code of what a single-cache virtual call implementation could look like.

```
bool cached = false;
bool invariant = true;
type expectedType = null;
funcptr cache = null;

exec(obj, method)
{
    if(!cached)
    {
        cached = true;
        expectedType = obj.Type;
        cache = obj.Type.ResolveVirtualFunc(method)
    }

    if (invariant)
    {
        if(obj.type==expectedType)
        {
            return cache.call()
        } else {
            Deoptimize()
            invariant = false
        }
    } 
    obj.Type.ResolveVirtualFunc(method).call()
}
```

When partially evaluated with `invariant == true`, the resulting code will look like this:

![alt](guard_first.drawio.svg)

As long as the virtual call is effectively static at runtime, we only spend time compiling the actual target function (which can be specialized for the environment) and during invocation only pay the price of a simple equality check.

Once the comparison fails, this version of the compiled method is thrown away, and a generic one is created:

![alt](guard_second.drawio.svg)

### Escape analysis and virtualization

All Java objects traditionally have to be allocated on the heap, as the VM has no concept of stack-allocated structures. However, allocating data on the heap is slow. The solution to this issue is escape analysis: if an object never leaves the current compilation unit, it can be virtualized. A virtualized object is never actually allocated but is decomposed to its individual fields, which are then subject to partial evaluation and other optimization methods.

### `MERGE_EXPLODE` strategy

One of the key elements that allows for implementing partial evaluation friendly bytecode interpreters is the `MERGE_EXPLODE` loop explosion strategy (emphasis added):

> like `ExplodeLoop.LoopExplosionKind.FULL_EXPLODE`, but copies of the loop body that have the exact same state (all local variables have the same value) are merged. This reduces the number of copies necessary, but can introduce loops again. **This kind is useful for bytecode interpreter loops.**

To fully appreciate the importance of this strategy, we have to point out the following fact of CLI's design from _I.12.3.2.1 The evaluation stack_(emphasis added):

> The type state of the stack (**the stack depth** and types of each element on the stack) at any given point in a program **shall be identical for all possible control flow paths**. For example, a program that loops an unknown number of times and pushes a new element on the stack at each iteration would be prohibited.

This design choice is not a coincidence, as it is vital also for hand-crafting performant JIT compilers. Regarding `MERGE_EXPLODE`, it means that all copies of the interpreter's inner loop that have the same bytecode offset will also have the same evaluation stack depth and type layout.

Thanks to this if we have, for example, a push immediate 4 instruction somewhere in the code, it can be translated to a simple statement like `stack[7] = 4`, as in every execution of this instruction the stack depth has to be the same. This enables more optimizations, as this constant can be propagated to the next instruction reading `stack[7]`.

To explain the inner working on a more involved example, let's manually apply this strategy to the following pseudo bytecode of `for(int i = 0; i < 100; i++) {a = a*a; }; return a;`:

```
; i=0
0: OPCODE_LOADCONST 0
1: OPCODE_STOREVAR i

; i < 100
2: OPCODE_LOADVAR i
3: OPCODE_LOADCONST 100
4: OPCODE_JMPIFBEQ @14

; a = a*a
5: OPCODE_LOADVAR a
6: OPCODE_LOADVAR a
7: OPCODE_MULTIPLY
8: OPCODE_STOREVAR a

; i++
9: OPCODE_LOADVAR i
10: OPCODE_LOADCONST 1
11: OPCODE_ADD
12: OPCODE_STOREVAR i

; loop
13: OPCODE_JMP @2

; return a
14: OPCODE_LOADVAR a
15: OPCODE_RET
```

Our interpreter might look something like this:

```
pc = 0 //bytecode offset
top = 0 //stack top

while(True)
    opcode = getOpcode(pc)
    switch opcode:
        case OPCODE_LOADCONST:
            stack[top++] = getImmediate(pc+1)
            break
        case OPCODE_STOREVAR:
            vars[getVar(pc+1)] = stack[top--]
            break
        case OPCODE_LOADVAR:
            stack[top++] = vars[getVar(pc+1)]
            break
        case OPCODE_JMPIFBEQ:
            top -= 2
            if(stack[top+1]>=stack[top+2]):
                pc = getImmediate(pc+1)
                continue
            break
        case OPCODE_MULTIPLY:
            top -= 1
            stack[top] = stack[top]*stack[top+1]
            break
        case OPCODE_ADD:
            top -= 1
            stack[top] = stack[top]+stack[top+1]
            break
        case OPCODE_JMP:
            pc = getImmediate(pc+1)
            continue
        case OPCODE_RET:
            return stack[top]

    pc += lenghtOf(opcode)
```

Thanks to the strategy, only one state per bytecode offset has to be created. Knowing exactly the stack depth, we can partially evaluate the stack positions to constants:

```
; i=0
0: stack[0] = 0
1: vars[i] = stack[0]

; i < 100
2: stack[0] = vars[i]
3: stack[1] = 100
4: if(stack[0]>=stack[1]) goto @14

; a = a*a
5: stack[0] = vars[a]
6: stack[1] = vars[a]
7: stack[0] = stack[0] * stack[1]
8: vars[a] = stack[0]

; i++
9: stack[0] = vars[i]
10: stack[1] = 1
11: stack[0] = stack[0] + stack[1]
12: vars[i] = stack[0]

; loop
13: goto @2

; return a
14: stack[0] = vars[a]
15: return stack[0]
```

As the stack does not leave this method, it will be completely virtualized. Since the stack array is always accessed using constant indices, we can apply aggressive optimization and optimize out the array:

```
  vars[i] = 0;

condition:
  if(vars[i]>=100) goto end
  vars[a] = vars[a]*vars[a]
  vars[i] = vars[i]+1
  goto condition

end:
  return vars[a]
```

Even though we started with a big interpreter loop, by merging the instances having the same bytecode offset, the interpreter loop disappears and the original control flow of the method reappears from the flat bytecode.

# CLI Component parser

Before being able to execute any code, it is necessary to read the code from the assemblies. Prior to starting the work, we expected some open source parsers for this format to exist for various languages, including Java. However, the only alternative stand-alone parser (not a component of a full CLI implementation) we found was [dnlib](https://github.com/0xd4d/dnlib) targeting .NET framework itself. If even for such a popular runtime there are no suitable parsers implemented in Java, we feel that the parser implementation step is an important part to consider in the whole "Building an experimental runtime" picture.

## Analysis

### Design goals

Before we design and implement the parser, we consider what additional constraints have to be put on a parser in order for it to be partial-evaluation friendly. For partial-evaluation friendliness, the key metric is how trivial can every piece of code get after partial evaluation. This metric is most important for a sequence of instructions executed frequently, often referred to as a "hot path". While our goal was for the parser to never be called on a hot path, for some scenarios, including reflection, it would be necessary.

There are two possible extremes for parser design: "fully lazy" where every query for the file causes it to be parsed from the start, and "fully preloaded" where all the data from the file is immediately fully parsed into hierarchies of objects and structures. Practical parsers usually choose a compromise between those two approaches, mainly because the extremes lead to extremely slow runtime or boot-up, respectively.

Driven by the goal of partial-evaluation friendliness, we design the initial parsing such that:

* trivial queries, e.g. queries for a metadata item at a constant index, will only result in a compilation constant,
* simple queries, e.g. queries for a metadata item at a variable index, will get compiled to a simple offset calculation (multiply and add) and a read from a (compilation constant) byte array,
* all further parsing necessary for more complex queries (creating objects representing metadata concepts etc.) will be lazy and invokers should cache the results themselves.

### Definition of important CLI component structures

Most of the metadata are stored in streams, with headers outside of the streams describing their locations. There are two basic types of streams: heaps and tables.

Heaps contain a sequence of bytes, the meaning of which changes based on the specific heap. The specification defines 4 heaps:

* `#Strings` containing values of identifier strings.
* `#US` containing "user strings" - values of strings used by the program code itself during runtime.
* `#Blob` containing variable-length metadata as binary blobs.
* `#GUID` containing GUIDs.

The tables are stored in a stream called `#~`. This is the root of all metadata information. The specification describes 38 tables. Cell values can be a constant or an index. Indices can point to heaps (the value is a byte offset), another table (the value is a row number), or one of multiple tables (the value is a "coded index" specifying both the table and row number).

For an example of references between these structures, this is what a single row looks like in the metadata table `TypeDef`, which contains definitions of types:

| Column        | Raw value | Comment                                                                                                                                                                             |
|---------------|-----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Flags         | 0x100000  | Constant bitmask specifying TypeAttributes.                                                                                                                                         |
| TypeName      | 0x01A9    | An offset into the `#String` heap, where the name of the type can be found. In this example, `Program` was written there.                                                           |
| TypeNamespace | 0x01DE    | An offset into the `#String` heap, where the namespace of the type can be found. In this example, `SampleProject` was written there.                                                |
| Extends       | 0x0031    | A coded index into `TypeDef`, `TypeRef`, or `TypeSpec`. In this example an index to `TypeRef` table row 12, which is a reference to `System.Object`.                              |
| FieldList     | 0x0002    | An index into the `Field` table where the fields for this type start. As in this case the type has no fields, the index points past the end of the `Field` table, which has only 1 row. |
| MethodList    | 0x0002    | An index into the `Method` table where the methods for this type start. As this type has multiple methods, row 2 of `Method` contains information about `Main`, other methods follow.   |


### Complexities of the CLI component format

Subjectively, we feel the format used by CLI components is not designed well with regard to supporting different parsing approaches and platforms. To substantiate this claim, we want to highlight several factors that complicate parsing the components and had to be considered in the design.

#### PE Wrapping

As stated in ECMA-335 _II.25 File format extensions to PE_:
> The file format for CLI components is a strict extension of the current Portable Executable (PE) File Format. [...] The PE format frequently uses the term RVA (Relative Virtual Address). An RVA is the address of an item _once loaded into memory_ [...].
> 
> The RVA of an item will almost always differ from its position within the file on disk. To compute the file position of an item with RVA r, search all the sections in the PE file to find the section with RVA _s_, length _l_ and file position _p_ in which the RVA lies, ie _s ≤ r < s+l_. The file position of the item is then given by _p+(r-s)_.

On Windows and other theoretical platforms where PE parsing is a service provided by the operating system, this allows for the component to be loaded into virtual memory as any other executable file. RVA addresses can then be resolved transparently by the CPU's and operating system's virtual memory mappings. For all other platforms, this adds one more level of indirection that needs to be handled.

As our parser is platform-agnostic and written in Java, we can't use any of those services. Therefore, we need to manually perform the sections search and RVA calculations as described in the standard.

#### Metadata tables format

The biggest complexity we encountered during parser design was the format of metadata tables. These tables contain most of the metadata information of the CLI component.

The data of the tables is stored in the `#~` stream. This stream consists of a header followed by a simple concatenation of values of all rows of all tables, with no additional metadata in between.

The header itself contains only the following fields relevant for locating data in the tables (shortened, for full structure see _II.24.2.6 #~ stream_):

| Offset | Size | Field     | Description                                                                              |
|--------|------|-----------|------------------------------------------------------------------------------------------|
| 6      | 1    | HeapSizes | Bit vector of heap sizes                                                                 |
| 8      | 8    | Valid     | Bit vectors of present tables                                                            |
| 24     | 4*n  | Rows      | Array of n 4-byte unsigned integers indicating the number of rows for each present table |

The first issue is that no information about table length in bytes is present. This results in **every single parser implementing the format having to implement the format for every single metadata table**, as skipping a table requires knowing the byte length of its rows. This completely prohibits an iterative development cycle that adds support for only the necessary tables. For example, to implement a utility that only outputs names of all the types available in the component, while only data from the TypeDef table is necessary, all 38 tables defined in ECMA-335 must be implemented. The BACIL implementation described here only accesses 11 of these tables.

The second complication comes in _II.22 Metadata logical format: tables_ and _II.24.2.6 #~ stream_:

> Each entry in each column of each table is either a constant or an index.
> 
> [...]
> 
> Each index is either 2 or 4 bytes wide. The index points into the same or another table, or into one of the four heaps. The size of each index column in a table is only made 4 bytes if it needs to be for that particular module. So, if a particular column indexes a table, or tables, whose highest row number fits in a 2-byte value, the indexer column need only be 2 bytes wide. Conversely, for tables containing 64K or more rows, an indexer of that table will be 4 bytes wide. 
> 
> [...]
> 
> If _e_ is a _coded index_ that points into table _t<sub>i</sub>_ out of _n_ possible tables _t<sub>0</sub>, … t<sub>n-1</sub>_, then it is stored as e << (log n) | tag{ t0, …tn-1}[ t<sub>i</sub>] using 2 bytes if the maximum number of rows of tables  _t<sub>0</sub>, … t<sub>n-1</sub>_, is less than 2<sup>(16 – (log n))</sup>, and using 4 bytes otherwise. 

While this decision saves storage size, it means that table row length isn’t a constant and depends on the row count of other tables. For example, a `TypeDef` table row can be from 14 up to 24 bytes in size.

This means that the parser can't workaround the first issue by expecting the table row length be constant.

If we were to improve the format to remove these issues, we would add information about the row length of present tables into the header. Even if each size was stored as a full byte (which all tables defined in the standard fit into), in the worst case this would increase each binary’s size by 38 bytes and allow for skipping tables without dealing with their internal row format.

#### Extensive normalisation

File format design is often a compromise between several engineering goals[^6]. One of the design concepts that apply is normalisation, a concept that each information should be stored only once, removing all redundancy. While such a goal can be beneficiary for other uses of the file format (like writing and modifying), from the point of view of a lightly preloading consumer, it results in non-ideal structures.

* In parent-child relationships, only one node has a direct reference to the other one. Traversing the edge backwards involves enumerating all the nodes and searching for one with the appropriate reference. If such queries are performance sensitive, the invoker has to cache the answers.

* When referencing a sequence of items in a table, only information about the beginning of the sequence is directly stored. The end of the sequence is either the last row of the table or the start of the next sequence, as specified by the next row, whichever comes first.

    While the complexity this adds usually amounts to a single "if" statement, it crosses the border between cell value semantics and metadata logical format internals - either the parser has to understand the semantics of cells as "sequence indices" to encapsulate resolving the sequence length, or the invoker has to understand the file format's internal detail of row numbers.

## Parser implementation details

### Metadata tables parser

As mentioned in [Metadata tables format](#metadata-tables-format), parsing any metadata tables requires implementing the internal row format for all tables specified in ECMA-335. Implementing all 38 tables manually would require a sizeable amount of work and make modifications to the parser complicated. Therefore, this problem is a nice match for code generation.

We created a simplified text-file containing information about all the columns in all tables that is also human readable. For example, the `TypeDef` table (used as an example in [Definition of important CLI component structures](#definition-of-important-cli-component-structures)), was specified like this:

```
TypeDef:02
-Flags:c4
-TypeName:hString
-TypeNamespace:hString
-Extends:iTypeDef|TypeRef|TypeSpec
-FieldList:iField
-MethodList:iMethodDef
```

For simplicity, we write the code generator in plain Java, outputting Java source files. The result is a `CLITableClassesGenerator` class.

### CLITableRow and CLITablePtr

We want the implementation of accessing metadata table rows to be as safe and simple-to-use as possible while keeping in mind the design goals for partial evaluation. The two operations we expect to be most common are enumerating a single table and resolving indices that reference other tables.

To support enumeration we make `CLITableRow` implement `Iterable`, allowing for a safe for-each access, completely hiding the internal table details. An example of printing all methods defined in a component:

```Java
CLIComponent component = ...;
for (CLIMethodDefTableRow methodDefTableRow : component.getTableHeads().getMethodDefTableHead()) {
    System.out.println(methodDefTableRow.getName().read(component.getStringHeap()));
}
```

For resolving indices, we made the tables return a `CLITablePtr` wrapped index. Such a pointer can then be directly provided to `CLITableRow`'s `skip` method, which validates that the table ID is correct. The importance of this wrapping is increased by the following fact mentioned in _II.22 Metadata logical format: tables_:

> Indexes to tables begin at 1, so index 1 means the first row in any given metadata table. (An index value of zero denotes that it does not index a row at all; that is, it behaves like a null reference.)

Exposing the indices as raw integers would allow for off-by-one bugs to become prevalent. Providing a wrapped variant that behaves as expected by default helps combat these issues.

The pattern for safe index resolving looks like this:

```Java
CLIComponent component = ...;
CLITypeDefTableRow typeDef = ...;

CLIFieldTypeRow firstField =  component.getTableHeads().getFieldTableHead().skip(typeDef.getFieldList());
```

### Sequence references

As mentioned in [Extensive normalisation](#extensive-normalisation), sequences of items in a table are stored in a way that requires either implementing column semantics in the parser or the invoker knowing logical table internals. As we generate our table parsers from a definition file, including sequence semantics would require expanding both the generator and the definition file. Instead, we leave the responsibility on the invoker, resulting in code like this (example from the `CLIType` class):

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

## Conclusion

In the end, design and implementation of the parser took a non-trivial portion of the development time. Even though straightforward code-size indicators are controversial, they demonstrate the substantiality of the parser, being about 30% of the whole project. Excluding generated code it comprises 119594 bytes over 3609 lines of code, while the rest of the language package comprises 266010 bytes over 7534 lines.

# Runtime

## Analysis

This chapter focuses on the overall design of our interpreter and the approaches required to achieve acceptable performance.

### Nodes

#### Bytecode nodes

The smallest compilation unit of Truffle is a Node. This nomenclature comes from Truffle's original AST-based design, where nodes represented actual nodes in the syntax tree. The supported pattern was that the nodes were small, typically representing a single operation - for example, an `AddNode` that has two child nodes and adds them together.

However, this design is not applicable to our bytecode interpreter. Inside one method, the bytecode doesn't have any tree structure we could replicate with the nodes. The most straightforward solution is to have one node per one bytecode chunk, which in the CLI subset we implement corresponds to one method. We call such a node the `BytecodeNode`. 

Having big nodes with several instructions has its tradeoffs, mainly the fact that without additional work (described below) once a node starts executing in a specific performance tier (see [Tiered compilation](#tiered-compilation)), it has to finish running in that specific tier. Truffle always starts executing code immediately in interpreted mode and only later considers compiling it. This can lead to poor results. For example, let's consider the following code running in a one-node-per-method implementation:

```C#
static int Main()
{
    int result = 0;
    for (int i = 0; i < 20000; i++)
    {
        for (int j = 0; j < 20000; j++)
        {
            result += i * j;
        }
    }
    return result;
}
```

The runtime will immediately enter the `Main` node and start executing it in interpreter mode. As the entire execution time is spent in this one node, the runtime will never get a chance to run a single compiled statement. This significantly affects the performance (see [Warmup concerns](#warmup-concerns) for an example of the slowdown in interpreted mode).

In [Bringing Low-Level Languages to the JVM: Efficient Execution of LLVM IR on Truffle (2016)](https://dl.acm.org/doi/10.1145/2998415.2998416), the authors described a method of separating the bytecode chunk into "basic blocks", each containing only instructions that don't affect the control flow. A "block dispatcher" controls the flow between these basic blocks, selecting the adequate basic block to continue the execution with.

![alt](basicblocks_0.svg)

_A small C program containing a loop. (citation)_

![alt](basicblocks_1.svg)

_LLVM IR of the C program. (citation)_

![alt](basicblocks_2.svg)

_Basic block dispatch node for the LLVM IR. (citation)_

While this approach eases the issue and would divide our nested loops into smaller compilation units, it was superseded by a more generic strategy directly implemented in Truffle.

This strategy, called On-Stack Replacement (OSR), was made specifically to target the described issue:

> During execution, Truffle will schedule "hot" call targets for compilation. Once a target is compiled, later invocations of the target can execute the compiled version. However, an ongoing execution of a call target will not benefit from this compilation, since it cannot transfer execution to the compiled code. This means that a long-running target can get "stuck" in the interpreter, harming warmup performance.
>
> On-stack replacement (OSR) is a technique used in Truffle to "break out" of the interpreter, transferring execution from interpreted to compiled code. Truffle supports OSR for both AST interpreters (i.e., ASTs with LoopNodes) and bytecode interpreters (i.e., nodes with dispatch loops). In either case, Truffle uses heuristics to detect when a long-running loop is being interpreted and can perform OSR to speed up execution.

During our design phase, the OSR was still being worked on, being only introduced in Graal 21.3 released in October 2021. For that reason, our design isn't compatible with it - to support it, the whole execution state has to be stored in Truffle's frames, while we only use frames to pass/receive arguments and store the rest of the state in plain variables. However, moving this state into the frame should be the only major step necessary to support OSR. Because of the significance of OSR, if we were designing the runtime again, we'd definitely focus on supporting it.

#### Instruction nodes

Some instructions require values that can be pre-calculated. A typical example in CIL are instructions that have a token as its argument - a token is a pointer into metadata tables and requires calling into the parser to resolve. We want to perform this resolution only once and cache it for future executions.

For that, we'll use a process of nodeization (called "quickening" by Espresso, the Java bytecode interpreter for GraalVM) - we create a node representing the instruction with the data already pre-computed and patch the bytecode, replacing the original instruction with a BACIL-specific `TRUFFLE_NODE` opcode. When the interpreter hits this instruction, it calls the respective child node.

### Dynamicity of references

One of the additional things to consider when implementing a partial-evaluation friendly interpreter is dynamicity of references, whereby dynamicity we mean how often the reference changes its state. This metric is important because, effectively, the dynamicity of a chain of references will be equal to the most dynamic of the references. As a result, what would traditionally be considered bad design patterns is sometimes necessary to divide the chain into more direct references, in order to make each object reachable with the lowest dynamicity possible.

The following reference graph shows the refactoring in a generic case:

![alt](dynamicity_generic.drawio.svg)

_Scenario 1: Reference chain results in class B being accessible with high dynamicity and therefore not being effectively partially evaluated._

![alt](dynamicity_generic_good.drawio.svg)

_Scenario 2: Class B is accessible with a low dynamicity reference, resulting in more effective partial evaluation._

For a case study from the BACIL implementation, let's consider the design decisions behind `LocationDescriptor` and `LocationHolder`. Each location has a type and a value. While the value itself (and the type of the value) changes based on the running code, the type of the location never changes. This is a perfect example of two pieces of information with different dynamicity.

Even from regular development patterns, it makes sense to divide location values and location types into separate classes - store the location type information in the metadata as a "prototype" for later creating the value storage based on it. In BACIL, `LocationDescriptor` contains the type information and `LocationHolder` contains the actual values.

It is always necessary to know the location type to work with the values, mainly to differentiate between ValueTypes and references. The rule of encapsulation would dictate that the consumer doesn't need to know that there's a `LocationDescriptor` tied to the `LocationHolder`, as it's an internal detail. Such an implementation would look something like this:

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

However, using this code results in a non-optimal dynamicity chain and ineffective partial evaluation:

![alt](dynamicity_case.drawio.svg)

_Scenario 1: As a `LocationHolder` is unique per object instance/method invocation/ etc., the reference to it is highly dynamic. The `LocationDescriptor` is only unique per object type/method definiton, but can only be reached through a dynamic chain._

In order to make this more effective, we have to hold a separate reference to a `LocationDescriptor`. As every location-accessing instruction (in the implemented subset of CLI) will always use the same `LocationDescriptor`, this results in effective partial evaluation. The new implementation looks like this:

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

### Standard libraries

Our goal was to implement as little of the standard library as possible. When starting with the implementation, we hoped parts implemented in CIL and native methods would be well decoupled, so that we could reuse all CIL parts. For the native parts, we would call .NET's native implementation if possible and only implement them in BACIL if not. Unfortunately, the coupling is tight, as the [documentation](https://github.com/dotnet/runtime/blob/main/docs/design/coreclr/botr/corelib.md) admits:

> CoreLib has several unique properties, many of which are due to its tight coupling to the CLR.

The biggest offender are strings. To achieve high performance, .NET's runtime expects the native view (`StringObject`) and managed view (`System.String`) of strings to be identical, so the coupling is extremely tight. To support string operations in BACIL, we'd either have to reimplement the operations or implement the strict marshalling that's expected by .NET's native code.

Truffle's official mechanism of calling into native code is called the Native Function Interface (NFI). Unfortunately, at the time of designing BACIL, it was missing key features, for example support of custom ABIs (calling conventions).

In the end, our experiments with calling native .NET runtime code using NFI showed it would significantly complicate the whole codebase with uncertain results, and we decided against it. Unfortunately, we'll have to re-implement all necessary native code ourselves in BACIL.

### BACILHelpers

To provide additional BACIL APIs, we need to expose our own "native" BACIL functionality to C# code. For that, we create an assembly called BACILHelpers. This assembly has two implementations: a proper .NET variant (written in C#) for running on .NET, and a "virtual" assembly leading to BACIL’s internal methods that BACIL silently injects.

Here’s an example of how we can implement a `BACILConsole.Write` method in the C# variant:

```C#
public class BACILConsole
{
    public static void Write(Object value)
    {
        Console.Write(value);
    }
}
```

And the same method in BACIL (shortened and simplified):

```Java
public class BACILHelpersComponent extends BACILComponent {
     public Type findLocalType(String namespace, String name) {
        if(name.equals("BACILConsole"))
            return new BACILConsoleType();
    }
}

public class BACILConsoleType extends Type {
     public BACILMethod getMemberMethod(String name, MethodDefSig signature) {
        if(name.equals("Write"))
            return new BACILConsoleWriteMethod();
     }
}

public class BACILConsoleWriteMethod extends JavaMethod {
    public Object execute(VirtualFrame frame) {
        CompilerDirectives.transferToInterpreter();
        System.out.print(frame.getArguments()[0]);
        return null;
    }
}
```

### Values and locations

As specified in ECMA-335, values can have the following "homes":

> **I.12.1.6.1 Homes for values**
> The **home** of a data value is where it is stored for possible reuse. The CLI directly supports the following home locations:
> * An incoming **argument**
> * A **local variable** of a method
> * An instance **field** of an object or value type
> * A **static** field of a class, interface, or module
> * An **array element**
> 
> [...]
> 
> In addition to homes, built-in values can exist in two additional ways (i.e., without homes):
> 1. as constant values (typically embedded in the CIL instruction stream using ldc.* instructions)
> 2. as an intermediate value on the evaluation stack, when returned by a method or CIL instruction.

As Truffle requires Java objects to be passed on the node boundary, BACIL also has an additional state where the value is a Java object.

In .NET, all locations are typed (ECMA-335 I.8.6.1.2 Location signatures). While evaluation stack slots are also typed, they use a different and more coarse type system.

To avoid boxing and unboxing numbers (integers and floating-point numbers), we cannot just store all values in an `Object[]`. Therefore, it is necessary to always have separate storages for primitives, best implemented by a `long[]`.

Locations usually exist in multiples (local variables, arguments, fields, etc.) and are always statically typed - one location will always have one type through its lifetime and only ever contain values type-compatible with its type. We divide the location into two parts: a descriptor and a holder.

The holder is actually extremely simple: it only has an `Object[] refs` and a `long[] primitives` that are big enough to hold all the values required by the descriptor. The holder knows nothing of the types or identities of values inside. This represents one instance of a value storage.

The descriptor represents the "shape" of the locations, knowing the type of each location and its position in the holder.

One feature of ECMA-335 is so-called user-defined ValueTypes, structures that have the semantics of a primitive. The idea is that two integers (x,y) and a Point structure (with x,y fields) will look exactly the same on the stack, instead of the latter turning into an object reference. Our implementation will follow that example, as we will "flatten" the structure, reserving space in the ValueType’s parent for each of its fields.

The evaluation stack is a bit more complicated: while at each point in time the type of the evaluation stack field is known, it changes throughout execution. Each stack slot therefore has to exist as both a reference slot and a primitive slot and we'll have to keep track of which one to use. To achieve that, we by default expect the value to be in the refs slot. In case it is not, we repurpose the refs slot to hold a "marker" object describing the stack-type of the value in the primitive slot. An object will be stored in `(ref, primitive)` as `(obj, undefined)`, a native int 42 will be stored as a `(EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT, (long)42)`.

#### State transitions

Keeping in mind the [Dynamicity of references](#dynamicity-of-references) and the fact that all locations are typed (and the type of a location never changes), it follows to make the `Type` objects responsible for implementing the state transitions. There are 6 possible transitions between objects, evaluation stack and locations implemented as the following methods of `Type`:

```Java
public void stackToLocation(LocationsHolder holder, int primitiveOffset, int refOffset, Object ref, long primitive)
public void locationToStack(LocationsHolder holder, int primitiveOffset, int refOffset, Object[] refs, long[] primitives, int slot)
public Object stackToObject(Object ref, long primitive)
public void objectToStack(Object[] refs, long[] primitives, int slot, Object value)
public Object locationToObject(LocationsHolder holder, int primitiveOffset, int refOffset)
public void objectToLocation(LocationsHolder holder, int primitiveOffset, int refOffset, Object value)
```

The `Type` class will provide the default transitions for reference types, while subclasses of this class can provide special variants for primitives.

A `LocationHolder` can then resolve the (compilation constant) location type and use it to transition the value, for example like this:

```Java
public void locationToStack(LocationsHolder holder, int locationIndex, Object[] refs, long[] primitives, int slot)
{
    locationTypes[locationIndex].locationToStack(holder, offsets[locationIndex], refs, primitives, slot);
}
```

One factor to keep in mind is that to follow the standard, the state transitions are coupled with widening or narrowing operations. For example, according to ECMA-335 _I.12.1 Supported data types_ "Short numeric values (int8, int16, unsigned int8, and unsigned int16) are widened when loaded and narrowed when stored". We also need to perform our own housekeeping because we store all primitives in a flat `long[]`. Each class representing a primitive implements its own widening and narrowing as necessary.

### CompilationFinal annotation

As explained in [Partial Evaluation](#partial-evaluation), one of the key decisions is separating inputs into two sets - dynamic inputs and static inputs. Java's `final` keyword is therefore integral for achieving performance, as it guarantees that the variable will be considered a static input.

There is an issue that, for arrays, marking them as `final` only means that the reference to the array doesn't change, while the contents of the array can change freely. The solution is the `CompilerDirectives.CompilationFinal` annotation, which can mark arrays such that the compiler considers reads with a constant index as constants. Unlike the built-in `final` keyword, the compiler cannot actually enforce that no writes happen to the array. It is the responsibility of the implementation to always invalidate the current compilation when modifying a `CompilationFinal` array.

## Debugging performance issues

To achieve high performance, the ability to debug performance issues is necessary. Unfortunately, traditional methods (like sampling) don't provide the required insight for outputs of Graal compilation - during partial evaluation, the code gets transformed too much for these methods to work properly. A single instruction can result from a partial evaluation of several methods and, as such, cannot be attributed properly to one.

Internally, the Graal compiler represents the code during compilation in graphs. The various optimization phases are then transformations on these graphs. Graal allows to dump the current graph in various stages of the compilation pipeline by using the `graal.Dump` VM argument. These graphs are key to understanding results of the partial evaluation, mainly which code was eliminated (by constant folding) and which remained. For our analysis, the "After TruffleTier" phase is the most important, as it reflects the graph state after partial evaluation.

The official tool for analyzing these graphs is the [Ideal Graph Visualizer](https://www.graalvm.org/22.1/tools/igv/). However, getting it requires "accepting the Oracle Technology Network Developer License", which contains strict limitations for allowed use. We don't consider the tool suitable for general use, as using it during development may limit the future uses of the project.

Fortunately, an MIT licensed open source project [Seafoam](https://github.com/Shopify/seafoam) provides all the necessary functionality. Compilation graphs in this work were all generated by Seafoam.

The most common issue we hit when analyzing those graphs was that a piece of code we expected to be eliminated by partial evaluation was still included in the compilation - we designed it to be optimized out, but from the view of the compiler it couldn't be. To debug these issues, we used the `CompilerAsserts.partialEvaluationConstant` method. It allows us to express our belief that something should be a partial evaluation constant to the compiler and get an error message with detailed information about the compiler's view of the expression when it's not.

### Case study

For a case study, let's look into optimizing a specific parser call. As specified in [Design goals](#design-goals), we designed the parser so that trivial queries, e.g. queries for a metadata item at a constant index, would only result in a compilation constant. Is that the case? Let's see the compilation graph (in commit 42e5cb2e6e34956aca75be0c4c71ac7eb0f4bea8) after TruffleTier for a function returning `method.getComponent().getTableHeads().getTypeDefTableHead().skip(1).getFlags()`:

![alt](parseraccess_bad.svg)

That definitely contains more than just a constant. Using `CompilerAsserts.partialEvaluationConstant` and checking the errors (enabled with `--engine.CompilationFailureAction=Print`) leads us to the following code where the first expression is a constant and the second isn't:

```Java
CLITypeDefTableRow row = method.getComponent().getTableHeads().getTypeDefTableHead();
CompilerAsserts.partialEvaluationConstant(row.getFlags());
CompilerAsserts.partialEvaluationConstant(row.skip(1).getFlags());
```

From this we can discern that our implementation of `skip` is at fault.
Our skip method contains just one statement, `return createNew(tables, cursor+count*getLength(), rowIndex+count);`. Let's first validate whether all the used arguments are constants:

```Java
public final T skip(int count)
{
    CompilerAsserts.partialEvaluationConstant(tables);
    CompilerAsserts.partialEvaluationConstant(cursor);
    CompilerAsserts.partialEvaluationConstant(getLength());
    return createNew(tables, cursor+count*getLength(), rowIndex+count);
}
```

We get the following error when checking the `getLength()` statement:

```
Partial evaluation did not reduce value to a constant, is a regular compiler node: 516|ValuePhi(459 515, i32) (513|Merge; 459|ValuePhi(401 458, i32); 515|+; )
```

Let's dig into `getLength()`, enhancing it with asserts to see if `isStringHeapBig()` or `areSmallEnough` are at fault:

```Java
public int getLength() {
    int offset = 14;
    CompilerAsserts.partialEvaluationConstant(tables.isStringHeapBig());
    CompilerAsserts.partialEvaluationConstant(areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC));
    if (tables.isStringHeapBig()) offset += 4;
    if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) offset += 2;
    if (!areSmallEnough(CLITableConstants.CLI_TABLE_FIELD)) offset += 2;
    if (!areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF)) offset += 2;
    return offset;
}
```

And we get an error on `areSmallEnough`. We continue digging into that method, eventually realising that inside `protected final boolean areSmallEnough(byte... tables)`, the `tables[0]` isn't a constant! Turns out that even though the calls to `areSmallEnough` look to just be providing constant integers, as the function is using varargs a **new array is allocated** for those constants that is then passed to the function. As we explain in [CompilationFinal annotation](#compilationfinal-annotation), array elements are not considered to be constant unless the array is properly annotated as `CompilationFinal`. Our inplace arrays have no way to be annotated. We modified `areSmallEnough` to take a `byte[]` instead of varargs (to make what's happening more obvious) and instead of using inplace arrays used constant fields annotated with `@CompilerDirectives.CompilationFinal(dimensions = 1)`.

After two additional small changes (adding a field to cache `tableData` with the `CompilationFinal` annotation and annotating `areSmallEnough` with `@ExplodeLoop`), the graph for the same expression (`method.getComponent().getTableHeads().getTypeDefTableHead().skip(1).getFlags()`) after TruffleTier looks like this:

![alt](parseraccess_good.svg)

This case study provides a great example of how choices that are functionally equivalent in regular Java can provide vastly different compilation results when partially evaluated. In the end, we mostly only had to add annotations to make a big difference.

We used this process of checking if graphs look as expected using Seafoam and then using `CompilerAsserts.partialEvaluationConstant` to express our desires about constants multiple times during development.

# Results

## Completeness

Due to time constraints, several areas of the standard were ignored. The development focused on being able to run simple calculation programs and being able to run benchmarks from [Hagmüller's work](#hagmüllers-work).

In total, ECMA-335 defines 219 opcodes, consisting of 6 prefixes and 213 instructions. Of those, our runtime contains code handling 151 instructions and no prefixes.

Notable missing features include:

* exceptions, overflow checking instructions
* interfaces
* generics
* casting and type checks
* general arrays - only SZArrays (single dimensional, zero-based array) are supported
* operations requiring 64-bit unsigned integers
* unmanaged pointers and localloc

To validate the proper implementation of instructions, we used [.NET's CodeGenBringUpTests](https://github.com/dotnet/runtime/tree/main/src/tests/JIT/CodeGenBringUpTests). According to [the documentation](https://github.com/dotnet/runtime/blob/main/docs/design/coreclr/jit/porting-ryujit.md), they are the recommended test suit to target when porting RyuJIT (.NET's JIT engine) to a new platform:

> **Initial bring-up**
>
> * [...]
> * Implement the bare minimum to get the compiler building and generating code for very simple operations, like addition.
> * Focus on the CodeGenBringUpTests (src\tests\JIT\CodeGenBringUpTests), starting with the simple ones.

They are perfect for testing edge cases of implemented instructions. One example of a bug uncovered in this test suite that would be very hard to find manually was a missing int32 truncation which was fixed [here](https://github.com/jagotu/BACIL/commit/056640ec276376434f5cb32ac70c3f9eb26c4881#diff-47ca212abb11bfd59685c4b47364f4a21a015136d4d2a8d6bbe014a7c873e2c9R1110).

After stubbing out `Write`, `WriteLine`, `ToString` and `Concat` (to get rid of the unsupported operations used by debug prints in case of failure), the BACIL implementation presented in this work passed 85%, e.g. 133 out of the 155 tests, included in the `v6.0.6` tag. All the failed tests were because of missing features and not bugs in implemented features:

| Test        | Reason for failure|
|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ArrayExc | Missing exception support |
| ArrayMD1 | Missing multi-dimensional array support |
| ArrayMD2 | Missing multi-dimensional array support |
| div2     | Missing exception support |
| DivConst | Missing exception support |
| FPConvI2F | Missing `conv.r.un` instruction implementation (due to unsigned 64-bit integers usage) |
| FPMath | Missing generics support (in `[System.Private.CoreLib]System.BitConverter::DoubleToInt64Bits` internally used by `[System.Private.CoreLib]System.Math::Round`) |
| LngConv | Missing generics support internally used by `[System.Private.CoreLib]System.IntPtr` |
| Localloc* (8 tests) | Missing support for localloc and raw pointers |
| ModConst  | Missing exception support |
| RecursiveTailCall | Missing generics support |
| Rotate | Missing `volatile.` prefix |
| StructReturn | The test (incorrectly) compares floats using `==` instead of checking their difference against an epsilon |
| UDivConst | Missing exception support |
| UModConst | Missing exception support |

### Library methods

While we pass library calls to the .NET runtime implementations, most of them either require generics or use native methods. We only implemented the following native methods:

* `System.Runtime.CompilerServices.RuntimeHelpers.InitializeArray(Array, RuntimeFieldHandle)` used for constant array initializatons (like `int[] a = new int[] {0, 1}`).
* `System.Math.Abs(Double)`, `System.Math.Cos(Double)` and `System.Math.Sqrt(Double)` required by some float instruction tests.
* `System.ValueType.GetHashCode()` required by user-defined value types, as they override this virtual method.

## Performance benchmarks

We performed all benchmarks mentioned here on a laptop with an AMD Ryzen 7 PRO 4750U with 8 cores and 16 virtual threads, a base clock of 1.7GHz and boost up to 4.1GHz, featuring 32 GB of RAM and running Windows 10.

We ran the tests on GraalVM version:

```
openjdk 11.0.15 2022-04-19
OpenJDK Runtime Environment GraalVM CE 22.1.0 (build 11.0.15+10-jvmci-22.1-b06)
OpenJDK 64-Bit Server VM GraalVM CE 22.1.0 (build 11.0.15+10-jvmci-22.1-b06, mixed mode, sharing)
```

and .NET version:

```
Host (useful for support):
  Version: 6.0.6
  Commit:  7cca709db2
```

### Harness

As mentioned in [BACILHelpers](#bacilhelpers), our way of exposing additional functionality comprises implementing them in the `BACILHelpers` assembly, which .NET calls directly and BACIL replaces with its own implementation. To facilitate benchmarks, we add two new methods: `StartTimer` which starts a timer and `GetTicks` which return the number of ticks since the start. The API was inspired by the API of `System.Diagnostics.StopWatch`, which is what the .NET implementation uses:

```C#
static Stopwatch stopWatch = new Stopwatch();
static long nanosecPerTick = (1000L * 1000L * 1000L) / Stopwatch.Frequency;

public static void StartTimer()
{
    stopWatch.Restart();
}

public static long GetTicks()
{
    stopWatch.Stop();
    return stopWatch.ElapsedTicks * nanosecPerTick;
}
```

On the BACIL side, we use `System.nanoTime()`, saving a value on start and the subtracting it from the value on end. Combining with the console writing capabilities, this is what our final harness looks like (`DoCalculation` and the iteration count being replaced as necessary):

```C#
static void report(int iteration, long ticks, int result)
{
    BACILHelpers.BACILConsole.Write("iteration:");
    BACILHelpers.BACILConsole.Write(iteration);
    BACILHelpers.BACILConsole.Write(" ticks:");
    BACILHelpers.BACILConsole.Write(ticks);
    BACILHelpers.BACILConsole.Write(" res:");
    BACILHelpers.BACILConsole.Write(result);
    BACILHelpers.BACILConsole.Write("\n");
} 

public static void Main(String[] args)
{
    int r;
    for(int i=0;i<1500;i++)
    {
        BACILHelpers.BACILEnvironment.StartTimer();
        var result = DoCalculation();
        long duration = BACILHelpers.BACILEnvironment.GetTicks();

        report(i, duration, result);
    }
}
```

### Hagmüller's work

One goal was to compare with Hagmüller's implementation from [Truffle CIL Interpreter (2020)](https://epub.jku.at/obvulihs/content/titleinfo/5473678). We received copies of the benchmark programs used in the work and could therefore run them against our implementation. When researching [.NET runtime JIT benchmarks](#net-runtime-jit-benchmarks), we discovered that the used benchmarks are based on code from the repository. However, to keep the comparison fair, we used the provided modified versions, only changing out the harness. We didn't have access to the interpreter itself, so couldn't replicate the original benchmarks, and only use the numbers provided in their work. To receive comparable numbers, we followed the outlined methodology:

> The discussed Truffle CIL Interpreter, was evaluated by running a set of different programs. All benchmarks were executed on an Intel i7-5557U processor with 2 cores, 4 virtual threads featuring 16GB of RAM and a core speed of 3.1 GHz running macOS Catalina(64 bit).
>
> We parametrized each benchmark so that its execution results in high workload for our test system. In order to get a performance reference to compare with, we executed the benchmark programs in the mono runtime. We ran the benchmark programs in our Truffle CIL Interpreter on the top of the Graal VM. To find out how much our Truffle CIL Interpreter benefits from the support of compilation by Graal, we also ran the tests in an interpreter only mode, by using the standard Java JDK, instead of Graal. Because Graal optimizes functions which are called a certain number of times, we executed each program in a loop a several amount of times. For our evaluation we wanted to ignore the warm up phase of the compilation, so we just took the last 10 iterations of the execution loop. For each iteration the execution time is measured. For these 10 iterations we calculated the arithmetic mean. In order to reduce statistical outliers we repeated this 10 times and calculated the geometric mean over the arithmetic means.

Our benchmarks had the following differences:

* instead of an unspecified version of the mono runtime, we used .NET 6.0.301 to get the reference performance
* our system was different
* we ignored "interpreter only mode" results - we tailored the interpreter for partial evaluation, so the slowdowns in interpreter mode are usually more than 200x; we don't see value in precisely benchmarking such a glaring difference

It wasn't obvious if CIL-level optimizations were enabled when compiling the tests in Hagmüller's work. For that reason, we measured both the debug (unoptimized) and release (optimized) compilation configurations.

The measured slowdowns (relative to .NET in release configuration) were:

|                       | Debug BACIL | Debug .NET | Release BACIL | Release .NET | Hagmüller |
|-----------------------|-------------|------------|---------------|--------------|-----------|
| Binarytrees           | 2.573       | 2.003      | 2.649         | 1            | 24        |
| Sieve of Eratosthenes | 2.172       | 4.696      | 1.698         | 1            | 226       |
| Fibonacci             | 1.635       | 4.449      | 1.512         | 1            | 38        |
| Mandelbrot            | 5.612       | 2.666      | 4.779         | 1            | 38        |
| N-Body                | 7.237       | 5.000      | 6.763         | 1            | 194       |

![alt](hagmuller1.svg)
_Linear bar chart including comparison with Hagmüller's work_

![alt](hagmuller2.svg)
_Linear bar chart with only BACIL results_

### .NET runtime JIT benchmarks

To get more performance comparisons, we used (a subset of) [.NET's JIT benchamrks](https://github.com/dotnet/runtime/tree/main/src/tests/JIT/Performance/CodeQuality). The subset selection was driven by picking only tests using features the BACIL implements.

Originally, the tests use `Xunit` framework for benchmarks. Apart from switching the `Xunit` harness for our own, we made no other modifications to the code.

Our methodology was driven by our interest in getting results for as many binaries as possible, rather than making sure the comparison is extremely precise. We ran each test once, took the arithmetic average of iterations 250-299 and calculated the slowdown ratio between BACIL and .NET. We used the benchmarks as compiled by the .NET runtime compilation process without changing any settings regarding optimizations.

The results were as follows:

| Benchmark        | BACIL Slowdown |
|------------------|----------------|
| TreeInsert       | 1.036          |
| Pi               | 1.125          |
| HeapSort         | 1.172          |
| Array1           | 1.322          |
| QuickSort        | 1.428          |
| Fib              | 1.519          |
| BubbleSort       | 1.538          |
| BubbleSort2      | 1.722          |
| CSieve           | 1.741          |
| fannkuch-redux-2 | 1.818          |
| spectralnorm-1   | 2.063          |
| MatInv4          | 2.351          |
| 8queens          | 2.355          |
| Permutate        | 2.595          |
| Ackermann        | 3.008          |
| TreeSort         | 3.158          |
| binarytrees-2    | 3.412          |
| Lorenz           | 4.600          |
| n-body-3         | 4.974          |

![alt](mybench.svg)
_Chart showing slowdown of BACIL compared to .NET runtime_

### Warmup concerns

One fact that's important for real-world performance but our benchmarks ignore is that both the internal workings of GraalVM and our design result in the warmup time (e.g. time before reaching full performance potential is) being significant.

While the cause inherent to GraalVM is the [tiered compilation](#tiered-compilation) model, which has to compromise between the time spent compiling and the quality of the resulting compilation, BACIL has another important performance limitation. As mentioned in [Nodes](#nodes), for BACIL the smallest compilation unit is a method and we don't support On-Stack Replacement (OSR). Therefore, performance for the first few iterations is (expectedly) terrible.

The example code from [Nodes](#nodes) (nested for loops in one method) runs about 60 times slower on BACIL than on .NET. Here's an example chart of time-per-iteration when running the `MatInv4` .NET runtime benchmark with default Truffle heuristics:

![alt](warmup.svg)

The first iteration was 83 times slower than iterations 30+.

### Interpreting the results

We draw two main conclusions from the performance benchmarks:

* our implementation outperforms Hagmüller's work
* in compiled code, BACIL is less than an order of magnitude slower than .NET runtime, with the worst case measured being 7.237 times slower

The last observation we want to make is regarding IL-level optimizations. While for the .NET runtime, the IL optimizations (in Release mode) made it significantly more performant, for BACIL such optimizations were very much insignificant. One interesting fact is that running Hagmüller's binarytrees on BACIL, they performed slightly worse in the optimized Release version than the Debug version. This probably has to do with the fact that the "optimizations" (which are surely tailored for .NET runtimes) resulted in using different instructions that were incidentally less performant on BACIL.

# Conclusion

* in a single person I was able to implement a fast interpreter for CIL = mission accomplished


# Appendix - Opcode implementation status

Implemented instructions:

```
add
and
beq
beq.s
bge
bge.s
bge.un
bge.un.s
bgt
bgt.s
bgt.un
bgt.un.s
ble
ble.s
ble.un
ble.un.s
blt
blt.s
blt.un
blt.un.s
bne.un
bne.un.s
box
br
brfalse
brfalse.s
br.s
brtrue
brtrue.s
call
callvirt
ceq
cgt
cgt.un
clt
clt.un
conv.i
conv.i1
conv.i2
conv.i4
conv.i8
conv.r4
conv.r8
conv.u
conv.u1
conv.u2
conv.u4
conv.u8
div
dup
initobj
ldarg.0
ldarg.1
ldarg.2
ldarg.3
ldarga.s
ldarg.s
ldc.i4
ldc.i4.0
ldc.i4.1
ldc.i4.2
ldc.i4.3
ldc.i4.4
ldc.i4.5
ldc.i4.6
ldc.i4.7
ldc.i4.8
ldc.i4.m1
ldc.i4.s
ldc.i8
ldc.r4
ldc.r8
ldelem
ldelema
ldelem.i
ldelem.i1
ldelem.i2
ldelem.i4
ldelem.i8
ldelem.r4
ldelem.r8
ldelem.ref
ldelem.u1
ldelem.u2
ldelem.u4
ldfld
ldflda
ldind.i
ldind.i1
ldind.i2
ldind.i4
ldind.i8
ldind.r4
ldind.r8
ldind.ref
ldind.u1
ldind.u2
ldind.u4
ldlen
ldloc.0
ldloc.1
ldloc.2
ldloc.3
ldloca.s
ldloc.s
ldnull
ldsfld
ldsflda
ldstr
ldtoken
mul
neg
newarr
newobj
nop
not
or
pop
rem
ret
shl
shr
shr.un
starg.s
stelem
stelem.i
stelem.i1
stelem.i2
stelem.i4
stelem.i8
stelem.r4
stelem.r8
stelem.ref
stfld
stind.i
stind.i1
stind.i2
stind.i4
stind.i8
stind.r4
stind.r8
stind.ref
stloc.0
stloc.1
stloc.2
stloc.3
stloc.s
stsfld
sub
unbox.any
xor 
```

Unimplemented instructions and prefixes:

```
add.ovf
add.ovf.un
arglist
break
calli
castclass
ckfinite
constrained.
conv.ovf.i
conv.ovf.i1
conv.ovf.i1.un
conv.ovf.i2
conv.ovf.i2.un
conv.ovf.i4
conv.ovf.i4.un
conv.ovf.i8
conv.ovf.i8.un
conv.ovf.i.un
conv.ovf.u
conv.ovf.u1
conv.ovf.u1.un
conv.ovf.u2
conv.ovf.u2.un
conv.ovf.u4
conv.ovf.u4.un
conv.ovf.u8
conv.ovf.u8.un
conv.ovf.u.un
conv.r.un
cpblk
cpobj
div.un
endfilter
endfinally
initblk
isinst
jmp
ldarg
ldarga
ldftn
ldloc
ldloca
ldobj
ldvirtftn
leave
leave.s
localloc
mkrefany
mul.ovf
mul.ovf.un
no.
readonly.
Refanytype
refanyval
rem.un
rethrow
sizeof
starg
stloc
stobj
sub.ovf
sub.ovf.un
switch
tail.
throw
unaligned.
unbox
volatile.
```

[^1]: https://github.com/oracle/graal/tree/master/truffle
[^2]: https://github.com/oracle/graal/tree/master/compiler
[^3]: Wuerthinger et al.: Practical Partial Evaluation for High-Performance Dynamic Language Runtimes, https://doi.org/10.1145/3062341.3062381
[^4]: https://www.ecma-international.org/publications/standards/Ecma-335.htm
[^5]: [Practical Partial Evaluation for High-Performance Dynamic Language Runtimes](https://dl.acm.org/doi/10.1145/3062341.3062381)
[^6]: [A brief look at file format design](http://decoy.iki.fi/texts/filefd/filefd)
[^7]: They can also be ahead-of-time compiled to native binaries, but that's out of scope for this work.