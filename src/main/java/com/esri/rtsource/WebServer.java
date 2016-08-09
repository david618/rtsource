/*
This is the web server for supporting health checks.
 */
package com.esri.rtsource;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

/**
 *
 * @author david
 */
public class WebServer {

    private final int port;
    
    RootHandler rootHandler;
    
    public void setCnt(long cnt) {
        rootHandler.addCnt(cnt);
    }
    
    public void setRate(double rate) {
        rootHandler.addRate(rate);
    }    
    
    public void setTm(long tm) {
        rootHandler.setTm(tm);
    }    

    public WebServer(int port) {

        this.port = port;

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new RootHandler());

            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
