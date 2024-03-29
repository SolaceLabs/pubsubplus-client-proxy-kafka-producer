# pubsubplus-client-proxy-kafka-producer

A proxy that allows a Kafka producer to publish to a PubSub+ topic without changes to the Kafka client application.

## Description

This project allows a Kafka producer application to publish topics to the PubSub+ event mesh via the proxy.
The proxy talks the Kafka wireline protocol to the Kafka producer application, and talks the Solace wireline protocol to the
Solace PubSub+ Event Mesh.

The Kafka topic can be published to the Solace PubSub+ Event Mesh unmodified, or converted to a Solace hierarchical topic by
splitting on a specified list of characters.


## Getting Started

### Dependencies

* kafka-clients
* sol-jcsmp
* slf4j-api and the sl4j binding of your choice (see http://www.slf4j.org/manual.html)


### Building

Use either Maven or Gradle to build the application
```
mvn install
java -cp target/kafkaproxy-1.0-SNAPSHOT-jar-with-dependencies.jar org.apache.kafka.solace.kafkaproxy.ProxyMain proxy-example.properties
```
 ~ OR ~
```
./gradlew assemble
cd build/distributions
unzip kafkaproxy-1.0-SNAPSHOT.zip
cd kafkaproxy-1.0-SNAPSHOT
bin/kafkaproxy proxy-example.properties
```

### Executing program

* The proxy take a mandatory properties filename on the command line
* Mandatory property file entries:
   - listeners : Comma-separated list of one or more ports to listen on for the Kafka wireline (protocol://host:port), e.g. PLAINTEXT://TheHost:9092,SSL://TheHost:9093
   - host : the PubSub+ Event broker that the proxy should connect to, e.g. host=192.168.168.100
* Other possible property file entries:
   - vpn_name : The message VPN on the Solace PubSub+ Event broker to connect to
   - advertised.listeners : Comma-separated list of ports to advertise (host:port). If specified, the number of entries 
                            in "advertised.listeners" must match the number of entries in "listeners". This is used when 
                            the external address that clients must connect to is different than the internal address
                            that the proxy is listening on. This can occur, for example, when the proxy is running in a container.
   - Kafka client transport layer security parameters, such as:
       - ssl.keystore.location=server.private
       - ssl.keystore.password=serverpw
       - ssl.enabled.protocols=TLSv1.2




## Help

Depending on the producing rate of the producer application, the producer properties may have to be tuned to provide correct flow control.
An appropriate method for this is to set buffer.memory, e.g. buffer.memory = 2000000
The default for buffer.memory is 33554432 which can lead to the producer buffering a very large number of small records, leading to records timing out.

### Limitations

* Only SASL_PLAINTEXT or SASL_SSL authentication is supported - the provided username and password from the Kafka producer is passed through to the Solace PubSub+ Event broker
* Transactions and compression are not supported

## Authors

Solace Corporation

## License

This project is licensed under the Apache License Version 2.0 - see the LICENSE.md file for details.



