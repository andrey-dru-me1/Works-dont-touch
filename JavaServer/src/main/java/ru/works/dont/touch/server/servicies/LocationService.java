package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Location;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.repositories.LocationRepository;

import java.util.Optional;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    @Autowired
    private CoordinateService coordinateService;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }


    public Iterable<Location> findAll() {
        return locationRepository.findAll();
    }

    public Iterable<Location> findAllByCardId(Long cardId) {
        return locationRepository.findAllByCardId(cardId);
    }

    public Iterable<Location> findAllCustom(Boolean isCustom) {
        return locationRepository.findAllByCustom(isCustom);
    }

    public Location findById(Long locationId) throws NotExistsException {
        Optional<Location> location = locationRepository.findById(locationId);
        if (location.isEmpty()) {
            throw new NotExistsException("Location doesn't exist" + locationId);
        }
        return location.get();
    }

    @Transactional
    public void deleteById(Long id) {
        locationRepository.deleteAllById(id);
        coordinateService.deleteByLocationId(id);
    }


    @Transactional
    public void deleteByCardId(Long cardId) {
        locationRepository.deleteAllByCardId(cardId);
        coordinateService.deleteByCardId(cardId);
    }

    @Transactional
    public Location save(Location location) throws ExistsException {
        if (locationRepository.existsById(location.getId())) {
            throw new ExistsException("location already exists: "
                    + location);
        }
        return locationRepository.save(location);
    }
    @Transactional
    public Location save(boolean isCustom,
                         String name,
                         long cardId) throws ExistsException {
        Location newLocation = new Location();
        newLocation.setName(name);
        newLocation.setCustom(isCustom);
        newLocation.setCardId(cardId);
        return locationRepository.save(newLocation);
    }

    /**
     * Set null in field, if it must not be changed
     *
     * @param locationId
     * @param isCustom
     * @param name
     * @param cardId
     * @return newLocation
     */
    @Transactional
    public Location update(Long locationId,
                           Boolean isCustom,
                           String name,
                           Long cardId) throws NotExistsException {
        if (!locationRepository.existsById(locationId)) {
            throw new NotExistsException("Location doesnt exists: " + locationId);
        }
        Location location = locationRepository.getLocationById(locationId);
        if (isCustom != null) {
            location.setCustom(isCustom);
        }
        if (name != null) {
            location.setName(name);
        }
        if (cardId != null) {
            location.setCardId(cardId);
        }
        locationRepository.update(location.getId(),
                location.getName(),
                location.getCustom(),
                location.getCardId());
        return location;
    }

}
