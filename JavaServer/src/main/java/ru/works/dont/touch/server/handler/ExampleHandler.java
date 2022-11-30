package ru.works.dont.touch.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExampleHandler implements HttpHandler {

    Logger logger = Logger.getLogger(ExampleHandler.class.getName());

    public void handle(HttpExchange exchange) throws IOException {
        try {
            logger.log(Level.INFO, exchange.getProtocol());
            logger.log(Level.INFO, exchange.getRequestMethod());
            logger.log(Level.INFO, exchange.getRemoteAddress().getHostName());
            for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
                StringBuilder builder = new StringBuilder();
                builder.append(entry.getKey()).append(": ");
                for (String value : entry.getValue()) {
                    builder.append(value).append(" ");
                }
                logger.log(Level.INFO, builder.toString());
            }
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String text = "ansewr";
            if (exchange.getRequestHeaders().containsKey("text")) {
                text = exchange.getRequestHeaders().getFirst("text");
            }
            if (params.containsKey("text")) {
                text = params.get("text");
            }
            byte[] data = ("{text:\"" + text + "\"}").getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, data.length);
            try (OutputStream stream = exchange.getResponseBody()) {
                stream.write(data);
            }
            exchange.close();
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
            logger.log(Level.WARNING, "request error", e);
        }
    }

    private Map<String, String> queryToMap(String query) {
        if(query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }

}
