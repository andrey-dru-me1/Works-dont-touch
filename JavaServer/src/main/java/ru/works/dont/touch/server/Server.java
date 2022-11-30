package ru.works.dont.touch.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import ru.works.dont.touch.server.handler.ExampleHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private final HttpServer server;
    Logger logger = Logger.getLogger(Server.class.getName());

    public Server(InetSocketAddress address) throws IOException {
        HttpServer server = HttpServer.create(address, 0);
        //TODO: Нужно подключить сертификат. Сам не знаю как адекватно его создавать.
        /*
        HttpsServer server = HttpsServer.create(address, 0);
        SSLContext context = SSLContext.getDefault();
        HttpsConfigurator configurator = new HttpsConfigurator(context);
        server.setHttpsConfigurator(configurator);*/
        this.server = server;
        addHandlers();
        server.setExecutor(null);
        server.start();
        logger.log(Level.INFO, "Start server");
    }

    private void addHandlers() {
        server.createContext("/", new ExampleHandler());
    }

    public HttpServer getHttpServer() {
        return server;
    }

    public void stop() {
        server.stop(2);
    }

}
