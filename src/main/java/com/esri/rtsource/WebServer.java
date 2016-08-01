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
