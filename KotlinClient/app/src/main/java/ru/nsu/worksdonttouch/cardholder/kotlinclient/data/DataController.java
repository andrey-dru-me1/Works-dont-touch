package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;


import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.ApiWorker;

public class DataController {

    private static DataController instance;

    private final Logger logger = Logger.getLogger(DataController.class.getName());

    private ApiWorker apiWorker = null;

    private final DataFileContainer dataFileContainer;

    private DataController(File dir) throws IOException {
        dataFileContainer = new DataFileContainer(dir);
    }

    public static synchronized void init(File dir) throws IOException{
        instance = new DataController(dir);
    }

    public static synchronized DataController getInstance() {
        return instance;
    }

    private void loginUser(UserData user) throws Exception {
        apiWorker = ApiWorker.login(user);
    }

    private void registerUser(UserData user) throws Exception {
        apiWorker = ApiWorker.register(user);
    }

//    public void createCard(Card card, DataCallBack<Card> callBack) throws IllegalStateException, Exception {
//        if (apiWorker == null)
//            throw new IllegalStateException("Not authorized");
//        apiWorker.addCard(card, (result, data) -> {
//            switch (result) {
//                case FAIL:
//
//                case NO_PERMISSION:
//                    callBack.callback(DataCallBack.DataStatus.CANCELED, null);
//                    break;
//                case AUTHORIZATION_ERROR:
//                    callBack.callback(DataCallBack.DataStatus.WRONG_USER, null);
//                    case
//            }
//        });
//    }

    public void pushUpdates() {
        //TODO: реализовать
    }

}
