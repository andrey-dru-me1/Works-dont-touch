package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;


import java.util.logging.Logger;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.ApiWorker;

public class DataController {

    private static DataController instance;

    private final Logger logger = Logger.getLogger(DataController.class.getName());

    private ApiWorker apiWorker = null;

    public static synchronized void init() {
        if(instance == null) {

        }
    }

    public static synchronized DataController getInstance() {
        return instance;
    }

    private void setUser(UserData user) {

    }

}
