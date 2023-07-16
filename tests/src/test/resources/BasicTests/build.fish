#!/usr/bin/env fish

set SCRIPT_DIR (cd (dirname (status -f)); and pwd)

set projects (find $SCRIPT_DIR -name '*.csproj')

for project in $projects;
	echo $project
	set projectdir (dirname "$project")
    dotnet build -c=Release --output "$projectdir\bin" $project
end