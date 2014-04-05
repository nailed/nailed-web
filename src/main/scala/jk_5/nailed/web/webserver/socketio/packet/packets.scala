package jk_5.nailed.web.webserver.socketio.packet

import jk_5.jsonlibrary.JsonObject
import scala.collection.mutable

/**
 * No description given
 *
 * @author jk-5
 */
class DisconnectSIOPacket extends SIOPacket(PacketType.DISCONNECT)
class ConnectSIOPacket extends SIOPacket(PacketType.CONNECT)
class HeartbeatSIOPacket extends SIOPacket(PacketType.HEARTBEAT)
class MessageSIOPacket(var message: String = null) extends SIOPacket(PacketType.MESSAGE)
class JsonSIOPacket(var json: JsonObject = new JsonObject) extends SIOPacket(PacketType.JSON)
class EventSIOPacket(var name: String = null, var args: mutable.ArrayBuffer[JsonObject] = mutable.ArrayBuffer[JsonObject]()) extends SIOPacket(PacketType.EVENT)
class AckSIOPacket extends SIOPacket(PacketType.ACK)
class ErrorSIOPacket extends SIOPacket(PacketType.ERROR)
class NoopSIOPacket extends SIOPacket(PacketType.NOOP)
