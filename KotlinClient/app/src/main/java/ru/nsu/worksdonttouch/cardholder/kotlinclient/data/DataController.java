package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.Event;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.EventHandler;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.EventListener;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.ListenerEventRunner;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.ApiWorker;

public class DataController {

    private static DataController instance;

    private final static Logger logger = Logger.getLogger(DataController.class.getName());

    private ApiWorker apiWorker = null;

    private final DataFileContainer dataFileContainer;

    private final static Map<Class<? extends Event>, List<ListenerEventRunner>> listenerMap = new ConcurrentHashMap<>();

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
//        if (apiWorker == null) {
//            throw new IllegalStateException("Not authorized");
//
//        }
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

    public static void registerListener(@NotNull EventListener eventListener) {
        Class<?> clazz = eventListener.getClass();
        while (!clazz.isPrimitive()) {
            for(Method m : clazz.getMethods()) {
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

    private static void runEvent(@NotNull Event event) {
        for(ListenerEventRunner runner : listenerMap.get(event.getClass())) {
            try {
                runner.run(event);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Event processing error " + runner, e);
            }
        }
    }

}
