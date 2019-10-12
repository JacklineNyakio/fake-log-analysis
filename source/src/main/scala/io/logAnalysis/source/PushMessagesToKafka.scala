package io.logAnalysis.source

import io.logAnalysis._

import core.avro.LogsGenericRecordsConvertor
import core.util.DataGenerator._

object PushMessagesToKafka extends App {
while (true)
  LogsKafkaProducer.publishToKafka(
    LogsKafkaProducer.topic,
    LogsGenericRecordsConvertor.logToGenericRecord(parseToCaseClass(generateLogData()))
  )
}
