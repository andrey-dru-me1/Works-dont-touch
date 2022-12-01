package ru.works.dont.touch.server.data;

public record Card(long id, String shop, String name, String barcode, Long[] imageids) {}
