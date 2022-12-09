package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;


import org.jetbrains.annotations.NotNull;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action.image.GetImage;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event.CardRemoveEvent;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action.card.CreateCard;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action.card.EditCard;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action.image.AddImage;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.CardList;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.CardWithDistance;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Cards;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.Event;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.EventHandler;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.EventListener;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.ListenerEventRunner;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event.LogOutEvent;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.LocalCard;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.SortedCard;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.ApiWorker;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.HttpCallback;

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

    private boolean isOffline = true;

    private final DataFileContainer dataFileContainer;

    private final static Map<Class<? extends Event>, List<ListenerEventRunner>> listenerMap = new ConcurrentHashMap<>();

    private DataController(File dir) throws IOException {
        dataFileContainer = new DataFileContainer(dir);
        try {
            UserData user = dataFileContainer.getUserData();
            apiWorker = ApiWorker.authTest(user);
            if (apiWorker != null)
                isOffline = false;
        } catch (Exception e) {}
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
        if (isOffline || apiWorker == null) {
            if (card instanceof LocalCard) {
                try {
                    dataFileContainer.deleteCard(card);
                    runCallback(callBack, DataCallBack.DataStatus.OK, card);
                } catch (Exception e) {
                    runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
                }
            } else {
                runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
            }
        } else {
            apiWorker.deleteCard(card, (result, data) -> {
                if(result == HttpCallback.HttpResult.SUCCESSFUL) {
                    runCallback(callBack, DataCallBack.DataStatus.OK, card);
                } else {
                    runCallback(callBack, DataCallBack.DataStatus.CANCELED, null);
                }
            });
        }
    }

    public void getImage(Card card, long id, DataCallBack<File> callBack) {
        GetImage getImage = new GetImage(this, callBack);
        getImage.apply(card, id);
    }

    public void addImage(Card card, InputStream inputStream, DataCallBack<File> callBack) {
        AddImage addImage = new AddImage(this, callBack);
        addImage.apply(card, inputStream);
    }

    public void getCards(DataCallBack<Cards> callBack) {
        if (isOffline || apiWorker == null) {
            runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, new Cards(new ArrayList<>(), dataFileContainer.getCards()));
        } else {
            apiWorker.getCardList((result, cardList) -> {
                switch (result) {
                    case NO_CONNECTION:
                    case FAIL:
                        runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, new Cards(new ArrayList<>(), dataFileContainer.getCards()));
                        break;
                    case SUCCESSFUL:
                        pushUpdates();
                        runCallback(callBack, DataCallBack.DataStatus.OK, new Cards(new ArrayList<>(), dataFileContainer.getCards()));
                        break;
                    case AUTHORIZATION_ERROR:
                        runCallback(callBack, DataCallBack.DataStatus.WRONG_USER, new Cards(new ArrayList<>(), dataFileContainer.getCards()));
                        runEvent(new LogOutEvent(apiWorker.getUserData()));
                        break;
                    case NOT_FOUND:
                        logger.log(Level.WARNING, "not found answer in get card list");
                        runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, new Cards(new ArrayList<>(), dataFileContainer.getCards()));
                        break;
                    case OTHER:
                        logger.log(Level.WARNING, "unknown answer in get card list");
                        runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, new Cards(new ArrayList<>(), dataFileContainer.getCards()));
                        break;
                    case NO_PERMISSION:
                        logger.log(Level.WARNING, "no permission in get card list");
                        runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, new Cards(new ArrayList<>(), dataFileContainer.getCards()));
                        break;
                    case WRONG_REQUEST:
                        logger.log(Level.WARNING, "wrong request");
                        runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, new Cards(new ArrayList<>(), dataFileContainer.getCards()));
                        break;
                }
            });
        }
    }

    public void getCards(DataCallBack<Cards> callBack, double latitude, double longitude) {
        if (isOffline || apiWorker == null) {
            runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, new Cards(new ArrayList<>(), dataFileContainer.getCards()));
        } else {
            apiWorker.getCardList(latitude, longitude, (result, cardList) -> {
                if (result == HttpCallback.HttpResult.SUCCESSFUL) {
                    pushUpdates();
                }
                if (cardList != null) {
                    runCallback(callBack, DataCallBack.DataStatus.OK, generateCards(cardList));
                } else {
                    runCallback(callBack, DataCallBack.DataStatus.NOT_SYNCHRONISED, new Cards(new ArrayList<>(), dataFileContainer.getCards()));
                }
            });
        }
    }

    private Cards generateCards(@NotNull CardList list) {
        Cards cards = new Cards(new ArrayList<>(), new ArrayList<>());
        List<Card> localCards = dataFileContainer.getCards();
        for (Card localCard : localCards) {
            boolean isSorted = false;
            if (localCard.getId() != null) {
                for (CardWithDistance cardWithDistance : list.getNearest()) {
                    if (cardWithDistance.getId().equals(localCard.getId())) {
                        cards.getSortedCards().add(new SortedCard(localCard, cardWithDistance.getDistance()));
                        isSorted = true;
                        break;
                    }
                }
            }
            if (!isSorted) {
                if (localCard.getId() != null && !list.getOther().contains(localCard.getId())) {
                    try {
                        dataFileContainer.deleteCard(localCard);
                    } catch (Exception e) {
                        logger.log(Level.INFO, "Card delete error", e);
                    }
                }
                cards.getOther().add(localCard);
            }
        }
        return cards;
    }

    public void pushUpdates() {
        try {
            for (Card card : dataFileContainer.getUpdateList()) {
                if (card instanceof LocalCard) {
                    apiWorker.addCard(new Card(card.getName(), card.getBarcode(), null, null), (result, data) -> {
                        if (data != null) {
                            try {
                                dataFileContainer.save(data, true);
                                Card createdCard = new Card(data.getId(), card.getName(), card.getBarcode(), new ArrayList<>(), card.getLocations());
                                dataFileContainer.deleteCard(card);
                                apiWorker.editCard(createdCard, (result1, data1) -> {
                                    if (data1 != null) {
                                        try {
                                            dataFileContainer.save(data1, true);
                                        } catch (Exception e) {
                                            logger.log(Level.INFO, "card edit error", e);
                                        }
                                    }
                                });
                                for (long imageID : card.getImages()) {
                                    try {
                                        apiWorker.uploadImage(dataFileContainer.getImageFile(imageID, card), createdCard.getId(), (result1, data1) -> {});
                                    } catch (Exception e) {
                                        logger.log(Level.INFO, "image add error", e);
                                    }
                                }
                            } catch (Exception e) {
                                logger.log(Level.INFO, "card edit error", e);
                            }
                        }
                    });
                } else {
                    apiWorker.editCard(card, (result, data) -> {
                        if (data != null) {
                            try {
                                dataFileContainer.save(data, true);
                            } catch (Exception e) {
                                logger.log(Level.INFO, "card edit error", e);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Update error", e);
        }
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
            clazz = clazz.getSuperclass();
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
