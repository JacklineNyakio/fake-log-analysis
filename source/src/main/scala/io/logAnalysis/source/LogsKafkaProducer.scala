package io.logAnalysis.source

import com.typesafe.scalalogging.LazyLogging

import java.util.Properties

import io.confluent.kafka.serializers.KafkaAvroSerializer

import org.apache.avro.generic.GenericRecord

import org.apache.kafka._
import clients.producer.{ KafkaProducer, ProducerConfig, ProducerRecord, Callback, RecordMetadata }

object LogsKafkaProducer extends LazyLogging {
  val props = new Properties()

  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put("schema.registry.url", "http://localhost:8081")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer])
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer])

  val topic = "web_logs"

  def publishToKafka(key : String, value: GenericRecord)  : Unit ={
    val kafkaProducer = new KafkaProducer[String, GenericRecord](props)

    val producerRecord = new ProducerRecord[String, GenericRecord](
      topic,
      key,
      value
    )

    val callback = new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        if (exception == null)
          logger.info(s"$value successfully written to Kafka.")
        else
          logger.info(s"Error while writing value : $value to Kafka.")
      }
    }
    kafkaProducer.send(producerRecord, callback)
  }

}

