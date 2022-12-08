package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.action.card;

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

    }

    @Override
    protected void onSuccessful(Card object) {

    }

    @Override
    protected void onFail(Card object) {

    }

    @Override
    protected void onNoPermission(Card object) {

    }

    @Override
    protected void onNotFound(Card object) {

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
