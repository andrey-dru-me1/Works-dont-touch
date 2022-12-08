package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;


import org.jetbrains.annotations.NotNull;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.Event;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.EventHandler;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.EventListener;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.ListenerEventRunner;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event.CardAddEvent;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event.CardChangeEvent;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event.LogOutEvent;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.ApiWorker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataController {

    private static DataController instance;

    private final static Logger logger = Logger.getLogger(DataController.class.getName());

    private ApiWorker apiWorker = null;

    private boolean isOffline = false;

    private final DataFileContainer dataFileContainer;

    private final static Map<Class<? extends Event>, List<ListenerEventRunner>> listenerMap = new ConcurrentHashMap<>();

    private DataController(File dir) throws IOException {
        dataFileContainer = new DataFileContainer(dir);
    }

    public static synchronized void init(File dir) throws IOException {
        instance = new DataController(dir);
    }

    public static synchronized DataController getInstance() {
        return instance;
    }

    private void loginUser(UserData user) throws Exception {
        try {
            apiWorker = ApiWorker.authTest(user);
            if (apiWorker != null) {
                dataFileContainer.setUserData(user);
                isOffline = false;
            } else {
                runEvent(new LogOutEvent(user));
            }
        } catch (Exception e) {
            apiWorker = null;
            runEvent(new LogOutEvent(user));
        }
    }

    private void registerUser(UserData user) throws Exception {
        try {
            apiWorker = ApiWorker.registration(user);
            if (apiWorker != null) {
                dataFileContainer.setUserData(user);
                isOffline = false;
            } else {
                runEvent(new LogOutEvent(user));
            }
        } catch (Exception e) {
            apiWorker = null;
            runEvent(new LogOutEvent(user));
        }
    }

    private void startOffline() {
        isOffline = true;
        apiWorker = null;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void createCard(String name, String barcode, DataCallBack<Card> callBack) {
        Card card = new Card(name, barcode, null, null);
        if (apiWorker != null) {
            try {
                apiWorker.addCard(card, (result, data) -> {
                    switch (result) {
                        case FAIL:
                            try {
                                Card finalCard = dataFileContainer.save(card);
                                runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, finalCard);
                                runEvent(new CardAddEvent(finalCard));
                            } catch (Exception e) {
                                logger.log(Level.WARNING, "card save error", e);
                                runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
                            }
                            break;
                        case NO_PERMISSION:
                        case NOT_FOUND:
                            runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
                            break;
                        case AUTHORIZATION_ERROR:
                            runCallback(callBack, DataCallBack.DataStatus.WRONG_USER, null);
                            runEvent(new LogOutEvent(apiWorker.getUserData()));
                            break;
                        case SUCCESSFUL:
                            try {
                                Card finalCard = dataFileContainer.save(data);
                                runCallback(callBack, DataCallBack.DataStatus.OK, finalCard);
                                runEvent(new CardAddEvent(finalCard));
                            } catch (Exception e) {
                                logger.log(Level.WARNING, "card save error", e);
                                runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
                            }
                            pushUpdates();
                            break;
                    }
                });
            } catch (Exception e) {
                logger.log(Level.WARNING, "request error", e);
                runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
            }
        } else {
            if (isOffline) {
                try {
                    Card finalCard = dataFileContainer.save(card);
                    runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, finalCard);
                    runEvent(new CardAddEvent(finalCard));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "card save error", e);
                    runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, null);
                }
            } else {
                runCallback(callBack, DataCallBack.DataStatus.WRONG_USER, null);
                runEvent(new LogOutEvent(apiWorker.getUserData()));
            }
        }
    }

    public void editCard(Card card, DataCallBack<Card> callBack) {
        if (apiWorker != null) {
            try {
                apiWorker.editCard(card, (result, data) -> {
                    switch (result) {
                        case FAIL:
                            try {
                                Card finalCard = dataFileContainer.save(card);
                                runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, finalCard);
                                runEvent(new CardChangeEvent(finalCard));
                            } catch (Exception e) {
                                logger.log(Level.WARNING, "card edit error", e);
                                runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
                            }
                            break;
                        case NO_PERMISSION:
                        case NOT_FOUND:
                            try {
                                dataFileContainer.deleteCard(card);
                            } catch (IOException e) {
                                logger.log(Level.WARNING, "card remove", e);
                            }
                            runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
                            break;
                        case AUTHORIZATION_ERROR:
                            runCallback(callBack, DataCallBack.DataStatus.WRONG_USER, null);
                            runEvent(new LogOutEvent(apiWorker.getUserData()));
                        case SUCCESSFUL:
                            try {
                                Card finalCard = dataFileContainer.save(data);
                                runCallback(callBack, DataCallBack.DataStatus.OK, finalCard);
                                runEvent(new CardChangeEvent(finalCard));
                            } catch (Exception e) {
                                logger.log(Level.WARNING, "card save error", e);
                                runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
                            }
                            pushUpdates();
                            break;
                        case NO_CONNECTION:
                            isOffline = true;
                            try {
                                Card finalCard = dataFileContainer.save(card);
                                runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, finalCard);
                                runEvent(new CardChangeEvent(finalCard));
                            } catch (Exception e) {
                                logger.log(Level.WARNING, "card edit error", e);
                                runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
                            }
                            break;
                    }
                });
            } catch (Exception e) {
                logger.log(Level.WARNING, "request error", e);
                runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
            }
        } else {
            if (isOffline) {
                try {
                    Card finalCard = dataFileContainer.save(card);
                    runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, finalCard);
                    runEvent(new CardChangeEvent(finalCard));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "card save error", e);
                    runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, null);
                }
            } else {
                runCallback(callBack, DataCallBack.DataStatus.WRONG_USER, null);
                runEvent(new LogOutEvent(apiWorker.getUserData()));
            }
        }
    }

    public void deleteCard(Card card, DataCallBack<Card> callBack) {
    }

    public void getImage(Card card, long id, DataCallBack<File> callBack) {
    }

    public void addImage(Card card, InputStream inputStream, DataCallBack<File> callBack) {
    }

    public void getCards(DataCallBack<Card[]> callBack) {
    }

    public void getCards(DataCallBack<Card[]> callBack, double latitude, double longitude) {
    }

    public void pushUpdates() {
        //TODO: реализовать
    }

    public ApiWorker getApiWorker() {
        return apiWorker;
    }

    public DataFileContainer getDataFileContainer() {
        return dataFileContainer;
    }

    public static void registerListener(@NotNull EventListener eventListener) {
        Class<?> clazz = eventListener.getClass();
        while (!clazz.isPrimitive()) {
            for (Method m : clazz.getMethods()) {
                if (m.isAnnotationPresent(EventHandler.class) && m.getParameterTypes().length == 1 && Event.class.isAssignableFrom(m.getParameterTypes()[0])) {
                    Class<? extends Event> type = (Class<? extends Event>) m.getParameterTypes()[0];
                    if (listenerMap.get(type) == null)
                        listenerMap.put(type, Collections.synchronizedList(new ArrayList<>()));
                    listenerMap.get(type).add(new ListenerEventRunner(m, eventListener));
                }
            }
        }
    }

//    public static void unregisterListener(@NotNull EventListener eventListener) {
//        listenerMap.forEach((clazz, listener) -> {
//            listener.removeIf(runner -> runner.getEventListener().equals(eventListener));
//        });
//    }

    public static void runEvent(@NotNull Event event) {
        for (ListenerEventRunner runner : listenerMap.get(event.getClass())) {
            try {
                runner.run(event);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Event processing error " + runner, e);
            }
        }
    }

    private static <T> void runCallback(DataCallBack<T> callBack, DataCallBack.DataStatus status, T type) {
        try {
            callBack.callback(status, type);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Callback error", e);
        }
    }


}
