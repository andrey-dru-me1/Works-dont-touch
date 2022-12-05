package ru.works.dont.touch.server.rest.v1_0.cards.object.location;

import ru.works.dont.touch.server.rest.v1_0.cards.object.coordinate.Coordinate;

import java.util.List;

public record Location(String name, boolean isCustom, List<Coordinate> coordinates) {
}
