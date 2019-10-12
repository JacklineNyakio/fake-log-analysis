package io.logAnalysis.compute

import scala.concurrent.duration._
import scala.io.StdIn

import io.logAnalysis.core.avro._

import LogAnalysisSerDe.deserialize

import org.apache.spark._

import sql.{ DataFrame, SparkSession, SaveMode }
import sql.cassandra._
import sql.functions._
import sql.types._
import sql.streaming._

import com.datastax.spark._

import connector.cql.{ CassandraConnectorConf, DefaultAuthConfFactory }
import connector.rdd.ReadConf.SplitSizeInMBParam
import CassandraConnectorConf._
import DefaultAuthConfFactory._

object LogComputation {
  val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("logs-spark-jobs")
    .getOrCreate()

  val cassyOptions = {
    ConnectionHostParam.option("127.0.0.1") ++
      ConnectionPortParam.option("9042") ++
      UserNameParam.option(Some("cassandra")) ++
      PasswordParam.option(Some("cassandra")) ++
      SplitSizeInMBParam.option(16)
  }

  spark.setCassandraConf(
    cluster = "Test Cluster",
    keyspace = "analytics",
    options = cassyOptions
  )

  def main(args: Array[String]): Unit = {
    val logSource : DataFrame = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "web_logs")
      .option("startingOffsets", "earliest")
      .load()

    import spark.implicits._
    import LogsGenericRecordsConvertor._

    val logEventsDf : DataFrame= logSource
      .select("value")
      .map(row => genericRecordToLog(deserialize(row(0).asInstanceOf[Array[Byte]])))
      .map(event => (
        event.ipAddress,
        event.timeStamp,
        event.requestType,
        event.requestPath,
        event.status,
        event.bodyBytes,
        event.httpReferer,
        event.httpUserAgent
      ))
      .toDF("ip_address", "timestamp", "request_type", "request_path", "status", "body_bytes", "http_referer", "http_user_agent")
        .select($"ip_address", $"timestamp".cast(TimestampType), $"request_type", $"request_path", $"status".cast(IntegerType), $"body_bytes".cast(IntegerType), $"http_referer", $"http_user_agent")

    val query = logEventsDf
      .writeStream
      .format("console")
      .start()

    query.awaitTermination()

//    val resultStream= logEventsDf
//      .withWatermark("timestamp", "10 minutes")
//      .groupBy(window(col("timestamp"), "30 minutes", "15 minutes"), col("ip_address"), col("status"))
//      .agg(avg("body_bytes") alias "average_body_bytes")
//
//  resultStream.printSchema()

//    val streamingQuery = resultStream
//        .writeStream
//        .queryName("log-analysis")
//        .outputMode("append")
//        .trigger(Trigger.ProcessingTime(3.minutes))
//        .foreachBatch{
//          (batch : DataFrame, batchId : Long) =>
//            batch
//              .select(
//                col("window.start") alias("start_time"),
//                col("")
//              )
//              .orderBy(asc("sensor_uuid"))
//              .write
//              .mode(SaveMode.Append)
//              .cassandraFormat(
//                table = "sensor_averages",
//                keyspace = "analytics"
//              )
//              .save()
//        }
//        .start()
//
//    streamingQuery.awaitTermination()
//
//    StdIn.readLine()
//    streamingQuery.stop()

  }
}
