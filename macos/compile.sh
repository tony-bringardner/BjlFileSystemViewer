
idir=/Library/Java/JavaVirtualMachines/jdk-20.jdk/Contents/Home/include
name=UtiHandlerList

g++  -c -fPIC -I$idir/darwin -I$idir $name.cpp -o $name.o 
g++ -dynamiclib -o libnativekeyboard.dylib $name.o -lc
