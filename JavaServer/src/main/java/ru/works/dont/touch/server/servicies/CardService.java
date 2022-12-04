package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.repositories.CardRepository;

@Service
public class CardService {
    private CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
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

    public Card saveCard(Card newCard) throws ExistsException {
        if (cardRepository.existsById(newCard.getId())) {
            throw new ExistsException();
        }
        return cardRepository.save(newCard);
    }

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
