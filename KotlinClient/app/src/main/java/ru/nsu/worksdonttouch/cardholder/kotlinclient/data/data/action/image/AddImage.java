package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.action.image;

import android.os.FileUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataCallBack;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.action.DataPairAction;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.LocalCard;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.ImageAnswer;

public class AddImage extends DataPairAction<File, Card, InputStream, ImageAnswer> {

    protected File temp;
    protected Card card;

    public AddImage(DataController dataController, DataCallBack<File> callBack) {
        super(dataController, callBack);
        temp = dataFileContainer.getTempFile();
    }

    @Override
    protected void onlineRun(Card object1, InputStream inputStream) {
        if (object1.getId() == null) {
            offlineRun(object1, inputStream);
            return;
        }
        try {
            temp.createNewFile();
            try (OutputStream outputStream = new FileOutputStream(temp)) {
                byte[] buffer = new byte[1<<17];
                int size;
                while((size = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, size);
                }
            }
        } catch (Exception e) {
            ioFail();
            return;
        }
        this.card = object1;
        apiWorker.uploadImage(temp, object1.getId(), this::httpReaction);
    }

    @Override
    protected void offlineRun(Card card, InputStream inputStream) {
        if(!(card instanceof LocalCard)) {
            runCallback(DataCallBack.DataStatus.CANCELED, null);
            return;
        }
        long imageID = dataFileContainer.getNewLocalImageID();
        try {
            File f = dataFileContainer.getImageFile(imageID, card);
            try (OutputStream outputStream = new FileOutputStream(f)) {
                byte[] buffer = new byte[1<<17];
                int size;
                while((size = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, size);
                }
            }
            card.getImages().add(imageID);
            dataFileContainer.save(card, false);
            runCallback(DataCallBack.DataStatus.NOT_SYNCHRONISED, f);
        } catch (Exception e) {
            ioFail();
            return;
        }
    }

    @Override
    protected void onSuccessful(ImageAnswer object) {
        File toSave = dataFileContainer.getImageFile(object.getId(), card);
        try {
            toSave.createNewFile();
            try (InputStream in = new FileInputStream(toSave)) {
                try (OutputStream out = new FileOutputStream(temp)) {
                    byte[] buffer = new byte[1<<17];
                    int size;
                    while((size = in.read(buffer)) > 0) {
                        out.write(buffer, 0, size);
                    }
                }
            }
            dataFileContainer.save(card, true);
            runCallback(DataCallBack.DataStatus.OK, toSave);
        } catch (Exception e) {
            runCallback(DataCallBack.DataStatus.CANCELED, null);
        } finally {
            temp.delete();
        }
    }

    @Override
    protected void onFail(ImageAnswer object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onNoPermission(ImageAnswer object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onNotFound(ImageAnswer object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onNoConnection(ImageAnswer object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onWrongRequest(ImageAnswer object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    @Override
    protected void onOther(ImageAnswer object) {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

    private void ioFail() {
        runCallback(DataCallBack.DataStatus.CANCELED, null);
    }

}
