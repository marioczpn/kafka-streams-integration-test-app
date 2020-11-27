package com.github.marioczpn.kafka.streams.helper;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ConsumerCreatorHelper {
    private static Logger logger = LoggerFactory.getLogger(ConsumerCreatorHelper.class.getName());

    public Consumer<String, String> createConsumer(final String bootStrapServer, final String topicName, final String groupId){

        // create consumer configs
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);

        // create consumer
        Consumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // subscribe consumer to our topic(s)
        consumer.subscribe(Collections.singletonList(topicName));
        return consumer;
    }

    public ConsumerRecord<String, String> getLastRecord(Consumer<String, String> consumer ) {
        consumer.poll(Duration.ofSeconds(10));
        consumer.assignment().forEach(record -> {
            logger.info("Topic: " + record.topic() + "Record data: " + record.toString());
        });

        AtomicLong maxTimestamp = new AtomicLong();
        AtomicReference<ConsumerRecord<String, String>> latestRecord = new AtomicReference<>();

        // get the last offsets for each partition
        consumer.endOffsets(consumer.assignment()).forEach((topicPartition, offset) -> {
            logger.info("offset: " + offset);

            // seek to the last offset of each partition
            consumer.seek(topicPartition, (offset == 0) ? offset : offset - 1);

            // poll to get the last record in each partition
            consumer.poll(Duration.ofSeconds(10)).forEach(record -> {

                // the latest record in the 'topic' is the one with the highest timestamp
                if (record.timestamp() > maxTimestamp.get()) {
                    maxTimestamp.set(record.timestamp());
                    latestRecord.set(record);
                }
            });
        });

        return latestRecord.get();
    }
}
