package jk_5.nailed.web.webserver.ipc.packet

import io.netty.buffer.{ByteBufProcessor, ByteBuf}
import jk_5.nailed.web.game.GameServer
import jk_5.nailed.web.webserver.ipc.PacketUtils
import java.nio.{ByteBuffer, ByteOrder}
import java.io.{InputStream, OutputStream}
import java.nio.channels.{ScatteringByteChannel, GatheringByteChannel}
import java.nio.charset.Charset

/**
 * No description given
 *
 * @author jk-5
 */
abstract class IpcPacket {
  def encode(buffer: ByteBuf)
  def decode(buffer: ByteBuf)
  def processPacket(server: GameServer)

  implicit def toIpcBuffer(buffer: ByteBuf): IpcBuffer = new IpcBuffer(buffer)
}

class IpcBuffer(val buffer: ByteBuf) extends ByteBuf {

  def writeString(s: String) = PacketUtils.writeString(s, buffer)





  override def capacity() = buffer.capacity()

  override def capacity(newCapacity: Int) = buffer.capacity(newCapacity)

  override def maxCapacity() = buffer.maxCapacity()

  override def alloc() = buffer.alloc()

  override def order() = buffer.order()

  override def order(endianness: ByteOrder) = buffer.order(endianness)

  override def unwrap() = buffer.unwrap()

  override def isDirect = buffer.isDirect

  override def readerIndex() = buffer.readerIndex()

  override def readerIndex(readerIndex: Int) = buffer.readerIndex(readerIndex)

  override def writerIndex() = buffer.writerIndex()

  override def writerIndex(writerIndex: Int) = buffer.writerIndex(writerIndex)

  override def setIndex(readerIndex: Int, writerIndex: Int) = buffer.setIndex(readerIndex, writerIndex)

  override def readableBytes() = buffer.readableBytes()

  override def writableBytes() = buffer.writableBytes()

  override def maxWritableBytes() = buffer.maxWritableBytes()

  override def isReadable = buffer.isReadable

  override def isReadable(size: Int) = buffer.isReadable(size)

  override def isWritable = buffer.isWritable

  override def isWritable(size: Int) = buffer.isWritable(size)

  override def clear() = buffer.clear()

  override def markReaderIndex() = buffer.markReaderIndex()

  override def resetReaderIndex() = buffer.resetReaderIndex()

  override def markWriterIndex() = buffer.markWriterIndex()

  override def resetWriterIndex() = buffer.resetWriterIndex()

  override def discardReadBytes() = buffer.discardReadBytes()

  override def discardSomeReadBytes() = buffer.discardSomeReadBytes()

  override def ensureWritable(minWritableBytes: Int) = buffer.ensureWritable(minWritableBytes)

  override def ensureWritable(minWritableBytes: Int, force: Boolean) = buffer.ensureWritable(minWritableBytes, force)

  override def getBoolean(index: Int) = buffer.getBoolean(index)

  override def getByte(index: Int) = buffer.getByte(index)

  override def getUnsignedByte(index: Int) = buffer.getUnsignedByte(index)

  override def getShort(index: Int) = buffer.getShort(index)

  override def getUnsignedShort(index: Int) = buffer.getUnsignedShort(index)

  override def getMedium(index: Int) = buffer.getMedium(index)

  override def getUnsignedMedium(index: Int) = buffer.getUnsignedMedium(index)

  override def getInt(index: Int) = buffer.getInt(index)

  override def getUnsignedInt(index: Int) = buffer.getUnsignedInt(index)

  override def getLong(index: Int) = buffer.getLong(index)

  override def getChar(index: Int) = buffer.getChar(index)

  override def getFloat(index: Int) = buffer.getFloat(index)

  override def getDouble(index: Int) = buffer.getDouble(index)

  override def getBytes(index: Int, dst: ByteBuf) = buffer.getBytes(index, dst)

  override def getBytes(index: Int, dst: ByteBuf, length: Int) = buffer.getBytes(index, dst, length)

  override def getBytes(index: Int, dst: ByteBuf, dstIndex: Int, length: Int) = buffer.getBytes(index, dst, dstIndex, length)

