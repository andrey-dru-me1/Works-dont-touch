package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.interaction.CardsSaveLoad;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.interaction.UserSaveLoad;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.User;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.AddCardUpdate;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.Update;

public class DataController {

    private static DataController instance;

    private final Logger logger = Logger.getLogger(DataController.class.getName());

    private final Set<Card> cards = new HashSet<>();

    private User user = null;

    private final List<UpdateListener> listeners = Collections.synchronizedList(new ArrayList<>());

    private DataController() {}

    public static synchronized DataController getInstance() {
        if(instance == null) {
            instance = new DataController();
        }
        return instance;
    }

    private void onUpdate(Update update) {
        CardsSaveLoad.saveCards(this.getCards());
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

    public void editCard(Card card, String name, String barcode, Bitmap bitmap, String path) {
        card.setName(name);
        card.setBarcode(barcode);
        card.setImage(bitmap);
        card.setPath(path);
        onUpdate(new AddCardUpdate(card));
    }

    public void putUserFromFile() {
        user = UserSaveLoad.getUserFromFile();
    }

    public void putCardsFromFile() {
        Collection<Card> cardList = CardsSaveLoad.getCardsFromFile();

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

    public void setUser(User user) {
        this.user = user;
        UserSaveLoad.saveUser(user);
    }

}
