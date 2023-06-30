#!/usr/bin/env fish

set SCRIPT_DIR (cd (dirname (status -f)); and pwd)

set projects (find $SCRIPT_DIR -name '*.csproj')

for project in $projects;
	echo $project
	dotnet build -c=Release $project
end