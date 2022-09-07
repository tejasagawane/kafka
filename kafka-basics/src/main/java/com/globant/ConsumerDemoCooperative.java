package com.globant;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Callable;

public class ConsumerDemoCooperative {

    private static final Logger log = LoggerFactory.getLogger(ConsumerDemoCooperative.class.getName());
    public static void main(String[] args) {
        log.info("*************** CONSUMER DEMO **************");
        String bootStrapServer = "127.0.0.1:9092";
        String groupId = "demo_topic_group_1";
        String topic = "demo_topic";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
        // create the Consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        final Thread finalThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Detected a shutdown");
                consumer.wakeup();
                try {
                    finalThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            //subscribe the topic
            consumer.subscribe(Collections.singletonList(topic));
            while (true) {
                ConsumerRecords<String, String> consumerRecord =
                        consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : consumerRecord) {
                    System.out.println("Reading ..." +
                            "key : " + record.key() + " , value : " + record.value() + "\n" +
                            "topic : " + record.topic() + ", offset : " + record.offset());
                }
            }
        }catch (WakeupException we) {
            log.info("Wakeup Exception");
        } catch (Exception e) {
            log.error("Unexpected behaviour");
        } finally {
            consumer.close();
        }

    }
}
