#!/usr/bin/groovy
@GrabResolver(name='custom', root='https://nexus.arcsmed.at/content/repositories/homer.core/', m2Compatible='true')
@GrabResolver(name='custom2', root='http://central.maven.org/maven2', m2Compatible='true')
@Grab(group='com.fazecast', module='jSerialComm', version='[2.0.0,3.0.0)')
import com.fazecast.jSerialComm.*
import groovy.transform.Field;

SerialPort comPort = SerialPort.getCommPorts()[0];
comPort.setComPortParameters(115200, 8, 1, 0);
comPort.openPort();
comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 3000, 0);
InputStream inputStream = comPort.getInputStream();

reader = new InputStreamReader(new BufferedInputStream(inputStream));

@Field
csv = new File('imu.csv')
@Field
samplerate = 0
@Field
start = 0
prevSerialPortData = ''
addHeaderForNewCSV()

reader.eachLine(){ serialPortData ->
    if (prevSerialPortData != serialPortData) {
    	// println serialPortData
	    csv << "$serialPortData\n"
	    samplerateInfo()
	    prevSerialPortData = serialPortData
	}
}

def samplerateInfo() {
    samplerate++
    elapsed = System.currentTimeMillis() - start
    if (elapsed >= 1000) {
        start = System.currentTimeMillis()
        println "$samplerate samples per second"
        samplerate = 0
    }
}

def addHeaderForNewCSV() {
    if (!csv.exists()) {
        csv << 'unix time,sample rate,accel x,accel y,accel z,gyro x,gyro y,gyro z\n'
    }
}
// currentValue = line.tokenize(',')[1].toInteger()