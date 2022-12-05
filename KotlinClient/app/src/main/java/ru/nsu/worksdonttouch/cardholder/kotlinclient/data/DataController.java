package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.interaction.CardsData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.User;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.AddCardUpdate;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.ReplaceCardUpdate;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.Update;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.UpdateType;

public class DataController {

    private static DataController instance;

    private final Logger logger = Logger.getLogger(DataController.class.getName());

    private final Collection<Card> cards = new ArrayList<>();

    private User user;

    private final List<UpdateListener> listeners = Collections.synchronizedList(new ArrayList<>());

    private DataController() {}

    public static synchronized DataController getInstance() {
        if(instance == null) {
            instance = new DataController();
        }
        return instance;
    }

    private void onUpdate(Update update) {
        CardsData.saveCards(this.getCards());
        for(UpdateListener listener : listeners) {
            listener.update(update);
        }
    }

    public void addListener(@NonNull UpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(@NonNull UpdateListener listener) {
        listeners.remove(listener);
    }

    public void putCard(Card card) {
        cards.add(card);
        logger.log(Level.INFO, "new card: " + card);
        onUpdate(new AddCardUpdate(card));
    }

    public void putUserFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            user = mapper.readValue("/data/data/ru.nsu.worksdonttouch.cardholder.kotlinclient/user.json", User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void putCardsFromFile() {
        Collection<Card> cardList = CardsData.getCardsFromFile();

        if(cardList == null) return;

        cards.addAll(cardList);
        logger.log(Level.INFO, "loaded cards:  " + cards);
    }

    public Collection<Card> getCards() {
        return cards;
    }

    public User getUser() {
        return user;
    }
}
