# CILOSTAZOL 💊

Continuation of BACIL interpreter

## Development

We recommend the following configuration steps to run and contribute to CILOSTAZOL.

1. Make a folder for CILOSTAZOL development (for example `CilostazolDev`).
2. Download [.NET 7](https://dotnet.microsoft.com/en-us/download/dotnet/7.0) core library
    - We copied `dotnet-runtime-7.0.1-win-x64/shared/Microsoft.NETCoreApp/7.0.1` folder to `CilostatolDev/Microsoft.NETCoreApp/7.0.1` folder.
3. Download [GraalVM](https://www.graalvm.org/downloads/) compiler (at least 21.3 version).
    - We put it into `CilostazolDev/GraalVM/graalvm-ce-java17-22.2.0`.
4. Download Cilostazol [repo](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg).
    - We put it into `CilostazolDev/CILOSTAZOL-100mg` folder.
5. Download Benchmarks and Tests [repo](https://github.com/Softwarovy-projekt/TestsAndBenchmarks).
   - We put it into `CilostazolDev/TestsAndBenchmarks` folder.
6. Build Benchmark and Tests (See [BenchmarksAndTests/README.md](https://github.com/Softwarovy-projekt/TestsAndBenchmarks/blob/main/README.md)).
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

# BACIL 🦠

An experimental interpreter for .NET CIL binaries for [GraalVM](https://github.com/oracle/graal). While it only implements a [small subset](thesis_text/thesis_text.md#Completeness) of the full [ECMA-335 Common Language Infrastructure (CLI) standard](https://www.ecma-international.org/publications-and-standards/standards/ecma-335/), stuff it can run it [runs within 10 times slower than .NET's official JIT runtime](thesis_text/thesis_text.md#performance-benchmarks).

The project is written like an interpreter (you will find a familiar `while(true) switch(opcode)` in [BytecodeNode.execute()](language/src/main/java/com/vztekoverflow/bacil/nodes/BytecodeNode.java)), and leverages [Truffle](https://github.com/oracle/graal/tree/master/truffle) and the Graal compiler to turn it into a Just-In-Time (JIT) compiler.

Written as a part of a bachelor thesis called _Truffle based .NET IL interpreter and compiler: run C# on Java Virtual Machine_ at Charles University, Prague. The thesis text is slowly appearing [here](thesis_text/thesis_text.md). Once the thesis is complete, it will be linked here.

## Building

For ease of building we use [Maven](https://maven.apache.org/), therefore building consists of running `mvn package` in the root of the project.
> You will need java [GraalVM compiler](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-21.3.3.1) of version at least 21.3

The resulting artifacts will be the language jar in `language/target/language-1.0-SNAPSHOT.jar` and a (shaded) launcher jar in `launcher/target/bacil-launcher.jar`.

## Running

For running you'll need to [obtain GraalVM](https://www.graalvm.org/downloads/) and a [copy of a .NET standard library](https://dotnet.microsoft.com/download/dotnet). While the target for development was Java 8 and .NET Runtime 5.0.6 on both Linux and Windows, we succesfully ran the project on Java 11 and 17 with .NET 6.

You'll also need an assembly to run. If you don't have any handy, you can clone [BACIL_examples](https://github.com/jagotu/BACIL_examples).

Once you have all the prerequisites, you can run BACIL on Windows with .NET Runtime 5.0.6 like so:

```
BACIL>java -version
openjdk version "1.8.0_282"
OpenJDK Runtime Environment (build 1.8.0_282-b07)
OpenJDK 64-Bit Server VM GraalVM CE 21.0.0 (build 25.282-b07-jvmci-21.0-b06, mixed mode)

BACIL>java -Dtruffle.class.path.append=language/target/language-1.0-SNAPSHOT.jar -jar launcher/target/bacil-launcher.jar "--cil.libraryPath=c:\Program Files\dotnet\shared\Microsoft.NETCore.App\5.0.6" BACIL_examples\Inheritance\bin\Debug\net5.0\TestHarness.dll
Micka: Meow
Rex: Woof
Returned 4
Returned: 0
Runtime: 110ms
```

Apart from the last 2 lines (added by BACIL to aid debugging), the output should be identical when running the assemblies directly.

```
BACIL>dotnet BACIL_examples\Inheritance\bin\Debug\net5.0\TestHarness.dll
Micka: Meow
Rex: Woof
Returned 4
```

On Linux it's very similiar:

```
$ java -version
openjdk version "1.8.0_292"
OpenJDK Runtime Environment (build 1.8.0_292-b09)
OpenJDK 64-Bit Server VM GraalVM CE 21.1.0 (build 25.292-b09-jvmci-21.1-b05, mixed mode)

$ java -Dtruffle.class.path.append=language/target/language-1.0-SNAPSHOT.jar -jar launcher/target/bacil-launcher.jar --cil.libraryPath=../dotnet-runtime-5.0.6-linux-x64/shared/Microsoft.NETCore.App/5.0.6 BACIL_examples/Inheritance/bin/Debug/net5.0/TestHarness.dll
Micka: Meow
Rex: Woof
Returned 4
Returned: 0
Runtime: 348ms

$ dotnet BACIL_examples/Inheritance/bin/Debug/net5.0/TestHarness.dll
Micka: Meow
Rex: Woof
Returned 4

```

Always make sure you are running GraalVM and replace the `libraryPath` with path to the .NET standard library DLLs.

## Development

For development purposes you can use provided Intellij configurations.
 - CILOSTAZOL-100mg, copy of .NET standard library and assembly to run (provided BACIL_examples) is supposed to be at the same level.
 - Make sure GraalVM java version is configured correctly
 - run BACIL-clean-build application
