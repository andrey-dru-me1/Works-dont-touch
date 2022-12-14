package ru.works.dont.touch.server.rest.v1_0.cards.object.card;

import org.jetbrains.annotations.Nullable;
import ru.works.dont.touch.server.rest.v1_0.cards.object.location.Location;

import java.util.List;

public record CardEditor(Long id, @Nullable String name, @Nullable String barcode, @Nullable List<Long> images, @Nullable List<Location> locations) {
}
