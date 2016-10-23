#!/bin/sh

# Test if the java compiler is installed
if ! hash nxjc 2> /dev/null
then
	echo "Could not find nxjc"
	echo "Did you install leJos and add it to your path?"
fi

# Test if the jar tool is installed
if ! hash jar 2> /dev/null
then
	echo "Could not find jar (the command line utility)"
	echo "Did you install the JDK and add it to your path?"
fi

mkdir bin/ 2>/dev/null # Redirect stdout to /dev/null
nxjc $(find src -name "*.java") -d bin/

cd bin/
jar -cf ../sensorboard-lib.jar $(find . -name "*.class")
