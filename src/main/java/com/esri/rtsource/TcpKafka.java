/*

Connects to Kafka then listens on port for clients.  

Start TcpServerKafka for each connection. 

*/
package com.esri.rtsource;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;


/**
 *
 * @author david
 */
public class TcpKafka {

    public TcpKafka(Integer port, String brokers, String topic, Integer webport, boolean calcLatency) {
    
        try {
            Properties props = new Properties();
            props.put("bootstrap.servers",brokers);
            props.put("client.id", TcpKafka.class.getName());
            props.put("acks", "1");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 8192000);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            /* Addin Simple Partioner didn't help */
            //props.put("partitioner.class", SimplePartitioner.class.getCanonicalName());
            
            Producer<String, String> producer = new KafkaProducer<>(props);
            
            WebServer server = new WebServer(webport);
            

            ServerSocket ss = new ServerSocket(port);
            
            while (true) {
                Socket cs = ss.accept();
                TcpServerKafka ts = new TcpServerKafka(cs, producer, topic, server, calcLatency);
                ts.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public static void main(String args[]) throws Exception {
        
        // Command Line 5565 d1.trinity.dev:9092 simFile 9000

        /*
        NOTE: For latency calculations ensure all servers including the server running simulation
        are using time chrnonized.

        Run this command simulatneously on machines to compare time
        $ date +%s

        NTP command force update now:  $ sudo ntpdate -s time.nist.gov
        CHRONYD check status: $ chronyc tracking

         */

        int numargs = args.length;

        if (numargs != 4 && numargs != 5) {
            System.err.print("Usage: TcpKafka <port-to-listen-on> <broker-list-or-hub-name> <topic> <web-port> (<calc-latency>)\n");
        } else {
            
            String brokers = args[1];            
            String brokersSplit[] = brokers.split(":");
            
            if (brokersSplit.length == 1) {
                // Try hub name. Name cannot have a ':' and brokers must have it.
                brokers = new MarathonInfo().getBrokers(brokers);
            }   // Otherwise assume it's brokers          
            
            if (numargs == 4) {
                TcpKafka t = new TcpKafka(Integer.parseInt(args[0]), brokers, args[2], Integer.parseInt(args[3]), false);
            } else {
                TcpKafka t = new TcpKafka(Integer.parseInt(args[0]), brokers, args[2], Integer.parseInt(args[3]), Boolean.parseBoolean(args[4]));
            }

        }
                
       
    }
}
