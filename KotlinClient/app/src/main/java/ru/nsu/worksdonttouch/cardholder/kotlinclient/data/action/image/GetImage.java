package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action.image;

import java.io.File;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataCallBack;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action.DataPairAction;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;

public class GetImage extends DataPairAction<File, Card, Long, File> {

    public GetImage(DataController dataController, DataCallBack<File> callBack) {
        super(dataController, callBack);
    }

    @Override
    protected void onlineRun(Card card, Long id) {
        File f = dataFileContainer.getImageFile(id, card);
        if(f.exists()) {
            runCallback(DataCallBack.DataStatus.OK, f);
        } else {
            apiWorker.imageGet(id, f, this::httpReaction);
        }
    }

    @Override
    protected void offlineRun(Card card, Long id) {
        File f = dataFileContainer.getImageFile(id, card);
        if(f.exists()) {
            runCallback(DataCallBack.DataStatus.OK, f);
        } else {
            runCallback(DataCallBack.DataStatus.CANCELED, f);
        }
    }

    @Override
    protected void onSuccessful(File object) {
        runCallback(DataCallBack.DataStatus.OK, object);
    }

    @Override
    protected void onFail(File object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onNoPermission(File object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onNotFound(File object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onNoConnection(File object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onWrongRequest(File object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onOther(File object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }
}
