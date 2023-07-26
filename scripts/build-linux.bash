#!/bin/bash

# Build C
cmake -G"Unix Makefiles" -S ../game -B ../build
cmake --build ../build

# Build Java
mvn -f ../ install

# Create archive
tar -cvf ../release-linux-$1.tar -C ../start-menu/target start-menu-1.0.0-jar-with-dependencies.jar
tar -rvf ../release-linux-$1.tar -C ../build lib
tar -rvf ../release-linux-$1.tar -C ../game textures shaders

# Create and add launch script
echo "#!/bin/bash" > launch.bash
echo "java -D"java.library.path=lib" -jar start-menu-1.0.0-jar-with-dependencies.jar" >> launch.bash
chmod +x launch.bash
tar -rvf ../release-linux-$1.tar launch.bash
rm launch.bash
