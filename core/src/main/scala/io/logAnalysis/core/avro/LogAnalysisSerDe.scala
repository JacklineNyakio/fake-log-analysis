package io.logAnalysis.core.avro

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream, AvroSchema}

import org.apache.avro.Schema

case class LogEvents(
  ipAddress : String,
  timeStamp : String,
  requestType : String,
  requestPath : String,
  status: Int,
  bodyBytes : Int,
  httpReferer : String,
  httpUserAgent : String
)

object LogAnalysisSerDe {
  val schema : Schema = AvroSchema[LogEvents]

  def serialize(logs : LogEvents) : Array[Byte] = {
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val output = AvroOutputStream.binary[LogEvents](byteArrayOutputStream)
    output.write(logs)
    byteArrayOutputStream.toByteArray
  }

  def deserialize(bytes : Array[Byte]) : LogEvents = {
    val byteArrayInputStream = new ByteArrayInputStream(bytes)
    val input = AvroInputStream.binary[LogEvents](byteArrayInputStream)
    val result = input.iterator.toSeq
    input.close()
    result.head
  }
}

