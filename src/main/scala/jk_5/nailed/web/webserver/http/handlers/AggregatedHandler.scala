package jk_5.nailed.web.webserver.http.handlers

import io.netty.channel._
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.HttpHeaders._
import io.netty.buffer.{CompositeByteBuf, Unpooled}
import io.netty.handler.codec.{DecoderResult, TooLongFrameException}
import io.netty.util.ReferenceCountUtil

/**
 * No description given
 *
 * @author jk-5
 */
object AggregatedHandler {
  private val continue = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER)
  private val maxLength = 1048576
  private val maxCumulationBufferComponents = 1024
}

trait AggregatedHandler extends SimpleChannelInboundHandler[HttpObject] {

  private var currentMessage: FullHttpRequest = null
  private var tooLongFrameFound = false

  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpObject){
    var currentMessage = this.currentMessage
    msg match {
      case m: HttpRequest =>
        tooLongFrameFound = false
        assert(currentMessage == null)
        if(is100ContinueExpected(m)){
          ctx.writeAndFlush(AggregatedHandler.continue).addListener(new ChannelFutureListener {
            override def operationComplete(future: ChannelFuture) = if(!future.isSuccess) {
              ctx.fireExceptionCaught(future.cause)
            }
          })
        }
        if(!m.getDecoderResult.isSuccess){
          removeTransferEncodingChunked(m)
          this.handleAggregated(ctx, toFullMessage(m))
          this.currentMessage = null
          ReferenceCountUtil.release(msg)
          return
        }
        msg match {
          case header: HttpRequest =>
            this.currentMessage = {
              currentMessage = new DefaultFullHttpRequest(header.getProtocolVersion, header.getMethod, header.getUri, Unpooled.compositeBuffer(AggregatedHandler.maxCumulationBufferComponents))
              currentMessage
            }
          case _ => throw new Error
        }
        currentMessage.headers.set(m.headers)
        removeTransferEncodingChunked(currentMessage)
      case chunk: HttpContent =>
        if(tooLongFrameFound){
          if(msg.isInstanceOf[LastHttpContent]) this.currentMessage = null
          ReferenceCountUtil.release(msg)
          return
        }
        assert(currentMessage != null)
        val content = currentMessage.content.asInstanceOf[CompositeByteBuf]
        if(content.readableBytes > AggregatedHandler.maxLength - chunk.content.readableBytes) {
          tooLongFrameFound = true
          currentMessage.release
          this.currentMessage = null
          throw new TooLongFrameException("HTTP content length exceeded " + AggregatedHandler.maxLength + " bytes.")
        }
        if(chunk.content.isReadable){
          chunk.retain
          content.addComponent(chunk.content)
          content.writerIndex(content.writerIndex() + chunk.content.readableBytes())
        }
        var last = false
        if(!chunk.getDecoderResult.isSuccess){
          currentMessage.setDecoderResult(DecoderResult.failure(chunk.getDecoderResult.cause))
          last = true
        }else{
          last = chunk.isInstanceOf[LastHttpContent]
        }
        if(last){
          this.currentMessage = null
          chunk match{
            case trailer: LastHttpContent => currentMessage.headers.add(trailer.trailingHeaders)
            case _ =>
          }
          currentMessage.headers.set(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(content.readableBytes))
          this.handleAggregated(ctx, currentMessage)
        }
      case _ => throw new Error
    }
    ReferenceCountUtil.release(msg)
  }

  def handleAggregated(ctx: ChannelHandlerContext, msg: FullHttpRequest)

  override def channelInactive(ctx: ChannelHandlerContext){
    super.channelInactive(ctx)
    if(currentMessage != null) {
      currentMessage.release
      currentMessage = null
    }
  }

  override def handlerRemoved(ctx: ChannelHandlerContext){
    super.handlerRemoved(ctx)
    if(currentMessage != null) {
      currentMessage.release
      currentMessage = null
    }
  }

  private def toFullMessage(msg: HttpRequest): FullHttpRequest = msg match {
    case msg: FullHttpRequest => msg.retain
    case msg: HttpRequest => new DefaultFullHttpRequest(msg.getProtocolVersion, msg.getMethod, msg.getUri, Unpooled.EMPTY_BUFFER, false)
  }
}
