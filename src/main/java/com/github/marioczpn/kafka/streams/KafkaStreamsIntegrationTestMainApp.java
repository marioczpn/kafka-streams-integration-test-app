package com.github.marioczpn.kafka.streams;

import com.github.marioczpn.kafka.streams.controller.KafkaManagerController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KafkaStreamsIntegrationTestMainApp {

    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamsIntegrationTestMainApp.class.getName());

    public static void main(String[] args) {
        logger.info("Starting.");

        KafkaManagerController kafkaManagerController = new KafkaManagerController();
        String action = System.getenv("KAFKA_ACTION_ENVVAR");
        if(StringUtils.isBlank(action)) {
            final String errorMsg = "Please set the KAFKA_ACTION_ENVVAR (environment variable), it is missed.";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        switch (action) {
            case "PRODUCER":
                kafkaManagerController.startProducer();
                break;
            case "CONSUMER":
                kafkaManagerController.startConsumer();
                break;
            default:
                logger.info("Sorry, This KAFKA_ACTION doesn't exists. Check the Environment variable (KAFKA_ACTION_ENVVAR). ");
                break;
        }

        logger.info("Ends.");
    }

}
