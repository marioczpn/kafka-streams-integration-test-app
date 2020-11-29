![CI/CD Pipelines](https://github.com/marioczpn/kafka-streams-integration-test-app/workflows/CI/CD%20Pipelines/badge.svg)

# kafka-streams-integration-test-app

This application verifies the (Kafka Streams Convert Base64 Application)[https://github.com/marioczpn/kafka-streams-convert-base64-app/] is working properly.
It is attaching to producer to topic X and consumer to topic Y to verify if the message is converting to base64 format. 


##Requirements
- Java 11
- Junit 5
- Maven:
    - It is a maven project and all dependencies are available in [pom.xml](https://github.com/marioczpn/kafka-streams-integration-test-app/blob/master/pom.xml)
 
* This project has being developed using the IntelliJ IDEA Community

# How to build this application?

This application has a Makefile available, you can build the application using:

- `make all` -> It will generate the jar file, docker image and push to your repository.


- You can define your repository into Makefile variables: 

Example: `DOCKER_REPO=quay.io/marioczpn`

If you only want to generate a:

- jar file: `make java_build`
- Docker Image: `make build` (Behind the scenes it's using the [dev.Dockerfile](https://github.com/marioczpn/kafka-streams-integration-test-app/blob/master/dev.Dockerfile)

For more information please open the [Makefile](https://github.com/marioczpn/kafka-streams-integration-test-app/blob/master/Makefile)

## CI/CD Pipelines

The application is using the github actions and it's using a different [Dockerfile](https://github.com/marioczpn/kafka-streams-integration-test-app/blob/master/Dockerfile) to build/push.

# How to run this application?

To run the application locally you can use the jar generated into target folder:

- Set Environment variables:
    - **ATTENTION**: The KAFKA_ACTION_ENVVAR (Env variable) defines the type of application, for example:
        - If you set **_KAFKA_ACTION_ENVVAR=PRODUCER_** - It will produce message to kafka, but if you define **KAFKA_ACTION_ENVVAR=CONSUMER** it will run as KAFKA Consumer.


    export BOOTSTRAP_SERVERS_ENVVAR=127.0.0.1:9092
    export APPLICATION_ID_ENVVAR=integration-test-app
    export CLIENT_ID_ENVVAR=integration-test-client
    export INPUT_TOPIC_NAME_ENVVAR=input-topic
    export STREAMS_OUTPUT_TOPIC_NAME_ENVVAR=streams-output-topic
    export KAFKA_ACTION_ENVVAR=PRODUCER
    export TOPIC_MSG_ENVVAR=integrationTest

- If you set the environment variables you can run:

`java -jar kafka-streams-integration-test-app-1.0-SNAPSHOT.jar`


## Deploy to Kubernetes
Please open the deployment automation to this project available [here](https://github.com/marioczpn/strimzi-kafka-cluster-deployment-automation)
 