  override def getBytes(index: Int, dst: Array[Byte]) = buffer.getBytes(index, dst)

  override def getBytes(index: Int, dst: Array[Byte], dstIndex: Int, length: Int) = buffer.getBytes(index, dst, dstIndex, length)

  override def getBytes(index: Int, dst: ByteBuffer) = buffer.getBytes(index, dst)

  override def getBytes(index: Int, out: OutputStream, length: Int) = buffer.getBytes(index, out, length)

  override def getBytes(index: Int, out: GatheringByteChannel, length: Int) = buffer.getBytes(index, out, length)

  override def setBoolean(index: Int, value: Boolean) = buffer.setBoolean(index, value)

  override def setByte(index: Int, value: Int) = buffer.setByte(index, value)

  override def setShort(index: Int, value: Int) = buffer.setShort(index, value)

  override def setMedium(index: Int, value: Int) = buffer.setMedium(index, value)

  override def setInt(index: Int, value: Int) = buffer.setInt(index, value)

  override def setLong(index: Int, value: Long) = buffer.setLong(index, value)

  override def setChar(index: Int, value: Int) = buffer.setChar(index, value)

  override def setFloat(index: Int, value: Float) = buffer.setFloat(index, value)

  override def setDouble(index: Int, value: Double) = buffer.setDouble(index, value)

  override def setBytes(index: Int, src: ByteBuf) = buffer.setBytes(index, src)

  override def setBytes(index: Int, src: ByteBuf, length: Int) = buffer.setBytes(index, src, length)

  override def setBytes(index: Int, src: ByteBuf, srcIndex: Int, length: Int) = buffer.setBytes(index, src, srcIndex, length)

  override def setBytes(index: Int, src: Array[Byte]) = buffer.setBytes(index, src)

  override def setBytes(index: Int, src: Array[Byte], srcIndex: Int, length: Int) = buffer.setBytes(index, src, srcIndex, length)

  override def setBytes(index: Int, src: ByteBuffer) = buffer.setBytes(index, src)

  override def setBytes(index: Int, in: InputStream, length: Int) = buffer.setBytes(index, in, length)

  override def setBytes(index: Int, in: ScatteringByteChannel, length: Int) = buffer.setBytes(index, in, length)

  override def setZero(index: Int, length: Int) = buffer.setZero(index, length)

  override def readBoolean() = buffer.readBoolean()

  override def readByte() = buffer.readByte()

  override def readUnsignedByte() = buffer.readUnsignedByte()

  override def readShort() = buffer.readShort()

  override def readUnsignedShort() = buffer.readUnsignedShort()

  override def readMedium() = buffer.readMedium()

  override def readUnsignedMedium() = buffer.readUnsignedMedium()

  override def readInt() = buffer.readInt()

  override def readUnsignedInt() = buffer.readUnsignedInt()

  override def readLong() = buffer.readLong()

  override def readChar() = buffer.readChar()

  override def readFloat() = buffer.readFloat()

  override def readDouble() = buffer.readDouble()

  override def readBytes(length: Int) = buffer.readBytes(length)

  override def readSlice(length: Int) = buffer.readSlice(length)

  override def readBytes(dst: ByteBuf) = buffer.readBytes(dst)

  override def readBytes(dst: ByteBuf, length: Int) = buffer.readBytes(dst, length)

  override def readBytes(dst: ByteBuf, dstIndex: Int, length: Int) = buffer.readBytes(dst, dstIndex, length)

  override def readBytes(dst: Array[Byte]) = buffer.readBytes(dst)

  override def readBytes(dst: Array[Byte], dstIndex: Int, length: Int) = buffer.readBytes(dst, dstIndex, length)

  override def readBytes(dst: ByteBuffer) = buffer.readBytes(dst)

  override def readBytes(out: OutputStream, length: Int) = buffer.readBytes(out, length)

  override def readBytes(out: GatheringByteChannel, length: Int) = buffer.readBytes(out, length)

  override def skipBytes(length: Int) = buffer.skipBytes(length)

