package jk_5.nailed.web.webserver.socketio

import com.corundumstudio.socketio.{AckRequest, SocketIOClient, Configuration, SocketIOServer}
import com.corundumstudio.socketio.listener.DataListener

/**
 * No description given
 *
 * @author jk-5
 */
object SIOServer {

  def start(){
    val config = new Configuration()
    config.setHostname("localhost")
    config.setPort(9002)

    val server = new SocketIOServer(config)
    server.addJsonObjectListener(classOf[TestObject], new DataListener[TestObject]{
      override def onData(client: SocketIOClient, data: TestObject, ack: AckRequest){
        println(data.something + " - " + data.somethingElse)
        client.sendJsonObject(data)
      }
    })
    server.start()
  }
}

class TestObject {
  var something: String = ""
  var somethingElse: String = ""
}
