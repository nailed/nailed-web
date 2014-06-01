package jk_5.nailed.web.webserver.ipc

import io.netty.channel.Channel
import jk_5.nailed.web.game.GameServer

/**
 * No description given
 *
 * @author jk-5
 */
class FakeGameServer(private val channel: Channel) extends GameServer(channel)
