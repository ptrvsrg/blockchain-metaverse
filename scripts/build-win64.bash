#!/bin/bash

# Build C
cmake -D"CMAKE_C_COMPILER:FILEPATH=C:\cygwin64\bin\x86_64-w64-mingw32-gcc.exe" -D"CMAKE_CXX_COMPILER:FILEPATH=C:\cygwin64\bin\x86_64-w64-mingw32-g++.exe" -G"Unix Makefiles" -S ../game -B ../game/build
cmake --build ../game/build

# Build Java
mvn install -f ../ -Dmaven.test.skip=true

# Create application directory
mkdir release-win64-$1
cp ../start-menu/target/start-menu-1.0.0-jar-with-dependencies.jar release-win64-$1
cp -r ../game/build/lib release-win64-$1
cp -r ../game/texture release-win64-$1
cp -r ../game/shaders release-win64-$1
echo "java -D\"java.library.path=lib\" -jar start-menu-1.0.0-jar-with-dependencies.jar" > release-win64-$1/launch.bat

# Create archive
tar -rvf ../release-win64-$1.tar -C . release-win64-$1
rm -rf release-win64-$1
