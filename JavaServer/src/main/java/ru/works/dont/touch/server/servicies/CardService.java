package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.repositories.CardRepository;

@Service
public class CardService {
    private final CardRepository cardRepository;

    @Autowired
    private ImageService imageService;
    @Autowired
    private LocationService locationService;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }
    public Card getCardById(Long id) throws NotExistsException {
        var card = cardRepository.findById(id);
        if (card.isEmpty()){
            throw new NotExistsException("Card not exists with id: "+id);
        }
        return card.get();
    }

    public Iterable<Card> getCardsByLogin(String login) {
        return cardRepository.findByUserLogin(login);
    }

    public Iterable<Card> getCardsByUserId(Long ownerId) {
        return cardRepository.findAllByOwnerId(ownerId);
    }

    public Iterable<Card> findAll() {
        return cardRepository.findAll();
    }


    public Card deleteById(Long id) throws NotExistsException {
        var card = getCardById(id);

        try {
            imageService.deleteByCardId(id);
        } catch (NotExistsException ignore) {}
        locationService.deleteByCardId(card.getId());
        cardRepository.deleteAllById(id);
        return card;

    }
    public Iterable<Card> deleteByOwnerId(Long id){
        var cards = getCardsByUserId(id);

        for (Card card : cards) {
            try {
                deleteById(card.getId());
            } catch (NotExistsException ignore) {}
        }

        return cards;
    }

    @Transactional
    public Card saveCard(Card newCard) throws ExistsException {
        if (cardRepository.existsById(newCard.getId())) {
            throw new ExistsException();
        }
        return cardRepository.save(newCard);
    }

    @Transactional
    public Card saveCard(String name, String barcode,
                            Long ownerId) throws ExistsException {
        Card newCard = new Card();
        newCard.setName(name);
        newCard.setBarcode(barcode);
        newCard.setOwnerId(ownerId);
        return saveCard(newCard);
    }

    @Transactional
    public void updateById(Long cardId, String name,
                           String barcode, Long ownerId) throws NotExistsException {
        var up = cardRepository.findById(cardId);
        if (up.isEmpty()){
            throw new NotExistsException("Card not exists" + cardId);
        }
        Card updated = up.get();
        if (name != null) {
            updated.setName(name);
        }
        if (barcode != null) {
            updated.setBarcode(barcode);
        }
        if (ownerId != null) {
            updated.setOwnerId(ownerId);
        }
        cardRepository.cardUpdate(updated.getId(),
                updated.getName(),
                updated.getBarcode(),
                updated.getOwnerId());
    }
}
