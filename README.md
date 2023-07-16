# CILOSTAZOL 💊

![graalVM CE build](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/actions/workflows/build_with_graalCE.yml/badge.svg)
![graalVM CE tests](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/actions/workflows/test_with_graalCE.yml/badge.svg)

![commit activity](https://img.shields.io/github/commit-activity/w/Softwarovy-projekt/CILOSTAZOL-100mg)
![last commit](https://img.shields.io/github/last-commit/Softwarovy-projekt/CILOSTAZOL-100mg)

Continuation of BACIL interpreter

## Development

We recommend the following configuration steps to run and contribute to CILOSTAZOL.

1. Make a folder for CILOSTAZOL development (for example `CilostazolDev`).
2. Download [.NET 7](https://dotnet.microsoft.com/en-us/download/dotnet/7.0) core library
    - We copied `dotnet-runtime-7.0.1-win-x64/shared/Microsoft.NETCoreApp/7.0.1` folder
      to `CilostatolDev/Microsoft.NETCoreApp/7.0.1` folder.
3. Download [GraalVM](https://www.graalvm.org/downloads/) compiler (at least 21.3 version).
    - We put it into `CilostazolDev/GraalVM/graalvm-ce-java17-22.2.0`.
4. Download Cilostazol [repo](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg).
    - We put it into `CilostazolDev/CILOSTAZOL-100mg` folder.
5. Download Benchmarks and Tests [repo](https://github.com/Softwarovy-projekt/TestsAndBenchmarks).
    - We put it into `CilostazolDev/TestsAndBenchmarks` folder.
6. Build Benchmark and Tests (
   See [BenchmarksAndTests/README.md](https://github.com/Softwarovy-projekt/TestsAndBenchmarks/blob/main/README.md)).
7. Fix paths in `CilostazolDev/CILOSTAZOL-100mg/.run` folder.
    - We changed the following keys in `BACIL-clean-build-run.run.xml` and `BACIL-dry-run.run.xml`
        - `<graalvm-path>` to `../GraalVM/graalvm-ce-java17-22.2.0`
        - `<dotnet-core-lib-path>` to `../Microsoft.NETCoreApp/7.0.1`
        - `<dotnet-app-dll-path>` to `../TestsAndBenchmarks/Tests/OSR/CilostazolEx/bin/Debug/net7.0/CilostazolEx.dll`
8. Open IntelliJ IDEA IDE, locate the configurations and run it.

### Notes

It would be better to set Project SDK in IntelliJ IDEA to GraalVM SDK, becuase of CodeInsight...

**Configurations**

- `BACIL-build(Maven).run.xml` - Builds BACIL.
- `BACIL-clean-build-run.run.xml` - Builds BACIL and runs it.
- `BACIL-dry-run.run.xml` - Runs BACIL without building.

![LOC](https://img.shields.io/tokei/lines/github/Softwarovy-projekt/CILOSTAZOL-100mg)
![code size](https://img.shields.io/github/languages/code-size/Softwarovy-projekt/CILOSTAZOL-100mg)

### Dev

**Interpreter**

| Opcode | Implemented | Tested | Issue|
| --- | --- | --- | --- |
| add | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| and | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| beq | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| beq.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| bge | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| bge.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| bge.un | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| bge.un.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| bgt | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| bgt.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| bgt.un | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| bgt.un.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| ble | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| ble.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| ble.un | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| ble.un.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| blt | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| blt.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| blt.un | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| blt.un.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| bne.un | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| bne.un.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| box | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| br | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| brfalse | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| brfalse.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| br.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| brtrue | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| brtrue.s | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| call | x | x | x | 
| callvirt | x | x | x |
| ceq | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| cgt | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| cgt.un | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| clt | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| clt.un | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| conv.i | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.i1 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.i2 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.i4 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.i8 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.r4 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.r8 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.u | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.u1 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.u2 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.u4 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.u8 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| div | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| dup | x | x | x |
| initobj | x | x | x |
| ldarg.0 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldarg.1 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldarg.2 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldarg.3 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldarga.s | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldarg.s | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.0 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.1 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.2 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.3 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.4 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.5 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.6 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.7 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.8 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.m1 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i4.s | o | o | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.i8 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.r4 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldc.r8 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldelem | x | x | x |
| ldelema | x | x | x |
| ldelem.i | x | x | x |
| ldelem.i1 | x | x | x |
| ldelem.i2 | x | x | x |
| ldelem.i4 | x | x | x |
| ldelem.i8 | x | x | x |
| ldelem.r4 | x | x | x |
| ldelem.r8 | x | x | x |
| ldelem.ref | x | x | x |
| ldelem.u1 | x | x | x |
| ldelem.u2 | x | x | x |
| ldelem.u4 | x | x | x |
| ldfld | x | x | x |
| ldflda | x | x | x |
| ldind.i | x | x | x |
| ldind.i1 | x | x | x |
| ldind.i2 | x | x | x |
| ldind.i4 | x | x | x |
| ldind.i8 | x | x | x |
| ldind.r4 | x | x | x |
| ldind.r8 | x | x | x |
| ldind.ref | x | x | x |
| ldind.u1 | x | x | x |
| ldind.u2 | x | x | x |
| ldind.u4 | x | x | x |
| ldlen | x | x | x |
| ldloc.0 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldloc.1 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldloc.2 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldloc.3 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldloca.s | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldloc.s | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldnull | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| ldsfld | x | x | x |
| ldsflda | x | x | x |
| ldstr | x | x | x |
| ldtoken | x | x | x |
| mul | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| neg | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| newarr | x | x | x |
| newobj | x | x | x |
| nop | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| not | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| or | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| pop | x | x | x |
| rem | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| ret | o | o | x |
| shl | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| shr | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| shr.un | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| starg.s | x | x | x |
| stelem | x | x | x |
| stelem.i | x | x | x |
| stelem.i1 | x | x | x |
| stelem.i2 | x | x | x |
| stelem.i4 | x | x | x |
| stelem.i8 | x | x | x |
| stelem.r4 | x | x | x |
| stelem.r8 | x | x | x |
| stelem.ref | x | x | x |
| stfld | x | x | x |
| stind.i | x | x | x |
| stind.i1 | x | x | x |
| stind.i2 | x | x | x |
| stind.i4 | x | x | x |
| stind.i8 | x | x | x |
| stind.r4 | x | x | x |
| stind.r8 | x | x | x |
| stind.ref | x | x | x |
| stloc.0 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| stloc.1 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| stloc.2 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| stloc.3 | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| stloc.s | x | x | [#73](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/73) |
| stsfld | x | x | x |
| sub | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| unbox.any | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| xor | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| Doesn't have impl. in bachelor thesis |
| add.ovf | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| add.ovf.un | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| arglist | x | x | x |
| break | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| calli | x | x | x |
| castclass | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| ckfinite | x | x | x |
| constrained. | x | x | x |
| conv.ovf.i | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.i1 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.i1.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.i2 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.i2.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.i4 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.i4.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.i8 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.i8.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.i.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.u | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.u1 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.u1.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.u2 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.u2.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.u4 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.u4.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.u8 | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.u8.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.ovf.u.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| conv.r.un | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| cpblk | x | x | x |
| cpobj | x | x | x |
| div.un | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| endfilter | x | x | [#61](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/61) |
| endfinally | x | x | [#61](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/61) |
| initblk | x | x | x |
| isinst | x | x | x |
| jmp | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| ldarg | x | x | x |
| ldarga | x | x | x |
| ldftn | x | x | x |
| ldloc | x | x | x |
| ldloca | x | x | x |
| ldobj | x | x | x |
| ldvirtftn | x | x | x |
| leave | x | x | x |
| leave.s | x | x | x |
| localloc | x | x | x |
| mkrefany | x | x | x |
| mul.ovf | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| mul.ovf.un | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| no. | x | x | x |
| readonly. | x | x x | |
| Refanytype | x | x | x |
| refanyval | x | x | x |
| rem.un | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| rethrow | x | x | [#61](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/61) |
| sizeof | x | x | x |
| starg | x | x | x |
| stloc | x | x | x |
| stobj | x | x | x |
| sub.ovf | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| sub.ovf.un | x | x | [#75](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/75) |
| switch | x | x | [#60](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/60) |
| tail. | x | x | x |
| throw | x | x | [#61](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/61) |
| unaligned. | x | x |
| unbox | x | x | [#66](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/issues/66) |
| volatile. | x | x | x |
