#!/bin/bash

# Build C
cmake -D"CMAKE_C_COMPILER:FILEPATH=C:\cygwin64\bin\x86_64-w64-mingw32-gcc.exe" -D"CMAKE_CXX_COMPILER:FILEPATH=C:\cygwin64\bin\x86_64-w64-mingw32-g++.exe" -G"Unix Makefiles" -S ../game -B ../build
cmake --build ../build

# Build Java
mvn -f ../ install

# Create archive
tar -cvf ../release-win64-$1.tar -C ../start-menu/target start-menu-1.0.0-jar-with-dependencies.jar
tar -rvf ../release-win64-$1.tar -C ../build lib
tar -rvf ../release-win64-$1.tar -C ../game textures shaders

# Create and add launch script
echo "java -D"java.library.path=lib" -jar start-menu-1.0.0-jar-with-dependencies.jar" >> launch.bat
tar -rvf ../release-win64-$1.tar launch.bat
rm launch.bat
