package jk_5.nailed.web.webserver.socketio.transport.websocket

import io.netty.channel.Channel
import java.util.UUID
import jk_5.nailed.web.auth.AuthSession
import jk_5.nailed.web.webserver.socketio.SIOClient

/**
 * No description given
 *
 * @author jk-5
 */
class WebsocketClient(_channel: Channel, _uid: UUID, _session: AuthSession) extends SIOClient(_channel, _uid, _session)
