package ru.works.dont.touch.server.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Coordinates {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Long locationId;

    private double latitude;

    private double longitude;
}
