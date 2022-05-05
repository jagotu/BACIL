# BACIL 🦠

An experimental interpreter for .NET CIL binaries for [GraalVM](https://github.com/oracle/graal). While it only implements a small subset of the full [ECMA-335 Common Language Infrastructure (CLI) standard](https://www.ecma-international.org/publications-and-standards/standards/ecma-335/), stuff it can run it runs with performance comparable to .NET 5.0's official JIT runtime (citation pending 😇).

The project is written like an interpreter (you will find a familiar `while(true) switch(opcode)` in [BytecodeNode.execute()](language/src/main/java/com/vztekoverflow/bacil/nodes/BytecodeNode.java)), and leverages [Truffle](https://github.com/oracle/graal/tree/master/truffle) and the Graal compiler to turn it into a Just-In-Time (JIT) compiler.

Written as a part of a bachelor thesis called _Truffle based .NET IL interpreter and compiler: run C# on Java Virtual Machine_ at Charles University, Prague. The thesis text is slowly appearing [here](thesis_text/thesis_text.md). Once the thesis is complete, it will be linked here.

## Building

For ease of building we use [Maven](https://maven.apache.org/), therefore building consists of running `mvn package` in the root of the project.

The resulting artifacts will be the language jar in `language/target/language-1.0-SNAPSHOT.jar` and a (shaded) launcher jar in `launcher/target/bacil-launcher.jar`.

## Running

For running you'll need to [obtain GraalVM](https://www.graalvm.org/downloads/) and a [copy of a .NET standard library](https://dotnet.microsoft.com/download/dotnet). While the target for development was Java 8 and .NET Runtime 5.0.6 on both Linux and Windows, we succesfully ran the project on Java 11 with .NET 6.

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

