package ru.works.dont.touch.server.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.servicies.UserService;
import ru.works.dont.touch.server.servicies.exceptions.ExistsException;
import ru.works.dont.touch.server.servicies.exceptions.NotExistsException;

@Controller // This means that this class is a Controller
@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class MainController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserService userService;

    @PostMapping(path="/ad") // Map ONLY POST Requests
    public @ResponseBody String addNewUser () {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        /**/
        byte[] fig = new byte[1];
        fig[0] = 5;
        try {
            userService.saveNewUser("25", fig);
            return "saved";
        } catch (ExistsException e) {
            return "Not saved";
        }

    }

    @GetMapping(path="/al")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userService.findAll();
    }

    @GetMapping(path="/up")
    public @ResponseBody String updateUser() {
        byte[] psw = new byte[1];
        psw[0] = 2;
        try {
            userService.changeByLogin("30", psw);
            return "Saved";
        } catch (NotExistsException e) {
            return e.getMessage();
        }
    }

    @GetMapping(path="/gbl")
    public @ResponseBody byte[] getByLogin() {
        try {
            return userService.getPasswordByLogin("30");
        } catch (NotExistsException e) {
            return null;
        }
    }
    @GetMapping(path="/userlog")
    public @ResponseBody User getUserByLogin() {
        try {
            return userService.getUserByLogin("25");
        } catch (NotExistsException e) {
            return null;
        }
    }
}