package ru.works.dont.touch.server.rest.v1_0.cards.object.card;

import java.util.List;

public record CardList(List<CardWithDistance> nearest, List<Long> other) {
}
