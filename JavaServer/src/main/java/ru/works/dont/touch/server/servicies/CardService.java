package ru.works.dont.touch.server.servicies;

import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.repositories.CardRepository;
import ru.works.dont.touch.server.exceptions.NotExistsException;

import java.util.stream.Stream;

@Service
public class CardService {
    private CardRepository cardRepository;
    public CardService(CardRepository cardRepository){
        this.cardRepository = cardRepository;
    }

    public Stream<Card> getCardsByLogin(String login){
        return cardRepository.findByUserLogin(login);
    }
    public Iterable<Card> getCardsByUserId(Long ownerId){
        return cardRepository.findAllByOwnerId(ownerId);
    }

    public Iterable<Card> findAll(){
        return cardRepository.findAll();
    }

    public boolean saveCard(Card newCard){
        if (cardRepository.existsById(newCard.getId())){
            return false;
        }
        else{
            cardRepository.save(newCard);
            return true;
        }
    }

    public boolean saveCard(String name, String barcode,
                            Long ownerId){
        Card newCard = new Card();
        newCard.setName(name);
        newCard.setBarcode(barcode);
        newCard.setOwnerId(ownerId);
        return saveCard(newCard);
    }

    public void updateById(Long cardId, String name,
                      String barcode, Long ownerId ) throws NotExistsException {
        if (!cardRepository.existsById(cardId)){
            throw new NotExistsException("Card not exists"+cardId);
        }
        Card updated = cardRepository.getCardById(cardId);
        if (name != null){
            updated.setName(name);
        }
        if (barcode != null){
            updated.setBarcode(barcode);
        }
        if (ownerId != null){
            updated.setOwnerId(ownerId);
        }
        cardRepository.cardUpdate(updated.getId(),
                updated.getName(),
                updated.getBarcode(),
                updated.getOwnerId());
    }
}
