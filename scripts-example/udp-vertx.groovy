@Grab('io.vertx:vertx-core:3.6.0')
@Grab('io.vertx:vertx-lang-groovy:3.6.0')
import groovy.transform.CompileStatic
import io.vertx.core.datagram.DatagramSocketOptions
import io.vertx.core.*
import io.vertx.core.buffer.Buffer

Vertx vertx = Vertx.vertx()

hostname = InetAddress.localHost.canonicalHostName
ip = ['/bin/bash', '-c', /hostname -I | cut -d" " -f 1/].execute().text

def socket = vertx.createDatagramSocket(new DatagramSocketOptions().setReuseAddress(true).setReusePort(true))
def socketForSend = vertx.createDatagramSocket([:])
socket.listen(6969, '0.0.0.0', { asyncResult ->
  if (asyncResult.succeeded()) {
    socket.handler({ packet ->
      String decoded = packet.data().getString(0, packet.data().length())
      println "Received: $decoded from: $packet.sender"
      socket.send("$hostname $ip", packet.sender().port(), packet.sender().host(), { sendResult ->
         println("Send succeeded? ${sendResult.succeeded()}")
      })
      socketForSend.send('Hello', 6968, '255.255.255.255', { sendResult ->
         println("Send succeeded? ${sendResult.succeeded()}")
      })
    })
  } else {
    println("Listen failed${asyncResult.cause()}")
  }
})