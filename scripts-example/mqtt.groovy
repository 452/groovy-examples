@GrabResolver(name='Paho', root='https://repo.eclipse.org/content/repositories/paho-releases/')
@Grab(group='org.eclipse.paho', module='mqtt-client', version='0.4.0')

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence

String tmpDir = System.getProperty("java.io.tmpdir")
MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir)

MqttClient client = new MqttClient("tcp://localhost:1883", "PublisherClient", dataStore)
client.connect()
MqttMessage message = new MqttMessage('Hello world!!'.bytes)
message.setQos(0)
print "publishing.."
long startTime = System.currentTimeMillis()
(1..1000000).each {
    client.publish('topic', 'Hello world!!'.bytes, 0, false)
}
client.publish('/exit', 'Exit'.bytes, 0, false)
long stopTime = System.currentTimeMillis()
long spentTime = stopTime -startTime
println "published in ${spentTime} ms"
client.disconnect()
println "disconnected"
