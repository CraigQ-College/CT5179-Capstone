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
}
