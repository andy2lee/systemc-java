# systemc-java
SystemC for Java via Java Panama(FFM)

## prerequisite
1) SystemC Lib download Ref: https://www.accellera.org/downloads/standards/systemc
2) Mingw POSIX prebuild verison install Ref: https://github.com/niXman/mingw-builds-binaries/releases
3) CMake install

## Compile SystemC on Windows OS
1) Enter your SystemC folder that has been already downloaded.
2) `mkdir build` then `cd build`
3) `cmake .. -G "MinGW Makefiles" -DCMAKE_C_COMPILER=gcc -DCMAKE_CXX_COMPILER=g++ -DCMAKE_INSTALL_PREFIX="D:/software/systemc" -DCMAKE_BUILD_TYPE=Release -DCMAKE_CXX_STANDARD=17`
4) cmake --build . --config Release
5) cmake --install .

## Build cpp Wrapper
1) Change the SystemC path which is already installed in run.ps1 file.
2) Run `run.ps1`

## Java API
1) `javac -cp . App.java` and `java -cp . App`
