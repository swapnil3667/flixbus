docker run --privileged  --network=host -it flix bash
docker run --rm --privileged --network=host -e HOST_IP="192.168.1.227:9092" -e INPUT_TOPIC="doc_in" -e OUTPUT_TOPIC="doc_out" -it flix scala -classpath flixbus-assembly-0.1.jar com.flixbus.FlixbusConsumer

scala -classpath flixbus-assembly-0.1.jar com.flixbus.FlixbusConsumer

docker run --rm --privileged --network=host -e HOST_IP="192.168.1.227:9092" -e INPUT_TOPIC="doc_in" -it flix scala -classpath flixbus-assembly-0.1.jar com.flixbus.FlixbusProducer input.json
