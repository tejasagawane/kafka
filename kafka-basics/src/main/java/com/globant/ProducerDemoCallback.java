package com.globant;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoCallback {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoCallback.class.getName());
    public static void main(String[] args) {
        log.info("Inside Producer Callback Demo...");

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for(int i= 0; i < 10 ; i++) {


            // create a producer record
            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>("demo_topic1", "hello world" + i);

            // send the data - asynchronous
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    if (e == null) {
                        log.info("Recived metadata /" +
                                " Topic : " + metadata.topic() +
                                " Partition : " + metadata.partition() +
                                " Offset : " + metadata.offset() +
                                " Timestamp : " + metadata.timestamp()
                        );
                    } else {
                        log.error("Exception occurred " + e.getMessage());
                    }
                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        // flush data - synchronous
        producer.flush();

        // flush and close producer
        producer.close();



    }
}
