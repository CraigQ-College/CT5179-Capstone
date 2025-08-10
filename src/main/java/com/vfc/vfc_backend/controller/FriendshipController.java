package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.User;
import com.vfc.vfc_backend.service.FriendService;
import com.vfc.vfc_backend.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/friendships")
public class FriendshipController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/friends")
    public String listFriends(@ModelAttribute("user") User theUser, Model model) {
        int currentUserId = theUser.getUserId();
        int userId = theUser.getUserId();
        List<User> friends = friendService.getFriends(currentUserId);
        model.addAttribute("friends", friends);
        model.addAttribute("userId", userId);
        return "friends-list";
    }
/*
    @GetMapping("/friends")
    public String listFriends(@ModelAttribute("user") User theUser, Model model) {
        int currentUserId = theUser.getUserId();
        List<User> friends = friendService.getFriends(currentUserId);
        model.addAttribute("friends", friends);
        return "friends-list";
    }*/

}
