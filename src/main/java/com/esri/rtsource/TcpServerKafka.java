/*

TcpServerKafka is the listener for TcpToKafka.  Instance of this class starts when client connects.

Listens on TCP port for messages and writes them to Kafka.

*/
package com.esri.rtsource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 *
 * @author david
 */
public class TcpServerKafka extends Thread {
    private Socket socket = null;
    private Producer<String, String> producer;
    private String topic;
    
    public TcpServerKafka(Socket socket, Producer<String, String> producer, String topic) {
        this.socket = socket;
        this.producer = producer;
        this.topic = topic;
    }
    
    @Override
    public void run() {
        try {
            
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            
            String inputLine = "";
            
            // Read lines from Socket forever
            
            LocalDateTime st = LocalDateTime.now();
            

            Integer cnt = 0;
            while ((inputLine = in.readLine()) != null) {
                //System.out.println(inputLine);

                UUID uuid = UUID.randomUUID();

                try {
                    producer.send(new ProducerRecord<String,String>(this.topic, uuid.toString(),inputLine));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                cnt += 1;
                if (cnt == 1) st = LocalDateTime.now();
            }

            Double rcvRate = 0.0;

            if (st != null ) {
                LocalDateTime et = LocalDateTime.now();

                Duration delta = Duration.between(st, et);

                Double elapsedSeconds = (double) delta.getSeconds() + delta.getNano() / 1000000000.0;

                rcvRate = (double) cnt / elapsedSeconds;                
            }                

            System.out.println(cnt + "," + rcvRate);

            this.socket.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }        

    }
    
}
