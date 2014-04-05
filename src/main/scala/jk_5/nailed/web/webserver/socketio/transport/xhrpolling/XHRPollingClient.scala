package jk_5.nailed.web.webserver.socketio.transport.xhrpolling

import jk_5.nailed.web.webserver.socketio.SIOClient
import io.netty.channel.Channel
import java.util.UUID
import jk_5.nailed.web.auth.AuthSession

/**
 * No description given
 *
 * @author jk-5
 */
class XHRPollingClient(_channel: Channel, _uid: UUID, _session: AuthSession) extends SIOClient(_channel, _uid, _session)
