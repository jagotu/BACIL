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

| Opcode | Implemented | Tested |
| --- | -- | --- |
| add | x | x |
| and | x | x |
| beq | x | x |
| beq.s | x | x |
| bge | x | x |
| bge.s | x | x |
| bge.un | x | x |
| bge.un.s | x | x |
| bgt | x | x |
| bgt.s | x | x |
| bgt.un | x | x |
| bgt.un.s | x | x |
| ble | x | x |
| ble.s | x | x |
| ble.un | x | x |
| ble.un.s | x | x |
| blt | x | x |
| blt.s | x | x |
| blt.un | x | x |
| blt.un.s | x | x |
| bne.un | x | x |
| bne.un.s | x | x |
| box | x | x |
| br | x | x |
| brfalse | x | x |
| brfalse.s | x | x |
| br.s | x | x |
| brtrue | x | x |
| brtrue.s | x | x |
| call | x | x |
| callvirt | x | x |
| ceq | x | x |
| cgt | x | x |
| cgt.un | x | x |
| clt | x | x |
| clt.un | x | x |
| conv.i | x | x |
| conv.i1 | x | x |
| conv.i2 | x | x |
| conv.i4 | x | x |
| conv.i8 | x | x |
| conv.r4 | x | x |
| conv.r8 | x | x |
| conv.u | x | x |
| conv.u1 | x | x |
| conv.u2 | x | x |
| conv.u4 | x | x |
| conv.u8 | x | x |
| div | x | x |
| dup | x | x |
| initobj | x | x |
| ldarg.0 | o | x |
| ldarg.1 | o | x |
| ldarg.2 | o | x |
| ldarg.3 | o | x |
| ldarga.s | o | x |
| ldarg.s | o | x |
| ldc.i4 | o | x |
| ldc.i4.0 | o | x |
| ldc.i4.1 | o | x |
| ldc.i4.2 | o | x |
| ldc.i4.3 | o | x |
| ldc.i4.4 | o | x |
| ldc.i4.5 | o | x |
| ldc.i4.6 | o | x |
| ldc.i4.7 | o | x |
| ldc.i4.8 | o | x |
| ldc.i4.m1 | o | x |
| ldc.i4.s | o | o |
| ldc.i8 | o | x |
| ldc.r4 | o | x |
| ldc.r8 | o | x |
| ldelem | x | x |
| ldelema | x | x |
| ldelem.i | x | x |
| ldelem.i1 | x | x |
| ldelem.i2 | x | x |
| ldelem.i4 | x | x |
| ldelem.i8 | x | x |
| ldelem.r4 | x | x |
| ldelem.r8 | x | x |
| ldelem.ref | x | x |
| ldelem.u1 | x | x |
| ldelem.u2 | x | x |
| ldelem.u4 | x | x |
| ldfld | x | x |
| ldflda | x | x |
| ldind.i | x | x |
| ldind.i1 | x | x |
| ldind.i2 | x | x |
| ldind.i4 | x | x |
| ldind.i8 | x | x |
| ldind.r4 | x | x |
| ldind.r8 | x | x |
| ldind.ref | x | x |
| ldind.u1 | x | x |
| ldind.u2 | x | x |
| ldind.u4 | x | x |
| ldlen | x | x |
| ldloc.0 | o | x |
| ldloc.1 | o | x |
| ldloc.2 | o | x |
| ldloc.3 | o | x |
| ldloca.s | o | x |
| ldloc.s | o | x |
| ldnull | x | x |
| ldsfld | x | x |
| ldsflda | x | x |
| ldstr | x | x |
| ldtoken | x | x |
| mul | x | x |
| neg | x | x |
| newarr | x | x |
| newobj | x | x |
| nop | o | x |
| not | x | x |
| or | x | x |
| pop | o | x |
| rem | x | x |
| ret | o | o |
| shl | x | x |
| shr | x | x |
| shr.un | x | x |
| starg.s | x | x |
| stelem | x | x |
| stelem.i | x | x |
| stelem.i1 | x | x |
| stelem.i2 | x | x |
| stelem.i4 | x | x |
| stelem.i8 | x | x |
| stelem.r4 | x | x |
| stelem.r8 | x | x |
| stelem.ref | x | x |
| stfld | x | x |
| stind.i | x | x |
| stind.i1 | x | x |
| stind.i2 | x | x |
| stind.i4 | x | x |
| stind.i8 | x | x |
| stind.r4 | x | x |
| stind.r8 | x | x |
| stind.ref | x | x |
| stloc.0 | o | x |
| stloc.1 | o | x |
| stloc.2 | o | x |
| stloc.3 | o | x |
| stloc.s | o | x |
| stsfld | x | x |
| sub | x | x |
| unbox.any | x | x |
| xor | x | x |
| Doesn't have impl. in bachelor thesis |
| add.ovf | x | x |
| add.ovf.un | x | x |
| arglist | x | x |
| break | x | x |
| calli | x | x |
| castclass | x | x |
| ckfinite | x | x |
| constrained. | x | x |
| conv.ovf.i | x | x |
| conv.ovf.i1 | x | x |
| conv.ovf.i1.un | x | x |
| conv.ovf.i2 | x | x |
| conv.ovf.i2.un | x | x |
| conv.ovf.i4 | x | x |
| conv.ovf.i4.un | x | x |
| conv.ovf.i8 | x | x |
| conv.ovf.i8.un | x | x |
| conv.ovf.i.un | x | x |
| conv.ovf.u | x | x |
| conv.ovf.u1 | x | x |
| conv.ovf.u1.un | x | x |
| conv.ovf.u2 | x | x |
| conv.ovf.u2.un | x | x |
| conv.ovf.u4 | x | x |
| conv.ovf.u4.un | x | x |
| conv.ovf.u8 | x | x |
| conv.ovf.u8.un | x | x |
| conv.ovf.u.un | x | x |
| conv.r.un | x | x |
| cpblk | x | x |
| cpobj | x | x |
| div.un | x | x |
| endfilter | x | x |
| endfinally | x | x |
| initblk | x | x |
| isinst | x | x |
| jmp | x | x |
| ldarg | x | x |
| ldarga | x | x |
| ldftn | x | x |
| ldloc | x | x |
| ldloca | x | x |
| ldobj | x | x |
| ldvirtftn | x | x |
| leave | x | x |
| leave.s | x | x |
| localloc | x | x |
| mkrefany | x | x |
| mul.ovf | x | x |
| mul.ovf.un | x | x |
| no. | x | x |
| readonly. | x | x |
| Refanytype | x | x |
| refanyval | x | x |
| rem.un | x | x |
| rethrow | x | x |
| sizeof | x | x |
| starg | x | x |
| stloc | x | x |
| stobj | x | x |
| sub.ovf | x | x |
| sub.ovf.un | x | x |
| switch | x | x |
| tail. | x | x |
| throw | x | x |
| unaligned. | x | x |
| unbox | x | x |
| volatile. | x | x |
