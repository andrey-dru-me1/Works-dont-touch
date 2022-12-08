package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;


import org.jetbrains.annotations.NotNull;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.action.card.CreateCard;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.action.card.EditCard;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.action.image.AddImage;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.CardList;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Cards;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.Event;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.EventHandler;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.EventListener;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.ListenerEventRunner;
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

    public void startOffline() {
        isOffline = true;
        apiWorker = null;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void createCard(String name, String barcode, DataCallBack<Card> callBack) {
        CreateCard createCard = new CreateCard(this, callBack);
        createCard.apply(name, barcode);
    }

    public void editCard(Card card, DataCallBack<Card> callBack) {
        EditCard editCard = new EditCard(this, callBack);
        editCard.apply(card);
    }

    public void deleteCard(Card card, DataCallBack<Card> callBack) {
    }

    public void getImage(Card card, long id, DataCallBack<File> callBack) {

    }

    public void addImage(Card card, InputStream inputStream, DataCallBack<File> callBack) {
        AddImage addImage = new AddImage(this, callBack);
        addImage.apply(card, inputStream);
    }

    public void getCards(DataCallBack<Cards> callBack) {

    }

    public void getCards(DataCallBack<Cards> callBack, double latitude, double longitude) {
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

    public static void unregisterListener(@NotNull EventListener eventListener) {
        listenerMap.forEach((clazz, listener) -> {
            listener.removeIf(runner -> runner.getEventListener().equals(eventListener));
        });
    }

    public static void runEvent(@NotNull Event event) {
        for (ListenerEventRunner runner : listenerMap.get(event.getClass())) {
            try {
                runner.run(event);
            } catch (Throwable e) {
                logger.log(Level.WARNING, "Event processing error " + runner, e);
            }
        }
    }

    private static <T> void runCallback(DataCallBack<T> callBack, DataCallBack.DataStatus status, T type) {
        try {
            callBack.callback(status, type);
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Callback error", e);
        }
    }


}
