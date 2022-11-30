package ru.works.dont.touch.server;

import java.io.Console;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    static Server server;

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());
        try {
            InetSocketAddress address = new InetSocketAddress(80);
            server = new Server(address);
            commandLoop();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can't run server", e);
        }
    }

    private static void commandLoop() {
        Console c = System.console();
        boolean working = true;
        while(working) {
            String line = c.readLine();
            if("stop".equalsIgnoreCase(line)) {
                working = false;
                server.stop();
            }
        }
    }

}
