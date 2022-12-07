package ru.works.dont.touch.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.works.dont.touch.server.entities.Image;
import ru.works.dont.touch.server.entities.Location;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.repositories.ImageRepository;
import ru.works.dont.touch.server.repositories.UserRepository;
import ru.works.dont.touch.server.servicies.ImageService;
import ru.works.dont.touch.server.servicies.LocationService;
import ru.works.dont.touch.server.servicies.UserService;

@Controller // This means that this class is a Controller
@RequestMapping(path="/emo") // This means URL's start with /demo (after Application path)
public class Contrl {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private ImageService imageRepository;
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserService userService;
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private LocationService locationService;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewUser () {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        /*
        User n = new User();
        n.setLogin("5");
        n.setPassword(new byte[1]);
        userRepository.save(n);
        */
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Image> getAllUsers() {
        System.out.println("HEllo\n");
        // This returns a JSON or XML with the users
        return imageRepository.findAll();
    }
    @PostMapping(path="/del/all")
    public @ResponseBody Iterable<User> delAllUsers() {
        var users = userService.deleteAllUsers();
        System.out.println(users);
        for (User user : users) {
            System.out.println(user);
        }
        return users;
    }
    @PostMapping(path="/del/loc")
    public @ResponseBody Iterable<Location> delAllLoc() {
        var locations = locationService.findAll();
        System.out.println(locations);
        for (Location loc : locations) {
            System.out.println(loc);
            locationService.deleteById(loc.getId());
        }
        return locations;
    }
}