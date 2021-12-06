# pubsubplus-client-proxy-kafka-producer

A proxy that allows a Kafka producer to publish to a PubSub+ topic without changes to the Kafka client application.

## Description

This project allows a Kafka producer application to publish topics to the PubSub+ event mesh via the proxy.
The proxy talks the Kafka wireline protocol to the Kafka producer application, and talks the Soalce wireline protocol to the
Solace PubSub+ Event Mesh.

The Kafka topic is published to the Solace PubSub+ Event Mesh unmodified.

## Getting Started

### Dependencies

* kafka-clients
* solace-messaging-client
* slf4j-api and the sl4j binding of your choice (see http://www.slf4j.org/manual.html)


### Executing program

* The proxy take a mandatory properties filename on the command line
* Mandatory property file entries:
   - listeners : comma-separated list of one or more ports to listen on for the Kafka wireline (protocol://host:port), e.g. PLAINTEXT://TheHost:9092,SSL://TheHost:9093
   - host : the PubSub+ Event broker that the proxy should connect to, e.g. host=192.168.168.100
* Other possible property file entries:
   - vpn_name : The message VPN on the Solace PubSub+ Event broker to connect to
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



