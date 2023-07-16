$scriptPath = $PSScriptRoot;

$projects = Get-ChildItem -Path (Get-Item $scriptPath ).FullName -Include *.csproj -File -Recurse -ErrorAction SilentlyContinue

foreach ($project in $projects)
{

  Write-Host $project
  dotnet build -c=Release $project --output $project\..\bin
}