package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.action.card;

import java.io.IOException;
import java.util.logging.Level;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataCallBack;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.action.DataAction;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;

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
        try {
            dataFileContainer.save(object);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Card edit IOException ", e);
            runCallback(DataCallBack.DataStatus.CANCELED, null);
        }
    }

    @Override
    protected void onSuccessful(Card object) {
        try {
            dataFileContainer.save(object);
            runCallback(DataCallBack.DataStatus.OK, null);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Card edit IOException ", e);
            runCallback(DataCallBack.DataStatus.CANCELED, null);
        }
    }

    @Override
    protected void onFail(Card object) {
        dataFileContainer.addCardToUpdate(object);
        runCallback(DataCallBack.DataStatus.NOT_SYNCHRONISED, null);
    }

    @Override
    protected void onNoPermission(Card object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onNotFound(Card object) {
        try {
            dataFileContainer.save(object);
            runCallback(DataCallBack.DataStatus.NOT_SYNCHRONISED, null);.
        } catch (IOException e) {
            logger.log(Level.WARNING, "Card edit IOException ", e);
            runCallback(DataCallBack.DataStatus.CANCELED, null);
        }
    }

    @Override
    protected void onNoConnection(Card object) {

    }

    @Override
    protected void onWrongRequest(Card object) {

    }

    @Override
    protected void onOther(Card object) {

    }

}
