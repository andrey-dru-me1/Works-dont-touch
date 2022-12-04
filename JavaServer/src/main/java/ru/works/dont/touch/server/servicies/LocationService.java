package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Location;
import ru.works.dont.touch.server.repositories.LocationRepository;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;

import java.util.stream.Stream;

@Service
public class LocationService {
    private LocationRepository locationRepository;
    public LocationService(LocationRepository locationRepository){
        this.locationRepository= locationRepository;
    }


    public Iterable<Location> findAll(){
        return locationRepository.findAll();
    }
    public Stream<Location> findAllByCardId(Long cardId){
        return locationRepository.findAllByCardId(cardId);
    }
    public Stream<Location> findAllCustom(Boolean isCustom){
        return locationRepository.findAllByCustom(isCustom);
    }


    public void deleteById(Long id){
        locationRepository.deleteAllById(id);
    }
    public void deleteByCardId(Long cardId){
        locationRepository.deleteAllByCardId(cardId);
    }

    public Location save(Location location) throws ExistsException {
        if (locationRepository.existsById(location.getId())){
            throw new ExistsException("location already exists: "
            + location);
        }
        return locationRepository.save(location);
    }
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
        if (!locationRepository.existsById(locationId)){
            throw new NotExistsException("Location doesnt exists: "+locationId);
        }
        Location location = locationRepository.getLocationById(locationId);
        if (isCustom != null){
            location.setCustom(isCustom);
        }
        if (name != null){
            location.setName(name);
        }
        if (cardId != null){
            location.setCardId(cardId);
        }
        locationRepository.update(location.getId(),
                location.getName(),
                location.getCustom(),
                location.getCardId());
        return location;
    }

}
