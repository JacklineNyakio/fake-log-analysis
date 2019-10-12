package io.logAnalysis.core.avro

import com.sksamuel.avro4s.RecordFormat

import org.apache.avro.generic.GenericRecord

object LogsGenericRecordsConvertor {
  private val logsRecordFormat = RecordFormat[LogEvents]
  implicit def logToGenericRecord(event: LogEvents) : GenericRecord = logsRecordFormat.to(event)
  implicit def genericRecordToLog(record: GenericRecord) : LogEvents = logsRecordFormat.from(record)
}