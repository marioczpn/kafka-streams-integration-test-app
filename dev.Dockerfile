FROM adoptopenjdk/openjdk11:alpine-slim

ARG JAR_FILE=target/kafka-streams-integration-test-app-1.0-SNAPSHOT.jar
ARG JAR_LIB_FILE=target/lib/
ARG EXTERNAL_CONFIG_FILE=target/config/config.properties

# cd /usr/local/runme
WORKDIR /usr/local/runme

# copy target/kafka-integration-test-app.jar /usr/local/runme/app.jar
COPY ${JAR_FILE} kafka-streams-integration-test-app.jar

# copy project dependencies
# cp -rf target/lib/  /usr/local/runme/lib
ADD ${JAR_LIB_FILE} lib/

# copy target/config/config.properties /usr/local/runme/config/
COPY ${EXTERNAL_CONFIG_FILE} config/config.properties

# java -jar /usr/local/runme/app.jar config/config.properties
ENTRYPOINT ["java","-jar","kafka-streams-integration-test-app.jar"]