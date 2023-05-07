# CILOSTAZOL 💊
![graalVM CE build](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/actions/workflows/build_with_graalCE.yml/badge.svg)
![graalVM CE tests](https://github.com/Softwarovy-projekt/CILOSTAZOL-100mg/actions/workflows/test_with_graalCE.yml/badge.svg)

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