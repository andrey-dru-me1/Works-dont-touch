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

import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event.CardAddEvent;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event.CardChangeEvent;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener.event.CardRemoveEvent;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.UserData;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card;
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.LocalCard;

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

    private final File userDataFile;

    private final File tempDir;

    private final ObjectMapper mapper;

    private final AtomicInteger localCardNumber = new AtomicInteger();

    private final AtomicLong localImageNumber = new AtomicLong();

    private final AtomicInteger tempFiles = new AtomicInteger();

    public DataFileContainer(File dir) throws NullPointerException, IOException {
        if (dir.mkdirs()) {
            logger.log(Level.INFO, "Create data directory");
        }
        this.tempDir = new File(dir, "temp");
        if (this.tempDir.mkdirs()) {
            logger.log(Level.INFO, "Create temp directory");
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
        this.updateListFile = new File(dir, "updateList.json");
        if (this.updateListFile.createNewFile()) {
            logger.log(Level.INFO, "Create update list file");
        }
        this.userDataFile = new File(dir, "userData.json");
        if (this.userDataFile.createNewFile()) {
            logger.log(Level.INFO, "Create user data file");
        }
        mapper = new ObjectMapper();
        loadCards();
    }

    public Card save(@NotNull Card card, boolean isSynchronized) throws IOException {
        if(card.getId() != null) {
            File f = new File(cardDir, card.getId() + ".json");
            mapper.writeValue(f, card);
            Card oldCard = cards.put(card.getId(), card);
            if (!isSynchronized) {
                addCardToUpdate(card.getId());
            } else {
                removeCardToUpdate(card.getId());
            }
            if (oldCard != null)
                DataController.runEvent(new CardChangeEvent(card));
            else
                DataController.runEvent(new CardAddEvent(card));
            return card;
        } else {
            LocalCard localCard;
            if (card instanceof LocalCard) {
                localCard = (LocalCard) card;
            } else {
                localCard = new LocalCard(card.getName(), card.getBarcode(), card.getImages(), card.getLocations());
            }
            if (localCard.getLocalID() == null)
                localCard.setLocalID(localCardNumber.incrementAndGet());
            File f = new File(localCardDir, (localCard.getLocalID() + ".json"));
            mapper.writeValue(f, localCard);
            LocalCard oldCard = localCards.put(localCard.getLocalID(), localCard);
            if (oldCard != null)
                DataController.runEvent(new CardChangeEvent(card));
            else
                DataController.runEvent(new CardAddEvent(card));
            return localCard;
        }
    }

    public void deleteCard(@NotNull Card card) throws IOException {
        if(card.getId() != null) {
            File f = new File(cardDir, card.getId() + ".json");
            f.delete();
            if(cards.remove(card.getId()) != null)
                DataController.runEvent(new CardRemoveEvent(card));
        } else {
            if (card instanceof LocalCard) {
                LocalCard localCard = (LocalCard) card;
                if (localCard.getLocalID() == null)
                    localCard.setLocalID(localCardNumber.incrementAndGet());
                File f = new File(localCardDir, (localCard.getLocalID() + ".json"));
                f.delete();
                if(localCards.remove(localCard.getLocalID()) != null)
                    DataController.runEvent(new CardRemoveEvent(card));
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

    public UserData getUserData() throws IOException {
        UserData userData = mapper.readValue(userDataFile, UserData.class);
        return userData;
    }

    public void setUserData(UserData userData) throws IOException {
        mapper.writeValue(userDataFile, UserData.class);
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

    public File getImageFile(long image, Card card) {
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

    public List<Card> getUpdateList() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(this.localCards.values().stream().map(LocalCard::clone).collect(Collectors.toList()));
        cards.addAll(updateSet.stream().map(aLong -> this.cards.get(aLong)).filter(Objects::nonNull).collect(Collectors.toList()));
        return cards;
    }

    public File getTempFile() {
        return new File(tempDir, Integer.toString(tempFiles.incrementAndGet()));
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
