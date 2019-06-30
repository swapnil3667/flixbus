## Flix Bus Assignment

Notes on Development:
1. At present I am reading a json file namely input.json in this case with array of records and ingesting them in kafka topic as JsonNode.
2. Next I am ingesting from the input kafka topic, transforming the timestamp to UTC timestamp and then sending transformed data to output topic.
3. Here I am dropping a record if the timestamp is null, this could also be send to some invalid records topic in kafka to keep a track of invalid records.
Commands to run:

First we need to bring up a local kafka.
I have used landoop fast data dev docker image  for this purpose
```
docker-compose up
```

We can visualize  the data coming in Kafka in the Landoop UI running at port 3030
```
http://127.0.0.1:3030/kafka-topics-ui/#/
```

Next we need to build a jar:
```
sbt clean compile assembly
```


Then we can build a docker image with this jar and input.json file attached in this repo:
```
docker build -t flix .
```

I have two classes:
1. FlixbusProducer : This class ingest the data from input.json file
    ```
    docker run --rm --privileged --network=host -e HOST_IP="192.168.1.227:9092" -e INPUT_TOPIC="doc_in" -it flix scala -classpath flixbus-assembly-0.1.jar com.flixbus.FlixbusProducer input.json
    ```
2. FlixbusConsumer: This class carries out the transform on timestamp key and drops the record if that key is empty

    ```
    docker run --rm --privileged --network=host -e HOST_IP="192.168.1.227:9092" -e INPUT_TOPIC="doc_in" -e OUTPUT_TOPIC="doc_out" -it flix scala -classpath flixbus-assembly-0.1.jar com.flixbus.FlixbusConsumer
    ```

Env Variables:
1. Host IP should be the ip address of the machine where the docker is running

