package ru.nsu.worksdonttouch.cardholder.kotlinclient.net;

import java.io.File;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.CardList;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.ImageAnswer;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.net.objects.SimpleHttpResult;

public abstract class ApiWorker {

    protected UserData user;

    public static ApiWorker login(UserData data) throws Exception {
        return null;
    }

    public static ApiWorker register(UserData data) throws Exception {
        return null;
    }

    public abstract void changePassword(String password, HttpCallback<SimpleHttpResult> callback) throws Exception;

    public abstract void getCardList(double latitude, double longitude, HttpCallback<CardList> callback) throws Exception;

    public abstract void getCard(long id, HttpCallback<Card> callback) throws Exception;

    public abstract void editCard(Card card, HttpCallback<Card> callback) throws Exception;

    /**
     * TODO: Нужно создать новый объект не содержащий id карты для отправки запроса
     * **/
    public abstract void addCard(Card card, HttpCallback<Card> callback) throws Exception;

    /**
     * Возвращает в callback файл toWrite если все прошло успешно
     * **/
    public abstract void imageGet(long id, File toWrite, HttpCallback<File> callback) throws Exception;

    public abstract void uploadImage(long id, HttpCallback<ImageAnswer> callback) throws Exception;

}
