package com.plovdev.pornviewer.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class SafeHttpServer {
    private final Logger logger = LoggerFactory.getLogger(SafeHttpServer.class);

    public static final int PORT = 3535;
    private boolean isRunning;
    private HttpServer server;
    private volatile static SafeHttpServer INSTANSE = null;

    public static SafeHttpServer getInstance() {
        if (INSTANSE == null) {
            synchronized (SafeHttpServer.class) {
                if (INSTANSE == null) {
                    INSTANSE = new SafeHttpServer();
                }
            }
        }
        return INSTANSE;
    }

    private SafeHttpServer() {}

    public void restartServer() {
        stopServer();
        startServer();
    }

    public void startServer() {
        if (isRunning) {
            logger.info("Server already running");
            return;
        }

        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/video", new SafeHttpHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            isRunning = true;
            logger.info("Server started on port {}", PORT);
        } catch (IOException e) {
            logger.error("Server start error: ", e);
        }
    }

    public void stopServer() {
        if (server != null) {
            server.stop(0);
            isRunning = false;
            logger.info("Server stopped");
        }
    }

    private void onMessage(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        logger.info("Getted body: {}", body);
    }

    public boolean isRunning() {
        return isRunning;
    }
}