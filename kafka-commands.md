
# Utility

## Java Home
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.4.jdk/Contents/Home

## Kill process
kill -9 $(lsof -t -i:18082)


# Kafka

## create topic
docker exec -ti docker_kafka_1  /usr/bin/kafka-topics --create --topic puc-topic  --partitions 2 --replication-factor 1 --zookeeper zookeeper:2181

## topic details

docker exec -ti docker_kafka_1  /usr/bin/kafka-topics --describe --topic pharm-alert-topic --zookeeper zookeeper:2181

## delete topic

docker exec -ti docker_kafka_1  /usr/bin/kafka-topics --delete --topic puc-topic  --zookeeper zookeeper:2181


## list topics
docker exec -ti docker_kafka_1 /usr/bin/kafka-topics --list --zookeeper zookeeper:2181

## produce event
docker exec -ti docker_kafka_1 /usr/bin/kafka-console-producer --topic pharm-alert-topic --bootstrap-server localhost:9092

## consume event
docker exec -ti docker_kafka_1 /usr/bin/kafka-console-consumer --topic pharm-alert-topic --from-beginning --bootstrap-server localhost:9092


N.B per cancellare topic bisogna settare proporty delete.topic.enable=true in config/server.properties 


Riferimenti: https://www.oak-tree.tech/blog/kafka-admin-remove-messages