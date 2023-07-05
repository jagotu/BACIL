#!/usr/bin/env bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )")

projects=$(find $SCRIPT_DIR -name '*.csproj')

for project in $projects; do
	echo $project
	dotnet build -c=Release "${project}\..\bin"
done