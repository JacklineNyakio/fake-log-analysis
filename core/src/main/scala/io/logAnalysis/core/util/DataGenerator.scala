package io.logAnalysis.core.util

import scala.util.Random

import fabricator.Fabricator

import io.logAnalysis.core.avro.LogEvents

import org.joda.time.LocalDateTime

import spray.json._

import LogAnalysisJsonProtocol._

object DataGenerator {

  def generateLogData () : String = {
    val rand= new Random()
    val timeLocal = LocalDateTime.now().toString
    val possibleReqTypes : Seq[String]= Seq("PUT", "GET", "POST")
    val requestType = possibleReqTypes(rand.nextInt(possibleReqTypes.length))
    val requestPath  = "/" + Fabricator.words().word
    val status  = Seq(201,202,200, 401, 400, 500)
    val randStatus = status(rand.nextInt(status.length))
    val bodyBytes = rand.nextInt(10000).toString
    val urlBuilder = Fabricator.internet().urlBuilder

    val logLine: String =
      s"""{ "ipAddress" : "${Fabricator.internet().ip}",
         |"timeStamp": "$timeLocal",
         |"requestType": "$requestType",
         |"requestPath": "$requestPath",
         |"status": $randStatus ,
         |"bodyBytes" : $bodyBytes,
         |"httpReferer": "$urlBuilder",
         | "httpUserAgent": "${Fabricator.userAgent().browser}" }
         | """.stripMargin
    logLine
  }

  def parseToCaseClass (logGenerated : String) : LogEvents =
    generateLogData().parseJson.convertTo[LogEvents]
}