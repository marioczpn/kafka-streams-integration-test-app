package com.github.marioczpn.kafka.streams;

import com.github.marioczpn.kafka.streams.controller.KafkaManagerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KafkaStreamsIntegrationTestMainApp {

    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamsIntegrationTestMainApp.class.getName());

    public static void main(String[] args) {
        logger.info("Starting.");

        KafkaManagerController kafkaManagerController = new KafkaManagerController();
        String action = System.getenv("KAFKA_ACTION_ENVVAR");
        switch (action) {
            case "PRODUCER":
                kafkaManagerController.startProducer();
                break;
            case "CONSUMER":
                kafkaManagerController.startConsumer();
                break;
            default:
                logger.info("Sorry, KAFKA_ACTION doesn't exists.");
                break;
        }

        logger.info("Ends.");
    }

}
