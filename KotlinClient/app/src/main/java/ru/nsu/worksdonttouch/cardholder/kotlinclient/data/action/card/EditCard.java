package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action.card;

import java.io.IOException;
import java.util.logging.Level;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataCallBack;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action.DataAction;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event.CardChangeEvent;

public class EditCard extends DataAction<Card, Card, Card> {

    public EditCard(DataController dataController, DataCallBack<Card> callBack) {
        super(dataController, callBack);
    }

    @Override
    protected void onlineRun(Card object) {
        apiWorker.editCard(object, this::httpReaction);
    }

    @Override
    protected void offlineRun(Card object) {
        notSynchronizedSave(object);
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
        notSynchronizedSave(object);
    }

    @Override
    protected void onNoConnection(Card object) {
        notSynchronizedSave(object);
    }

    @Override
    protected void onWrongRequest(Card object) {
        logger.log(Level.WARNING, "wrong request for card edit" + object);
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onOther(Card object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    private void notSynchronizedSave(Card card) {
        try {
            dataFileContainer.save(card, false);
            runCallback(DataCallBack.DataStatus.NOT_SYNCHRONISED, card);
            DataController.runEvent(new CardChangeEvent(card));
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
