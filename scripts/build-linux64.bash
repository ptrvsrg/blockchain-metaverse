#!/bin/bash

# Build C
cmake -G"Unix Makefiles" -S ../game -B ../game/build
cmake --build ../game/build

# Build Java
mvn install -f ../ -Dmaven.test.skip=true

# Create application directory
mkdir release-linux-$1
cp ../start-menu/target/start-menu-1.0.0-jar-with-dependencies.jar release-linux-$1
cp -r ../game/build/lib release-linux-$1
cp -r ../game/texture release-linux-$1
cp -r ../game/shaders release-linux-$1
echo "#!/bin/bash" > release-linux-$1/launch.bash
echo "java -D\"java.library.path=lib\" -jar start-menu-1.0.0-jar-with-dependencies.jar" >> release-linux-$1/launch.bash

# Create archive
tar -rvf ../release-linux-$1.tar -C . release-linux-$1
rm -rf release-linux-$1
