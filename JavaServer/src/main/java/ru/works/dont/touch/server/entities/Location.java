package ru.works.dont.touch.server.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Foo")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    private int id;

    private Long placeId;

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

}