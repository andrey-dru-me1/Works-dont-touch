package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataCallBack;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataFileContainer;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event.LogOutEvent;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.ApiWorker;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.HttpCallback;

public abstract class DataPairAction<T, R, L, K> {

    protected final static Logger logger = Logger.getLogger(DataController.class.getName());

    protected DataCallBack<T> callBack;
    protected DataController dataController;
    protected DataFileContainer dataFileContainer;
    protected ApiWorker apiWorker;


    public DataPairAction(DataController dataController, DataCallBack<T> callBack) {
        this.callBack = callBack;
        this.dataController = dataController;
        this.apiWorker = dataController.getApiWorker();
        this.dataFileContainer = dataController.getDataFileContainer();
    }

    public void apply(R object1, L object2) {
        if (dataController.isOffline()) {
            offlineRun(object1, object2);
        } else {
            if (apiWorker != null) {
                onlineRun(object1, object2);
            } else {
                runCallback(DataCallBack.DataStatus.WRONG_USER, null);
                DataController.runEvent(new LogOutEvent(null));
            }
        }
    }

    protected abstract void onlineRun(R object1, L object2);

    protected abstract void offlineRun(R object1, L object2);

    protected abstract void onSuccessful(K object);

    protected abstract void onFail(K object);

    protected abstract void onNoPermission(K object);

    protected abstract void onNotFound(K object);

    protected abstract void onNoConnection(K object);

    protected abstract void onWrongRequest(K object);

    protected void onAuthorizationError(K object) {
        runCallback(DataCallBack.DataStatus.WRONG_USER, null);
        DataController.runEvent(new LogOutEvent(apiWorker.getUserData()));
    }

    protected abstract void onOther(K object);

    protected void httpReaction(HttpCallback.HttpResult result, K object) {
        switch (result) {
            case NO_CONNECTION:
                onNoConnection(object);
                break;
            case AUTHORIZATION_ERROR:
                onAuthorizationError(object);
                break;
            case FAIL:
                onFail(object);
                break;
            case NO_PERMISSION:
                onNoPermission(object);
                break;
            case NOT_FOUND:
                onNotFound(object);
                break;
            case OTHER:
                onOther(object);
                break;
            case SUCCESSFUL:
                onSuccessful(object);
                dataController.pushUpdates();
                break;
            case WRONG_REQUEST:
                onWrongRequest(object);
                break;
        }
    }

    protected void runCallback(DataCallBack.DataStatus status, T type) {
        try {
            callBack.callback(status, type);
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Callback error", e);
        }
    }


}
