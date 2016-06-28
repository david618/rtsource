/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esri.rtsource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 *
 * @author david
 */
public class Tcp {
    
    private Integer port;
    
    private BufferedReader in;
    private ServerSocket srvr;
    

    public Tcp(Integer port) {
        this.port = port;  
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
        Tcp tcp = new Tcp(5565);
        tcp.listen();
    }
}
