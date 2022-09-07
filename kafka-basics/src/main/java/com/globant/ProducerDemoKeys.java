package com.globant;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoKeys {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoKeys.class.getName());
    public static void main(String[] args) {
        log.info("Inside Producer Callback Demo...");

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for(int i= 0; i < 10 ; i++) {
            String key = "id "+i;
            String topic = "demo_topic";
            String value = "Hello World" + i;
            // create a producer record
            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>(topic,key,value);

            // send the data - asynchronous
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    if (e == null) {
                        log.info("Recived metadata /" +
                                " Topic : " + metadata.topic() +
                                " Partition : " + metadata.partition() +
                                " Keys : " + producerRecord.key() +
                                " Offset : " + metadata.offset() +
                                " Timestamp : " + metadata.timestamp()
                        );
                    } else {
                        log.error("Exception occurred " + e.getMessage());
                    }
                }
            });


        }
        // flush data - synchronous
        producer.flush();

        // flush and close producer
        producer.close();



    }
}
