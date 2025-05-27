package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import com.vfc.vfc_backend.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {


    private UserService userService;

    public UserController(UserService theUserService) {
        this.userService = theUserService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user-registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User theUser, Model model) {
        try {
            User savedUser = userService.save(theUser);
            model.addAttribute("user", savedUser);
            return "user-registration-summary";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Passwords do not match");
            return "user-registration";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "log-in"; // Returns login.html
    }

    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute("user") User theUser, Model theModel) {

        // Check if the username and password are valid using UserService
        //User user = userService.findByUsername(theUser.getUserName());
        User user = userService.findByUseremail(theUser.getUserEmail());

        if (user != null && user.getUserPassword().equals(theUser.getUserPassword())) {
            // Valid credentials, add username to model for main page
            //theModel.addAttribute("user_name", user.getUserName());
            //theModel.addAttribute("user_id", user.getUserId());
            theModel.addAttribute("user", user);
            return "dashboard"; // Redirect to main.html
        } else {
            // Invalid credentials, add error message and return to login page
            theModel.addAttribute("error", "Invalid username or password");
            return "log-in"; // Return to login.html
        }
    }
}