  override def writeBoolean(value: Boolean) = buffer.writeBoolean(value)

  override def writeByte(value: Int) = buffer.writeByte(value)

  override def writeShort(value: Int) = buffer.writeShort(value)

  override def writeMedium(value: Int) = buffer.writeMedium(value)

  override def writeInt(value: Int) = buffer.writeInt(value)

  override def writeLong(value: Long) = buffer.writeLong(value)

  override def writeChar(value: Int) = buffer.writeChar(value)

  override def writeFloat(value: Float) = buffer.writeFloat(value)

  override def writeDouble(value: Double) = buffer.writeDouble(value)

  override def writeBytes(src: ByteBuf) = buffer.writeBytes(src)

  override def writeBytes(src: ByteBuf, length: Int) = buffer.writeBytes(src, length)

  override def writeBytes(src: ByteBuf, srcIndex: Int, length: Int) = buffer.writeBytes(src, srcIndex, length)

  override def writeBytes(src: Array[Byte]) = buffer.writeBytes(src)

  override def writeBytes(src: Array[Byte], srcIndex: Int, length: Int) = buffer.writeBytes(src, srcIndex, length)

  override def writeBytes(src: ByteBuffer) = buffer.writeBytes(src)

  override def writeBytes(in: InputStream, length: Int) = buffer.writeBytes(in, length)

  override def writeBytes(in: ScatteringByteChannel, length: Int) = buffer.writeBytes(in, length)

  override def writeZero(length: Int) = buffer.writeZero(length)

  override def indexOf(fromIndex: Int, toIndex: Int, value: Byte) = buffer.indexOf(fromIndex, toIndex, value)

  override def bytesBefore(value: Byte) = buffer.bytesBefore(value)

  override def bytesBefore(length: Int, value: Byte) = buffer.bytesBefore(length, value)

  override def bytesBefore(index: Int, length: Int, value: Byte) = buffer.bytesBefore(index, length, value)

  override def forEachByte(processor: ByteBufProcessor) = buffer.forEachByte(processor)

  override def forEachByte(index: Int, length: Int, processor: ByteBufProcessor) = buffer.forEachByte(index, length, processor)

  override def forEachByteDesc(processor: ByteBufProcessor) = buffer.forEachByteDesc(processor)

  override def forEachByteDesc(index: Int, length: Int, processor: ByteBufProcessor) = buffer.forEachByteDesc(index, length, processor)

  override def copy() = buffer.copy()

  override def copy(index: Int, length: Int) = buffer.copy(index, length)

  override def slice() = buffer.slice()

  override def slice(index: Int, length: Int) = buffer.slice(index, length)

  override def duplicate() = buffer.duplicate()

  override def nioBufferCount() = buffer.nioBufferCount()

  override def nioBuffer() = buffer.nioBuffer()

  override def nioBuffer(index: Int, length: Int) = buffer.nioBuffer(index, length)

  override def internalNioBuffer(index: Int, length: Int) = buffer.internalNioBuffer(index, length)

  override def nioBuffers() = buffer.nioBuffers()

  override def nioBuffers(index: Int, length: Int) = buffer.nioBuffers(index, length)

  override def hasArray = buffer.hasArray

  override def array() = buffer.array()

  override def arrayOffset() = buffer.arrayOffset()

  override def hasMemoryAddress = buffer.hasMemoryAddress

  override def memoryAddress() = buffer.memoryAddress()

  override def toString(charset: Charset) = buffer.toString(charset)

  override def toString(index: Int, length: Int, charset: Charset) = buffer.toString(index, length, charset)

  override def hashCode() = buffer.hashCode()

  override def equals(obj: scala.Any) = buffer.equals(obj)

  override def compareTo(buffer: ByteBuf) = buffer.compareTo(buffer)

  override def toString = buffer.toString

  override def retain(increment: Int) = buffer.retain(increment)

  override def retain() = buffer.retain()

  override def refCnt() = buffer.refCnt()

  override def release() = buffer.release()

  override def release(decrement: Int) = buffer.release(decrement)
}
