# Rate-Limited Notification Service

This is a Java Project (build with Java 11) to showcase a rate limiter for notifications with various rules to add thresholds to messaging to recipients.

#### Docker

This project includes scripts to create containers to run it. `build-image.bat` can be used to create the base image for the project.

The project includes a `docker-compose.yml` with all the necessary containers to run the software (Zookeeper, Kafka and the rate limiter software).

After executing the Docker Compose script, you can publish to the `notification-request` topic, here's an example of a docker run execution to use the Kafka Producer CLI:

```sh
docker run -it --network notification-test_notification-net --rm bitnami/kafka:latest kafka-console-producer.sh --bootstrap-server kafka-server:9092 --topic notification-request
```

And an example payload:
```sh
{"type": "STATUS", "userId": "user1", "payload": "Message 1"}
```



