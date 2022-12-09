package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action.card;

import java.io.IOException;
import java.util.logging.Level;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataCallBack;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action.DataPairAction;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.LocalCard;

public class CreateCard extends DataPairAction<Card, String, String, Card> {

    public CreateCard(DataController dataController, DataCallBack<Card> callBack) {
        super(dataController, callBack);
    }

    @Override
    protected void onlineRun(String object1, String object2) {
        Card card = new Card(object1, object2, null, null);
        apiWorker.addCard(card, this::httpReaction);
    }

    @Override
    protected void offlineRun(String object1, String object2) {
        notSynchronizedSave(new Card(object1, object1, null, null));
    }

    @Override
    protected void onSuccessful(Card object) {
        synchronizedSave(object);
    }

    @Override
    protected void onFail(Card object) {
        notSynchronizedSave(object);
    }

    @Override
    protected void onNoPermission(Card object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onNotFound(Card object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onNoConnection(Card object) {
        notSynchronizedSave(object);
    }

    @Override
    protected void onWrongRequest(Card object) {
        logger.log(Level.WARNING, "wrong request for card create" + object);
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onOther(Card object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    private void notSynchronizedSave(Card card) {
        LocalCard localCard = new LocalCard(card.getName(), card.getBarcode(), card.getImages(), card.getLocations());
        try {
            dataFileContainer.save(localCard, false);
            runCallback(DataCallBack.DataStatus.NOT_SYNCHRONISED, localCard);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Card edit IOException ", e);
            runCallback(DataCallBack.DataStatus.CANCELED, null);
        }
    }

    private void synchronizedSave(Card card) {
        try {
            dataFileContainer.save(card, true);
            runCallback(DataCallBack.DataStatus.OK, card);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Card edit IOException ", e);
            runCallback(DataCallBack.DataStatus.CANCELED, null);
        }
    }

}
