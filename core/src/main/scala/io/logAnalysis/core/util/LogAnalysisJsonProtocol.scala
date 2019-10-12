package io.logAnalysis.core.util

import spray.json._
import DefaultJsonProtocol._

import io.logAnalysis.core.avro._

object LogAnalysisJsonProtocol extends DefaultJsonProtocol {

  implicit val logDataFormat = jsonFormat8(LogEvents)

}