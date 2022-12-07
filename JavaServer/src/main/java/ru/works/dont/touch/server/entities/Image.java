package ru.works.dont.touch.server.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long cardId;


    public Long getId() {
        return id;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long imageId) {
        this.cardId = imageId;
    }
}
