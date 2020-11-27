package com.github.marioczpn.kafka.streams.controller;

import com.github.marioczpn.kafka.streams.constants.Constants;
import com.github.marioczpn.kafka.streams.helper.ConsumerCreatorHelper;
import com.github.marioczpn.kafka.streams.helper.ProducerCreatorHelper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

public class KafkaManagerController {

    private static final Logger logger = LoggerFactory.getLogger(KafkaManagerController.class.getName());

    /**
     * This method is starting a simple Kafka Producer to publish a message at Broker.
     */
    public  void startProducer() {
        logger.info("Starting Kafka Producer...");
        ProducerCreatorHelper producerCreatorHelper = new ProducerCreatorHelper();

        // create a producer
        Producer<String, String> producer = producerCreatorHelper.createProducer(System.getenv(Constants.CLIENT_ID_ENVVAR), System.getenv(Constants.BOOTSTRAP_SERVERS_ENVVAR));

        for (int i = 0; i < 10; i++) {
            // send a msg to topic and get a return
            String topicName = System.getenv(Constants.INPUT_TOPIC_NAME_ENVVAR);
            String key = "id_" + i;

            RecordMetadata recordMetadata = null;
            try {
                recordMetadata = producer.send(new ProducerRecord<String, String>(topicName, key, System.getenv(Constants.TOPIC_MSG_ENVVAR)), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
                        if (exception == null) {
                            logger.info("Record sent: \n" + "Topic:" + recordMetadata.topic() + "\n" + "Partition: " + recordMetadata.partition() + "\n" + "Offset: " + recordMetadata.offset() + "\n" + "Timestamp: " + recordMetadata.timestamp());
                        } else {
                            logger.error("Error while producing msg", exception);
                        }
                    }
                }).get();

            } catch (ExecutionException | InterruptedException e) {
                logger.error("Producer got an error: " + e);
            }
        }

        // flush data
        producer.flush();
        // flush and close producer
        producer.close();
        logger.info("Ends.");
    }

    /**
     * This method is starting a simple Kafka Consumer to get the message from Base64 format and converting
     * to normal format.
     */
    public  void startConsumer() {
        logger.info("Starting Kafka Consumer...");
        ConsumerCreatorHelper consumerCreatorHelper = new ConsumerCreatorHelper();
        Consumer<String, String> consumer = consumerCreatorHelper.createConsumer(System.getenv(Constants.BOOTSTRAP_SERVERS_ENVVAR), System.getenv(Constants.STREAMS_OUTPUT_TOPIC_NAME_ENVVAR), System.getenv(Constants.CLIENT_ID_ENVVAR));

        int noMessageToFetch = 0;

        while (true) {
            final ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
            if (consumerRecords.count() == 0) {
                noMessageToFetch++;
                if (noMessageToFetch > 100) {
                    logger.info("No message found.");
                    break;
                } else {
                    continue;
                }
            }

            consumerRecords.forEach(record -> {
                logger.info("Record Key " + record.key());
                logger.info("From Base64 format ENCODED [ " + record.value() + " ] to DECODED [ " + new String(Base64.getDecoder().decode(record.value().getBytes())) + "]");
            });

            consumer.commitAsync();
        }

        consumer.close();
        logger.info("Ends.");
    }
}
