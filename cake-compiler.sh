#!/bin/bash


if [ "$1" = "release" ]; then
   mvn -Drelease clean source:jar javadoc:jar deploy
   echo "now login and goto https://oss.sonatype.org/index.html#stagingRepositories"
   echo "select staging repository and verify its okay, then select close and then release"
   echo "select drop if you want to start over"
   exit
elif [ "$1" = "snapshot" ]; then
   mvn clean source:jar javadoc:jar deploy
  exit
elif [ -z "$1" ]; then 
	echo Usage: $0 target
	echo where target is:
else
	echo Unknown target: "$1"
	echo Valid targets are:

fi

echo "  snapshot   Builds and uploads snapshots to sonatype.org"
echo "  release    Builds and uploads released jars to sonatype.org"

