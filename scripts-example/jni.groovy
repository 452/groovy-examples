@Grab(group='net.java.dev.jna', module='jna', version='4.5.2')
import com.sun.jna.Library;
import com.sun.jna.Native;

interface NativeExample extends Library {
	void printValue(String value)
};

def nativeLibrary = Native.loadLibrary(System.getProperty('nativeGroovyLibPath'), NativeExample.class)

nativeLibrary.printValue('Go go gadget Manual JNA')

//gcc -c -Wall -Werror -fpic nativeGroovy.c -o nativeGroovy.o; gcc -shared -o nativeGroovy.so nativeGroovy.o; rm nativeGroovy.o; groovy -DnativeGroovyLibPath=./nativeGroovy.so jni.groovy
