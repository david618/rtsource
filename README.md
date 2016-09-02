# rtsource

Test sources for real time DCOS/Mesos/Marathon apps.

## Installation

Created the project in NetBeans 8.1; later switched to IntelliJ.

Tested with maven 3.3.9
$ mvn install 

The target folder will contain:
- lib folder: all of the jar depdencies
- rtsource.jar: small executable jar (w/o dependencies)
- rtsource-jar-with-dependencies.jar: larget executable jar with dependencies.

## Usage

You can run them from the command line:

TcpKafka
- java -cp rtsource.jar com.esri.rtsource.TcpKafka 
- Usage: TcpKafka <port-to-listen-on> <broker-list-or-hub-name> <topic> <web-port>
- Example Command: $ java -cp rtsource.jar com.esri.rtsink.TcpKafka 5565 rth simFile 14001

TcpKafka listens on a specified port (e.g. 5565) and then writes lines receivied the the Kafka (DCOS named rth) topic named (e.g. simFile). The app counts the number of lines received and calculates rate. After input stop it outputs the count and reate.  The count and reate are also available on the web-port. (http://localhost:14001/count).

TcpNoOp - Listens on TCP socket and counts events
- java -cp rtsource.jar com.esri.rtsource.TcpNoOp
- Usage: TcpNoOp <port-to-listen-on>  <web-port>
- Example Command: $ java -cp rtsource.jar com.esri.rtsink.TcpKafka 5565 14001

TcpNoOp listens on a specified port (e.g. 5565). The app counts the number of lines received and calculates rate. After input stop it outputs the count and reate.  The count and reate are also available on the web-port. (http://localhost:14001/count).


This app can also be ran in Mesos/Marathon.  http://davidssysadminnotes.blogspot.com/2016/08/performance-testing-kafka-on-dcos.html 

Additional classes are in development to support other sources.

## License

http://www.apache.org/licenses/LICENSE-2.0 
