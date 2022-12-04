package ru.works.dont.touch.server.rest.v1_0.objects.loaction;

import ru.works.dont.touch.server.rest.v1_0.objects.coordinate.Coordinate;

import java.util.List;

public record Location(String name, boolean isCustom, List<Coordinate> coordinates) {
}
