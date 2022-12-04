package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import ru.works.dont.touch.server.entities.Coordinate;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.repositories.CoordinateRepository;

import java.util.stream.Stream;

public class CoordinateService {
    private CoordinateRepository coordinateRepository;

    public CoordinateService(CoordinateRepository coordinateRepository) {
        this.coordinateRepository = coordinateRepository;
    }


    public Iterable<Coordinate> findAll() {
        return coordinateRepository.findAll();
    }

    public Stream<Coordinate> findAllByCardId(Long cardId) {
        return coordinateRepository.findByCardId(cardId);
    }

    public Coordinate findById(Long id) {
        return coordinateRepository.findById(id);
    }


    public void deleteById(Long id) {
        coordinateRepository.deleteById(id);
    }

    public void deleteByCardId(Long cardId) {
        coordinateRepository.deleteAllByLocationId(cardId);
    }

    public Coordinate save(Coordinate coordinate) throws ExistsException {
        if (coordinateRepository.existsById(coordinate.getId())) {
            throw new ExistsException("location already exists: "
                    + coordinate);
        }
        return coordinateRepository.save(coordinate);
    }

    public Coordinate save(Long locationId,
                           Double latitude,
                           Double longitude) throws ExistsException {
        Coordinate newCoordinate = new Coordinate();
        newCoordinate.setLatitude(latitude);
        newCoordinate.setLongitude(longitude);
        newCoordinate.setLocationId(locationId);
        return coordinateRepository.save(newCoordinate);
    }


    /**
     * Set null in field, if it must not be changed
     *
     * @param coordId
     * @param locationId
     * @param latitude
     * @param longitude
     * @return newLocation
     */
    @Transactional
    public Coordinate update(long coordId,
                             Long locationId,
                             Double latitude,
                             Double longitude) throws NotExistsException {
        if (!coordinateRepository.existsById(locationId)) {
            throw new NotExistsException("Location doesnt exists: " + locationId);
        }
        Coordinate location = coordinateRepository.findById(coordId);
        if (locationId != null) {
            location.setLocationId(locationId);
        }
        if (latitude != null) {
            location.setLatitude(latitude);
        }
        if (longitude != null) {
            location.setLongitude(longitude);
        }
        coordinateRepository.updateById(location.getId(),
                location.getLocationId(),
                location.getLatitude(),
                location.getLongitude());
        return location;
    }
}
