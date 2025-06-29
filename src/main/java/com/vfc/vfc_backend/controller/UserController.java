package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.*;
import com.vfc.vfc_backend.repository.FriendRequestRepository;
import com.vfc.vfc_backend.service.FriendService;
import com.vfc.vfc_backend.service.MealService;
import com.vfc.vfc_backend.service.WorkoutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.vfc.vfc_backend.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController {


    private UserService userService;
    private  FriendService friendService;

    private WorkoutService workoutService;
    private MealService mealService;



    public UserController(UserService theUserService, FriendService friendService,WorkoutService workoutService,MealService mealService) {
        this.userService = theUserService;
        this.friendService = friendService;
        this.workoutService =workoutService;
        this.mealService =mealService;
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
/*
    @PostMapping("/users")
    public String listUsers(@ModelAttribute("user") User theUser, Model model) {
        //model.addAttribute("user", new User());
        int currentUserId = theUser.getUserId();
        List<User> users = userService.findAll();

        /*
        // Create a map to store whether a friend request can be sent for each user
        Map<Integer, Boolean> canSendFriendRequest = new HashMap<>();
        for (User user : users) {
            boolean canSend = user.getUserId() != currentUserId &&
                    !friendService.isFriend(currentUserId, user.getUserId()) &&
                    friendRequestRepository.findBySenderAndReceiver(
                            userRepository.findById(currentUserId).orElse(null), user
                    ).isEmpty();
            canSendFriendRequest.put(user.getUserId(), canSend);
        }
        model.addAttribute("users", users);
        model.addAttribute("currentUserId", currentUserId);
        return "user-list";
    }*/

    @PostMapping("/users")
//public String listUsers(@ModelAttribute("user") User theUser, Model model) {
    public String listUsers(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<User> users = userService.findAll();
        if (users == null) {
            users = Collections.emptyList();
        }
        Map<Integer, Boolean> isFriendMap = new HashMap<>();
        for (User u : users) {
            if (u.getUserId() == user.getUserId()) {
                isFriendMap.put(u.getUserId(), false);
                continue;
            }
            boolean isFriend = friendService.isFriend(user.getUserId(), u.getUserId());
            isFriendMap.put(u.getUserId(), isFriend);
        }
        model.addAttribute("users", users);
        model.addAttribute("currentUserId", user.getUserId());
        model.addAttribute("isFriendMap", isFriendMap);
        model.addAttribute("userForm", new User());
        return "user-list";
    }

    @GetMapping("/users/{id}")
    public String viewProfile(@PathVariable("id") int id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user-profile";
    }

    @PostMapping("/users/{receiverId}/friend-request")
    public String sendFriendRequest(@ModelAttribute("userForm") User userForm,HttpSession session, Model model,@PathVariable("receiverId") int receiverId) {
        try {
            User user = (User) session.getAttribute("user");
            //int senderId = userForm.getUserId();
            int senderId = user.getUserId();
            friendService.sendFriendRequest(senderId, receiverId);
            //model.addAttribute("user", userForm);
            return "redirect:/users/users";
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Reload user list with error
            int currentUserId = userForm.getUserId();
            List<User> users = userService.findAll();
            if (users == null) {
                users = Collections.emptyList();
            }
            Map<Integer, Boolean> isFriendMap = new HashMap<>();
            for (User u : users) {
                if (u.getUserId() == currentUserId) {
                    isFriendMap.put(u.getUserId(), false);
                    continue;
                }
                boolean isFriend = friendService.isFriend(currentUserId, u.getUserId());
                isFriendMap.put(u.getUserId(), isFriend);
            }
            model.addAttribute("users", users);
            model.addAttribute("currentUserId", currentUserId);
            model.addAttribute("isFriendMap", isFriendMap);
            model.addAttribute("userForm", new User());
            model.addAttribute("error", e.getMessage());
            return "user-list";
        }

    }

    @GetMapping("/users/pending")
    //public String listPendingRequests(@ModelAttribute("user") User theUser,HttpSession session,Model model) {
    public String showPendingRequests(HttpSession session,Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        try {
            List<FriendRequest> pendingRequests = friendService.getPendingRequests(user.getUserId());
            /*for (FriendRequest request : pendingRequests) {
                User sender = userService.findById(request.getSenderId());
                //request.setSenderName(sender != null ? sender.getUserName() : "Unknown");
                //request.setSenderEmail(sender != null ? sender.getUserEmail() : "Unknown");
            }*/
            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("currentUserId", user.getUserId());
            return "pending-requests";
        }catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
        //int currentUserId =theUser.getUserId();
        //int currentUserId =user.getUserId();
        //List<FriendRequest> pendingRequests = friendService.getPendingRequests(currentUserId);
        //model.addAttribute("pendingRequests", pendingRequests);
        //return "pending-requests";
    }

    @PostMapping("/pending")
    public String listPendingRequests(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        try {
            List<FriendRequest> pendingRequests = friendService.getPendingRequests(user.getUserId());
            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("currentUserId", user.getUserId());
            return "pending-requests";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "error";
        }
    }

    @PostMapping("/requests/{requestId}/accept")
    public String acceptFriendRequest(@PathVariable("requestId") int requestId) {
        friendService.acceptFriendRequest(requestId);
        return "redirect:/requests/pending";
    }

    @PostMapping("/friends")
    public String listFriends(@ModelAttribute("user") User theUser,Model model) {
        int currentUserId = theUser.getUserId();
        List<User> friends = friendService.getFriends(currentUserId);
        model.addAttribute("friends", friends);
        return "friends-list";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "log-in"; // Returns login.html
    }

    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute("user") User user, HttpSession session, Model theModel) {

        // Check if the username and password are valid using UserService
        //User user = userService.findByUsername(theUser.getUserName());
        User existingUser = userService.findByUseremail(user.getUserEmail());


        if (existingUser != null && existingUser.getUserPassword().equals(user.getUserPassword())) {
            // Valid credentials, add username to model for main page
            //theModel.addAttribute("user_name", user.getUserName());
            //theModel.addAttribute("user_id", user.getUserId());
            //theModel.addAttribute("user", user);
            session.setAttribute("user", existingUser);
            //return "dashboard"; // Redirect to main.html
            return "redirect:/users/dashboard";
        } else {
            // Invalid credentials, add error message and return to login page
            theModel.addAttribute("error", "Invalid username or password");
            return "log-in"; // Return to login.html
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        int userId = user.getUserId();

        // Fetch the last 3 workouts
        List<Workout> lastThreeWorkouts = workoutService.getLastThreeWorkoutsByUserId(userId);

        // Fetch the last 3 meals
        List<Meal> lastThreeMeals = mealService.getLastThreeMealsByUserId(userId);

        // Fetch last 3 friendships
        List<Friendship> lastThreeFriendships = friendService.getLastThreeFriendshipsByUserId(userId);

        model.addAttribute("lastThreeFriendships", lastThreeFriendships);

        // Add to model
        model.addAttribute("lastThreeWorkouts", lastThreeWorkouts);
        model.addAttribute("lastThreeMeals", lastThreeMeals);

        model.addAttribute("user", user);
        // Placeholder for dashboard attributes (replace with actual service calls)
        model.addAttribute("todayCalories", 0);
        model.addAttribute("weeklyWorkouts", 0);
        model.addAttribute("activeChallenges", 0);
        model.addAttribute("currentStreak", 0);
        model.addAttribute("recentWorkouts", Collections.emptyList());
        model.addAttribute("activeChallengesList", Collections.emptyList());
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
