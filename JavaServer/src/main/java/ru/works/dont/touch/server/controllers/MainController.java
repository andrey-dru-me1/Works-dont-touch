package ru.works.dont.touch.server.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.works.dont.touch.server.data.entities.User;
import ru.works.dont.touch.server.data.servicies.UserService;

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
        boolean saved = userService.saveNewUser("20", new byte[1]);

        return saved? "Saved": "Not saved";
    }

    @GetMapping(path="/al")
    public @ResponseBody Iterable<User> getAllUsers() {
        System.out.println("HEllo\n");
        // This returns a JSON or XML with the users
        return userService.findAll();
    }

    @GetMapping(path="/up")
    public @ResponseBody boolean updateUser() {
        byte[] psw = new byte[1];
        psw[0] = 2;
        return userService.changeByLogin("20", psw);
    }

    @GetMapping(path="/gbl")
    public @ResponseBody byte[] getByLogin() {
        return userService.getPasswordByLogin("20");
    }
}