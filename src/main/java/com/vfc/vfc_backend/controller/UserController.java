package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.*;
import com.vfc.vfc_backend.service.FriendService;
import com.vfc.vfc_backend.service.MealService;
import com.vfc.vfc_backend.service.WorkoutService;
import com.vfc.vfc_backend.service.ChallengeService;
import com.vfc.vfc_backend.service.ChallengeProgressService;
import jakarta.servlet.http.HttpSession;
import com.vfc.vfc_backend.service.UserService;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
    private ChallengeService challengeService;
    private ChallengeProgressService challengeProgressService;



    public UserController(UserService theUserService, FriendService friendService,WorkoutService workoutService,MealService mealService, ChallengeService challengeService, ChallengeProgressService challengeProgressService) {
        this.userService = theUserService;
        this.friendService = friendService;
        this.workoutService =workoutService;
        this.mealService =mealService;
        this.challengeService = challengeService;
        this.challengeProgressService = challengeProgressService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user-registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User theUser, Model model, HttpSession session) {
        try {
            // Check if user with this email already exists
            User existingUser = userService.findByUseremail(theUser.getUserEmail());
            if (existingUser != null) {
                model.addAttribute("error", "A user with this email already exists");
                return "user-registration";
            }
            
            // Check if user with this username already exists
            User existingUsername = userService.findByUsername(theUser.getUserName());
            if (existingUsername != null) {
                model.addAttribute("error", "A user with this username already exists");
                return "user-registration";
            }
            
            String hashPassword = BCrypt.hashpw(theUser.getUserPassword(),BCrypt.gensalt()); // Hashed and Salted the users password before saving to DB
            theUser.setUserPassword(hashPassword);
            User savedUser = userService.save(theUser);
            
            // Automatically log in the user after successful registration
            session.setAttribute("user", savedUser);
            
            return "redirect:/users/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Passwords do not match");
            return "user-registration";
        }
    }

    @PostMapping("/users")
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

    @GetMapping("/friends")
    public String listFriends(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        
        int currentUserId = user.getUserId();
        List<User> friends = friendService.getFriends(currentUserId);
        model.addAttribute("friends", friends);
        model.addAttribute("user", user);
        return "friends-list";
    }

    @PostMapping("/friends")
    public String listFriendsPost(@ModelAttribute("user") User theUser, Model model) {
        int currentUserId = theUser.getUserId();
        List<User> friends = friendService.getFriends(currentUserId);
        model.addAttribute("friends", friends);
        return "friends-list";
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "log-in"; // Returns login.html
    }

    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute("user") User user, HttpSession session, Model theModel) {

        User existingUser = userService.findByUseremail(user.getUserEmail());

        // Check if the username and password are valid using UserService
        if (existingUser != null && BCrypt.checkpw(user.getUserPassword(), existingUser.getUserPassword())){
            session.setAttribute("user", existingUser);
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

        // Fetch all friends for the current user
        List<User> friends = friendService.getFriends(userId);

        // Fetch pending friend requests
        List<FriendRequest> pendingRequests = friendService.getPendingRequests(userId);

        model.addAttribute("lastThreeFriendships", lastThreeFriendships);
        model.addAttribute("friends", friends);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("pendingRequestsCount", pendingRequests.size());

        // Add to model
        model.addAttribute("lastThreeWorkouts", lastThreeWorkouts);
        model.addAttribute("lastThreeMeals", lastThreeMeals);

        model.addAttribute("user", user);
        
        // get challenge data for dashboard
        long activeChallengesCount = challengeService.countActiveChallenges();
        List<Challenge> activeChallengesList = challengeService.getActiveChallenges();
        
        // Get user's active challenge participations with progress
        List<ChallengeParticipant> userActiveChallenges = challengeProgressService.getUserActiveChallenges(userId);
        
        // Calculate challenge statistics
        long completedChallengesCount = challengeService.countCompletedChallenges();
        long userActiveChallengesCount = userActiveChallenges.size();
        
        // Placeholder for dashboard attributes (replace with actual service calls)
        model.addAttribute("todayCalories", 0);
        model.addAttribute("weeklyWorkouts", 0);
        model.addAttribute("activeChallenges", activeChallengesCount);
        model.addAttribute("currentStreak", 0);
        model.addAttribute("recentWorkouts", Collections.emptyList());
        model.addAttribute("activeChallengesList", activeChallengesList);
        model.addAttribute("userActiveChallenges", userActiveChallenges);
        model.addAttribute("userActiveChallengesCount", userActiveChallengesCount);
        model.addAttribute("completedChallengesCount", completedChallengesCount);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
