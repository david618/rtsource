/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esri.rtsource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 *
 * @author david
 */
public class TcpToKafka {
    
    // TCP port to open and listen on
    private Integer port;
    
    // Broker Server(s)  192.168.56.61:9092,192.168.56.61:9093
    private String brokers;
    
    // Topic 
    private String topic;
    
    // Web Port
    private Integer webport;
    
    private BufferedReader in;
    private ServerSocket srvr;
    private Producer<String, String> producer;
    
    
    

    public TcpToKafka(Integer port, String brokers, String topic, Integer webport) {
        this.port = port;  
        this.topic = topic;
        this.webport = webport;
    
        try {
            Properties props = new Properties();
            props.put("bootstrap.servers",brokers);
            props.put("client.id", TcpToKafka.class.getName());
            props.put("acks", "1");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 8192000);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            
            this.producer = new KafkaProducer<>(props);
            
            WebServer server = new WebServer(webport);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        openSocket();
    }
    
    
    
    private void openSocket() {
        try {
            
            // Setup Socket to listen to
            this.srvr = new ServerSocket(port);
            Socket skt = this.srvr.accept();            
            this.in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            
           
            
        } catch (Exception e) {
           e.printStackTrace();
        }        
        
    }
    
    
    
    public void listen() {
        try {
            String inputLine = "";
            
            // Read lines from Socket forever
            
            LocalDateTime st = LocalDateTime.now();
            
            while (true) {
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
                
                // After a client stops sending in.readline returns null
                // Reset the ServerSocket and start listening again               
                this.srvr.close();
                openSocket();                               
            }
                        

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    public static void main(String args[]) {
        
        if (args.length != 4) {
            System.err.print("Usage: rtsource <port-to-listen-on> <broker-list> <topic> <web-port>\n");
        } else {
            TcpToKafka t = new TcpToKafka(Integer.parseInt(args[0]), args[1], args[2], Integer.parseInt(args[3]));
            t.listen();
        }
                
        
//        TcpToKafka tcp = new TcpToKafka(5565, "d1.trinity.dev:9092", "faa-stream");
//        tcp.listen();
    }
}
