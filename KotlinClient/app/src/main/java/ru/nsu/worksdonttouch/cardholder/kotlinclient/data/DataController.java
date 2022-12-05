package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.AddCardUpdate;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.ReplaceCardUpdate;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.Update;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.UpdateType;

public class DataController {

    private static DataController instance;

    private final Logger logger = Logger.getLogger(DataController.class.getName());

    private final Map<Long, Card> cards = Collections.synchronizedMap(new HashMap<>());

    private long id = 0;

    private final List<UpdateListener> listeners = Collections.synchronizedList(new ArrayList<>());

    private DataController() {}

    public static synchronized DataController getInstance() {
        if(instance == null) {
            instance = new DataController();
        }
        return instance;
    }

    private void onUpdate(Update update) {
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

    public Card getCard(long id) {
        return cards.get(id);
    }

    public Card putCard(Card card) {
        Card removedCard = cards.put(card.getId(), card);
        logger.log(Level.INFO, "old card: " + removedCard + " new card " + card);
        if (removedCard == null)
            onUpdate(new AddCardUpdate(card));
        else
            onUpdate(new ReplaceCardUpdate(removedCard, card));
        return removedCard;
    }

    public Collection<Card> getCards() {
        return cards.values();
    }

    public long nextId() {
        return this.id++;
    }

}
