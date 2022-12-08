package ru.nsu.worksdonttouch.cardholder.kotlinclient.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.LocalCard;

public class DataFileContainer {

    private final Logger logger = Logger.getLogger(DataFileContainer.class.getName());

    private Map<Long, Card> cards;

    private Map<Integer, LocalCard> localCards;

    private Set<Long> updateSet = Collections.synchronizedSet(new HashSet<>());

    private final File cardDir;

    private final File localCardDir;

    private final File imagesDir;

    private final File localImagesDir;

    private final File updateListFile;

    private final ObjectMapper mapper;

    private final AtomicInteger localCardNumber = new AtomicInteger();

    private final AtomicLong localImageNumber = new AtomicLong();

    public DataFileContainer(File dir) throws NullPointerException, IOException {
        if (dir.mkdirs()) {
            logger.log(Level.INFO, "Create data directory");
        }
        this.cardDir = new File(dir, "cards");
        if (this.cardDir .mkdirs()) {
            logger.log(Level.INFO, "Create cards directory");
        }
        this.localCardDir = new File(dir, "localCards");
        if (this.localCardDir.mkdirs()) {
            logger.log(Level.INFO, "Create local cards directory");
        }
        this.imagesDir = new File(dir, "images");
        if (imagesDir.mkdirs()) {
            logger.log(Level.INFO, "Create images directory");
        }
        this.localImagesDir = new File(dir, "localImages");
        if (localImagesDir.mkdirs()) {
            logger.log(Level.INFO, "Create local images directory");
        }
        this.updateListFile = new File(dir, "updateList");
        if (this.updateListFile.createNewFile()) {
            logger.log(Level.INFO, "Create update list file");
        }
        mapper = new ObjectMapper();
        loadCards();
    }

    public void save(@NotNull Card card) throws IOException {
        if(card.getId() != null) {
            File f = new File(cardDir, card.getId() + ".json");
            mapper.writeValue(f, card);
            cards.put(card.getId(), card);
        } else {
            if (card instanceof LocalCard) {
                LocalCard localCard = (LocalCard) card;
                if (localCard.getLocalID() == null)
                    localCard.setLocalID(localCardNumber.incrementAndGet());
                File f = new File(localCardDir, (localCard.getLocalID() + ".json"));
                mapper.writeValue(f, localCard);
                localCards.put(localCard.getLocalID(), localCard);
            }
        }
    }

    public void deleteCard(@NotNull Card card) throws IOException {
        if(card.getId() != null) {
            File f = new File(cardDir, card.getId() + ".json");
            f.delete();
            cards.remove(card.getId());
        } else {
            if (card instanceof LocalCard) {
                LocalCard localCard = (LocalCard) card;
                if (localCard.getLocalID() == null)
                    localCard.setLocalID(localCardNumber.incrementAndGet());
                File f = new File(localCardDir, (localCard.getLocalID() + ".json"));
                f.delete();
                localCards.remove(localCard.getLocalID());
            }
        }
    }

    public boolean contain(@NotNull Card card) {
        if (card instanceof LocalCard) {
            LocalCard localCard = (LocalCard) card;
            return localCards.containsKey(localCard.getLocalID());
        } else {
            return cards.containsKey(card.getId());
        }
    }

    private void loadCards() throws NullPointerException {
        localCards = new ConcurrentHashMap<>();
        for (File localFile : Objects.requireNonNull(localCardDir.listFiles((dir, name) -> name.endsWith(".json")))) {
            try {
                LocalCard card = mapper.readValue(localFile, LocalCard.class);
                localCards.put(card.getLocalID(), card);
                if (card.getLocalID() > localCardNumber.get()) {
                    localCardNumber.set(card.getLocalID());
                }
                for (Long image : card.getImages()) {
                    if (image > localImageNumber.get()) {
                        localImageNumber.set(image);
                    }
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "card read card " + localFile.getAbsolutePath(), e);
                localFile.delete();
            }
        }
        cards = new ConcurrentHashMap<>();
        for (File file : Objects.requireNonNull(cardDir.listFiles((dir, name) -> name.endsWith(".json")))) {
            try {
                Card card = mapper.readValue(file, Card.class);
                cards.put(card.getId(), card);
            } catch (Exception e) {
                logger.log(Level.WARNING, "card read card " + file.getAbsolutePath(), e);
                file.delete();
            }
        }
        try {
            updateSet.clear();
            updateSet.addAll(mapper.readValue(updateListFile, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Long.class)));
        } catch (Exception e) {

        }
    }

    private File getImageFile(long image, Card card) {
        if (card instanceof LocalCard) {
            return new File(localImagesDir, Long.toString(image));
        } else {
            return new File(imagesDir, Long.toString(image));
        }
    }

    public Long getNewLocalImageID() {
        return localImageNumber.incrementAndGet();
    }

    public List<Card> getCards() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(this.localCards.values().stream().map(LocalCard::clone).collect(Collectors.toList()));
        cards.addAll(this.cards.values().stream().map(Card::clone).collect(Collectors.toList()));
        return cards;
    }

    public void addCardToUpdate(long id) {
        updateSet.add(id);
        updateSet.removeIf(checkID -> !cards.containsKey(checkID));
    }

    public void removeCardToUpdate(long id) {
        updateSet.remove(id);
        updateSet.removeIf(checkID -> !cards.containsKey(checkID));
    }

    public List<Card> getUpdateSet() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(this.localCards.values().stream().map(LocalCard::clone).collect(Collectors.toList()));
        cards.addAll(updateSet.stream().map(aLong -> this.cards.get(aLong)).filter(Objects::nonNull).collect(Collectors.toList()));
        return cards;
    }

    public void clear() {
        cards.clear();
        localCards.clear();
        updateSet.clear();
        this.cardDir.delete();
        this.localCardDir.delete();
        this.imagesDir.delete();
        this.localCardDir.delete();

        if (this.cardDir .mkdirs()) {
            logger.log(Level.INFO, "Create cards directory");
        }
        if (this.localCardDir.mkdirs()) {
            logger.log(Level.INFO, "Create local cards directory");
        }
        if (imagesDir.mkdirs()) {
            logger.log(Level.INFO, "Create images directory");
        }
        if (localImagesDir.mkdirs()) {
            logger.log(Level.INFO, "Create local images directory");
        }
        try {
            mapper.writeValue(updateListFile, updateSet);
        } catch (Exception e) {
            logger.log(Level.WARNING, "update list clear error", e);
        }
    }

}
