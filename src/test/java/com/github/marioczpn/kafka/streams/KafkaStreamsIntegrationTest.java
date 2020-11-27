package com.github.marioczpn.kafka.streams;

import com.github.marioczpn.kafka.streams.constants.Constants;
import com.github.marioczpn.kafka.streams.helper.ConsumerCreatorHelper;
import com.github.marioczpn.kafka.streams.helper.FileHelper;
import com.github.marioczpn.kafka.streams.helper.ProducerCreatorHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaStreamsIntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(KafkaStreamsIntegrationTest.class.getName());
    private String clientId;
    private String bootStrapServers;
    private String inputTopicName;
    private String topicMsg;
    private String streamsOutputTopicName;

    @BeforeEach
    void setUp() throws IOException {
        FileHelper fileHelper = new FileHelper();
        final Properties envProps = fileHelper.loadEnvProperties("config/config.properties");

        clientId = envProps.getProperty(Constants.CLIENT_ID);
        bootStrapServers = envProps.getProperty(Constants.BOOTSTRAP_SERVERS);
        inputTopicName = envProps.getProperty(Constants.INPUT_TOPIC_NAME);
        topicMsg = envProps.getProperty(Constants.TOPIC_MSG);
        streamsOutputTopicName = envProps.getProperty(Constants.STREAMS_OUTPUT_TOPIC_NAME);
    }

    /**
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    void testProduceCommonMessageToKafkaBroker() throws ExecutionException, InterruptedException {
        logger.info("Executing .testProduceCommonMessageToKafkaBroker() - Producing a msg to Kafka Broker");

        ProducerCreatorHelper producerCreatorHelper = new ProducerCreatorHelper();

        // create a producer
        Producer<String, String> producer = producerCreatorHelper.createProducer(clientId, bootStrapServers);

        // send a msg to topic and get a return
        RecordMetadata recordMetadata = producer.send(new ProducerRecord<String, String>(inputTopicName, topicMsg)).get();
        logger.info("Record sent: \n" + "Topic:" + recordMetadata.topic() + "\n" + "Partition: " + recordMetadata.partition() + "\n" + "Offset: " + recordMetadata.offset() + "\n" + "Timestamp: " + recordMetadata.timestamp());

        producer.flush();
        producer.close();

        Assertions.assertEquals(inputTopicName, recordMetadata.topic());
        Assertions.assertNotNull(recordMetadata.partition());
        Assertions.assertNotNull(recordMetadata.offset());
    }

    @Test
    void testKafkaConsumeMsgInBase64Format() {
        logger.info("Executing .testKafkaConsumeMsgInBase64Format() - Consuming an *encoded* message from Kafka Broker");
        ConsumerCreatorHelper consumerCreatorHelper = new ConsumerCreatorHelper();
        Consumer<String, String> consumer = consumerCreatorHelper.createConsumer(bootStrapServers, streamsOutputTopicName, clientId);
        ConsumerRecord<String, String> lastRecord = consumerCreatorHelper.getLastRecord(consumer);

        // check if the data is in base64format
        Assertions.assertTrue(Base64.isBase64(lastRecord.value()));

    }
}
