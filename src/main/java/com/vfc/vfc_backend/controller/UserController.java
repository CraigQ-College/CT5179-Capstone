package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.*;
import com.vfc.vfc_backend.repository.FriendRequestRepository;
import com.vfc.vfc_backend.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PostService postService;



    public UserController(UserService theUserService, FriendService friendService,WorkoutService workoutService,MealService mealService,PostService postService) {
        this.userService = theUserService;
        this.friendService = friendService;
        this.workoutService =workoutService;
        this.mealService =mealService;
        this.postService = postService;
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

    @GetMapping("/list/{userId}")
    public String listWorkoutsAndMeals(
            @PathVariable("userId") int userId,
            @RequestParam(defaultValue = "1") int workoutPage,
            @RequestParam(defaultValue = "1") int mealPage,
            HttpSession session, Model model) {

        // Optional: Verify user authorization (e.g., check if session user matches userId)
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || sessionUser.getUserId() != userId) {
            return "redirect:/users/login"; // Or handle unauthorized access differently
        }

        int pageSize = 10;

        // Workouts
        List<Workout> workouts = workoutService.getWorkoutsByUserId(userId, workoutPage, pageSize);
        long totalWorkouts = workoutService.countWorkoutsByUserId(userId);
        int totalWorkoutPages = (int) Math.ceil((double) totalWorkouts / pageSize);

        // Meals
        List<Meal> meals = mealService.getMealsByUserId(userId, mealPage, pageSize);
        long totalMeals = mealService.countMealsByUserId(userId);
        int totalMealPages = (int) Math.ceil((double) totalMeals / pageSize);

        // Add to model
        model.addAttribute("workouts", workouts);
        model.addAttribute("meals", meals);
        model.addAttribute("userId", userId);
        model.addAttribute("userName", sessionUser.getUserName());
        model.addAttribute("currentWorkoutPage", workoutPage);
        model.addAttribute("totalWorkoutPages", totalWorkoutPages);
        model.addAttribute("currentMealPage", mealPage);
        model.addAttribute("totalMealPages", totalMealPages);

        return "workouts-meals"; // Thymeleaf template name
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
/*
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
    }*/

    /*
    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }

        // Get all users
        List<User> allUsers = userService.findAll();
        if (allUsers == null) {
            allUsers = Collections.emptyList();
        }

        // Get the current user's friends
        List<User> filteredUsers = userService.findNonFriends(user.getUserId(), FriendRequestStatus.PENDING);
        if (filteredUsers == null) {
            filteredUsers = Collections.emptyList();
        }


        model.addAttribute("users", filteredUsers);
        model.addAttribute("currentUserId", user.getUserId());
        //model.addAttribute("isFriendMap", isFriendMap);
        model.addAttribute("userForm", new User());
        return "user-list";
    }*/
    @GetMapping("/users")
    public String redirectFindFriends(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        return "redirect:/users/friends/" + user.getUserId() + "?tab=find-friends";
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
    /*
    @GetMapping("/users/pending")
    //public String listPendingRequests(@ModelAttribute("user") User theUser,HttpSession session,Model model) {
    public String showPendingRequests(HttpSession session,Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        try {
            List<FriendRequest> pendingRequests = friendService.getPendingRequests(user.getUserId());
            /*for (FriendRequest request : pendingRequests) {
                User sender = userService.findById(request.getSenderId());
                //request.setSenderName(sender != null ? sender.getUserName() : "Unknown");
                //request.setSenderEmail(sender != null ? sender.getUserEmail() : "Unknown");
            }
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
    }*/
/*
    @PostMapping("/pending")
    public String listPendingRequests(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
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
    }*/



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
    public String showDashboard( @RequestParam(defaultValue = "1") int workoutPage,
                                @RequestParam(defaultValue = "1") int mealPage,@RequestParam(defaultValue = "1") int postPage,HttpSession session, Model model){


        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }

        int userId = user.getUserId();
        int pageSize = 5;



        //Posts
        List<Post> posts = postService.getPostsByUserId(userId, postPage, pageSize);
        long totalPosts = postService.countPostsByUserId(userId);
        int totalPostPages = (int) Math.ceil((double) totalPosts / pageSize);
        model.addAttribute("posts",posts);
        model.addAttribute("currentPostPage", postPage);
        model.addAttribute("totalPostPages", totalPostPages);

        // Workouts
        List<Workout> workouts = workoutService.getWorkoutsByUserId(userId, workoutPage, pageSize);
        long totalWorkouts = workoutService.countWorkoutsByUserId(userId);
        int totalWorkoutPages = (int) Math.ceil((double) totalWorkouts / pageSize);
        model.addAttribute("workouts", workouts);
        model.addAttribute("currentWorkoutPage", workoutPage);
        model.addAttribute("totalWorkoutPages", totalWorkoutPages);

        // Meals
        List<Meal> meals = mealService.getMealsByUserId(userId, mealPage, pageSize);
        long totalMeals = mealService.countMealsByUserId(userId);
        int totalMealPages = (int) Math.ceil((double) totalMeals / pageSize);
        model.addAttribute("meals", meals);
        model.addAttribute("currentMealPage", mealPage);
        model.addAttribute("totalMealPages", totalMealPages);

        long mealCount = mealService.getWeeklyMealCount(userId);
        model.addAttribute("weeklyMeals", mealCount);

        long workoutCount = workoutService.getWeeklyWorkoutCount(userId);
        model.addAttribute("weeklyWorkouts", workoutCount);


        long postCount = postService.getWeeklyPostCount(userId);
        model.addAttribute("weeklyPosts", postCount);

        double totalCalories = mealService.getTodaysCalories(userId);
        model.addAttribute("todayCalories", totalCalories);


        String name = userService.findById(userId).getUserName();

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("userName", name);
        //model.addAttribute("userName", sessionUser.getUserName());

        List<User> friends = friendService.getFriends(userId);
        model.addAttribute("friends", friends);

        List<FriendRequest> pendingRequests = friendService.getPendingRequests(userId);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("pendingRequestsCount", pendingRequests.size());

        //return "test2";
        return "dashboard";
    }

    @GetMapping("/dashboard2")
    public String showDashboard2( @RequestParam(defaultValue = "1") int workoutPage,
                                 @RequestParam(defaultValue = "1") int mealPage,@RequestParam(defaultValue = "1") int postPage,HttpSession session, Model model){
        /*
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
        // Placeholder for dashboard attributes (replace with actual service calls)
        model.addAttribute("todayCalories", 0);
        model.addAttribute("weeklyWorkouts", 0);
        model.addAttribute("activeChallenges", 0);
        model.addAttribute("currentStreak", 0);
        model.addAttribute("recentWorkouts", Collections.emptyList());
        model.addAttribute("activeChallengesList", Collections.emptyList());
        return "dashboard";*/

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }

        int userId = user.getUserId();
        int pageSize = 10;

        // Workouts
        List<Post> posts = postService.getPostsByUserId(userId, postPage, pageSize);
        long totalPosts = postService.countPostsByUserId(userId);
        int totalPostPages = (int) Math.ceil((double) totalPosts / pageSize);

        // Workouts
        List<Workout> workouts = workoutService.getWorkoutsByUserId(userId, workoutPage, pageSize);
        long totalWorkouts = workoutService.countWorkoutsByUserId(userId);
        int totalWorkoutPages = (int) Math.ceil((double) totalWorkouts / pageSize);

        // Meals
        List<Meal> meals = mealService.getMealsByUserId(userId, mealPage, pageSize);
        long totalMeals = mealService.countMealsByUserId(userId);
        int totalMealPages = (int) Math.ceil((double) totalMeals / pageSize);

        List<FriendRequest> pendingRequests = friendService.getPendingRequests(userId);

        String name = userService.findById(userId).getUserName();
        // Add to model
        model.addAttribute("workouts", workouts);
        model.addAttribute("meals", meals);
        model.addAttribute("posts",posts);
        model.addAttribute("userId", userId);
        //model.addAttribute("userName", sessionUser.getUserName());

        model.addAttribute("currentPostPage", postPage);
        model.addAttribute("totalPostPages", totalPostPages);

        model.addAttribute("currentWorkoutPage", workoutPage);
        model.addAttribute("totalWorkoutPages", totalWorkoutPages);
        model.addAttribute("currentMealPage", mealPage);
        model.addAttribute("totalMealPages", totalMealPages);



        List<User> friends = friendService.getFriends(userId);
        model.addAttribute("friends", friends);
        model.addAttribute("user", user);
        model.addAttribute("userName", name);

        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("pendingRequestsCount", pendingRequests.size());

        return "test2";
        //return "dashboard";
    }



    @GetMapping("/dashboard/{id}")
    public String showDashboardForUser(@PathVariable("id") int userId , @RequestParam(defaultValue = "1") int workoutPage,
                                       @RequestParam(defaultValue = "1") int mealPage,@RequestParam(defaultValue = "1") int postPage,HttpSession session, Model model) {



            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/users/login";
            }

            User viewUser = userService.findById(userId);
            String viewUserName = viewUser.getUserName();
            //int userId = user.getUserId();
            int pageSize = 5;



            //Posts
            List<Post> posts = postService.getPostsByUserId(userId, postPage, pageSize);
            long totalPosts = postService.countPostsByUserId(userId);
            int totalPostPages = (int) Math.ceil((double) totalPosts / pageSize);
            model.addAttribute("posts",posts);
            model.addAttribute("currentPostPage", postPage);
            model.addAttribute("totalPostPages", totalPostPages);

            // Workouts
            List<Workout> workouts = workoutService.getWorkoutsByUserId(userId, workoutPage, pageSize);
            long totalWorkouts = workoutService.countWorkoutsByUserId(userId);
            int totalWorkoutPages = (int) Math.ceil((double) totalWorkouts / pageSize);
            model.addAttribute("workouts", workouts);
            model.addAttribute("currentWorkoutPage", workoutPage);
            model.addAttribute("totalWorkoutPages", totalWorkoutPages);

            // Meals
            List<Meal> meals = mealService.getMealsByUserId(userId, mealPage, pageSize);
            long totalMeals = mealService.countMealsByUserId(userId);
            int totalMealPages = (int) Math.ceil((double) totalMeals / pageSize);
            model.addAttribute("meals", meals);
            model.addAttribute("currentMealPage", mealPage);
            model.addAttribute("totalMealPages", totalMealPages);

            long mealCount = mealService.getWeeklyMealCount(userId);
            model.addAttribute("weeklyMeals", mealCount);

            long workoutCount = workoutService.getWeeklyWorkoutCount(userId);
            model.addAttribute("weeklyWorkouts", workoutCount);


            long postCount = postService.getWeeklyPostCount(userId);
            model.addAttribute("weeklyPosts", postCount);

            double totalCalories = mealService.getTodaysCalories(userId);
            model.addAttribute("todayCalories", totalCalories);


            String name = userService.findById(userId).getUserName();

            model.addAttribute("user", viewUser);
            model.addAttribute("userId", userId);
            model.addAttribute("userName", viewUserName);


            List<User> friends = friendService.getFriends(userId);
            model.addAttribute("friends", friends);

            List<FriendRequest> pendingRequests = friendService.getPendingRequests(userId);
            //model.addAttribute("pendingRequests", pendingRequests);
            //model.addAttribute("pendingRequestsCount", pendingRequests.size());

            //return "test2";
            return "dashboard";
        }

    @GetMapping("/dashboard2/{id}")
    public String showDashboard2ForUser(@PathVariable("id") int userId , @RequestParam(defaultValue = "1") int workoutPage,
    @RequestParam(defaultValue = "1") int mealPage,@RequestParam(defaultValue = "1") int postPage,HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }


            int pageSize = 10;

        // Workouts
            List<Post> posts = postService.getPostsByUserId(userId, postPage, pageSize);
            long totalPosts = postService.countPostsByUserId(userId);
            int totalPostPages = (int) Math.ceil((double) totalPosts / pageSize);

            // Workouts
            List<Workout> workouts = workoutService.getWorkoutsByUserId(userId, workoutPage, pageSize);
            long totalWorkouts = workoutService.countWorkoutsByUserId(userId);
            int totalWorkoutPages = (int) Math.ceil((double) totalWorkouts / pageSize);

            // Meals
            List<Meal> meals = mealService.getMealsByUserId(userId, mealPage, pageSize);
            long totalMeals = mealService.countMealsByUserId(userId);
            int totalMealPages = (int) Math.ceil((double) totalMeals / pageSize);

            String name = userService.findById(userId).getUserName();
            // Add to model
            model.addAttribute("workouts", workouts);
            model.addAttribute("meals", meals);
            model.addAttribute("posts",posts);
            model.addAttribute("userId", userId);
            //model.addAttribute("userName", sessionUser.getUserName());

            model.addAttribute("currentPostPage", postPage);
            model.addAttribute("totalPostPages", totalPostPages);

            model.addAttribute("currentWorkoutPage", workoutPage);
            model.addAttribute("totalWorkoutPages", totalWorkoutPages);
            model.addAttribute("currentMealPage", mealPage);
            model.addAttribute("totalMealPages", totalMealPages);



        List<User> friends = friendService.getFriends(userId);
        model.addAttribute("friends", friends);
        model.addAttribute("user", user);
        model.addAttribute("userName", name);

        return "test2";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

    /*
    @PostMapping("/requests/{requestId}/accept")
    public String acceptFriendRequest(@PathVariable("requestId") int requestId) {
        friendService.acceptFriendRequest(requestId);
        return "redirect:/requests/pending";
    }*/

    @GetMapping("/friends")
    public String listFriends(
        //@PathVariable("id") int userId,
        @RequestParam(defaultValue = "1") int friendPage,
        @RequestParam(defaultValue = "1") int pendingPage,
        @RequestParam(defaultValue = "1") int findFriendsPage,
        @RequestParam(defaultValue = "friends") String tab,
        HttpSession session, Model model) {

            // Verify user authorization
            User user = (User) session.getAttribute("user");
            int userId = user.getUserId();
            if (user == null || user.getUserId() != userId) {
                return "redirect:/users/login";
            }

            int pageSize = 10;

            // Friends
            List<User> friends = friendService.getFriends(userId, friendPage, pageSize);
            long totalFriends = friendService.countFriends(userId);
            int totalFriendPages = (int) Math.ceil((double) totalFriends / pageSize);

            // Pending Friend Requests
            List<FriendRequest> pendingRequests = friendService.getPendingRequests(userId, pendingPage, pageSize);
            long totalPendingRequests = friendService.countPendingRequests(userId);
            int totalPendingPages = (int) Math.ceil((double) totalPendingRequests / pageSize);

            // Non-Friends (Find Friends)
            List<User> nonFriends = userService.findNonFriends(userId, FriendRequestStatus.PENDING, findFriendsPage, pageSize);
            long totalNonFriends = userService.countNonFriends(userId, FriendRequestStatus.PENDING);
            int totalFindFriendsPages = (int) Math.ceil((double) totalNonFriends / pageSize);

            // Add to model
            model.addAttribute("friends", friends);
            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("nonFriends", nonFriends);
            model.addAttribute("userId", userId);
            model.addAttribute("user", user);
            model.addAttribute("userName", user.getUserName());
            model.addAttribute("currentFriendPage", friendPage);
            model.addAttribute("totalFriendPages", totalFriendPages);
            model.addAttribute("currentPendingPage", pendingPage);
            model.addAttribute("totalPendingPages", totalPendingPages);
            model.addAttribute("currentFindFriendsPage", findFriendsPage);
            model.addAttribute("totalFindFriendsPages", totalFindFriendsPages);
            model.addAttribute("activeTab", tab);

            return "friends"; // New Thymeleaf template name
        }

    @PostMapping("/friends")
    public String listFriendsPost(@ModelAttribute("user") User theUser, Model model) {
        int currentUserId = theUser.getUserId();
        List<User> friends = friendService.getFriends(currentUserId);
        model.addAttribute("friends", friends);
        return "friends-list";
    }

    /*
    @PostMapping("/friends")
    public String listFriends(@ModelAttribute("user") User theUser,Model model) {
        int currentUserId = theUser.getUserId();
        List<User> friends = friendService.getFriends(currentUserId);
        model.addAttribute("friends", friends);
        return "friends-list";
    }*/

    @GetMapping("/friends/{id}")
    public String listFriendsAndRequests(
            @PathVariable("id") int userId,
            @RequestParam(defaultValue = "1") int friendPage,
            @RequestParam(defaultValue = "1") int pendingPage,
            @RequestParam(defaultValue = "1") int findFriendsPage,
            @RequestParam(defaultValue = "friends") String tab,
            HttpSession session, Model model) {

        // Verify user authorization
        User user = (User) session.getAttribute("user");
        if (user == null || user.getUserId() != userId) {
            return "redirect:/users/login";
        }

        int pageSize = 10;

        // Friends
        List<User> friends = friendService.getFriends(userId, friendPage, pageSize);
        long totalFriends = friendService.countFriends(userId);
        int totalFriendPages = (int) Math.ceil((double) totalFriends / pageSize);

        // Pending Friend Requests
        List<FriendRequest> pendingRequests = friendService.getPendingRequests(userId, pendingPage, pageSize);
        long totalPendingRequests = friendService.countPendingRequests(userId);
        int totalPendingPages = (int) Math.ceil((double) totalPendingRequests / pageSize);

        // Non-Friends (Find Friends)
        List<User> nonFriends = userService.findNonFriends(userId, FriendRequestStatus.PENDING, findFriendsPage, pageSize);
        long totalNonFriends = userService.countNonFriends(userId, FriendRequestStatus.PENDING);
        int totalFindFriendsPages = (int) Math.ceil((double) totalNonFriends / pageSize);

        // Add to model
        model.addAttribute("friends", friends);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("nonFriends", nonFriends);
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        model.addAttribute("userName", user.getUserName());
        model.addAttribute("currentFriendPage", friendPage);
        model.addAttribute("totalFriendPages", totalFriendPages);
        model.addAttribute("currentPendingPage", pendingPage);
        model.addAttribute("totalPendingPages", totalPendingPages);
        model.addAttribute("currentFindFriendsPage", findFriendsPage);
        model.addAttribute("totalFindFriendsPages", totalFindFriendsPages);
        model.addAttribute("activeTab", tab);

        return "friends"; // New Thymeleaf template name
    }

    @PostMapping("/{id}/friend-request")
    public String sendFriendRequest(HttpSession session, @PathVariable("id") int receiverId, Model model,
                                    @RequestParam(defaultValue = "1") int friendPage,
                                    @RequestParam(defaultValue = "1") int pendingPage,
                                    @RequestParam(defaultValue = "1") int findFriendsPage,
                                    @RequestParam(defaultValue = "find-friends") String tab) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        int currentUserId = user.getUserId();
        try {
            friendService.sendFriendRequest(currentUserId, receiverId);
            return "redirect:/users/friends/" + currentUserId + "?friendPage=" + friendPage + "&pendingPage=" + pendingPage + "&findFriendsPage=" + findFriendsPage + "&tab=" + tab;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return listFriendsAndRequests(currentUserId, friendPage, pendingPage, findFriendsPage, tab,session, model);

        }
    }
    @PostMapping("/requests/{id}/accept")
    public String acceptFriendRequest(@PathVariable("id") int requestId, HttpSession session, Model model,
                                        @RequestParam(defaultValue = "1") int friendPage,
                                        @RequestParam(defaultValue = "1") int pendingPage,
                                        @RequestParam(defaultValue = "1") int findFriendsPage,
                                        @RequestParam(defaultValue = "pending") String tab) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        try {
            friendService.acceptFriendRequest(requestId);
            return "redirect:/users/friends/" + user.getUserId() + "?friendPage=" + friendPage + "&pendingPage=" + pendingPage + "&findFriendsPage=" + findFriendsPage + "&tab=" + tab;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return listFriendsAndRequests(user.getUserId(), friendPage, pendingPage, findFriendsPage,tab, session, model);
        }
    }

    @PostMapping("/requests/{id}/reject")
    public String rejectFriendRequest(@PathVariable("id") int requestId, HttpSession session, Model model,
                                      @RequestParam(defaultValue = "1") int friendPage,
                                      @RequestParam(defaultValue = "1") int pendingPage,
                                      @RequestParam(defaultValue = "1") int findFriendsPage,
                                      @RequestParam(defaultValue = "pending") String tab) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        try {
            friendService.rejectFriendRequest(requestId);
            return "redirect:/users/friends/" + user.getUserId() + "?friendPage=" + friendPage + "&pendingPage=" + pendingPage + "&findFriendsPage=" + findFriendsPage + "&tab=" + tab;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return listFriendsAndRequests(user.getUserId(), friendPage, pendingPage, findFriendsPage,tab, session, model);
        }
    }
    @PostMapping("/friends/{id}/delete")
    public String deleteFriend(@PathVariable("id") int friendId, HttpSession session, Model model,
                               @RequestParam(defaultValue = "1") int friendPage,
                               @RequestParam(defaultValue = "1") int pendingPage,
                               @RequestParam(defaultValue = "1") int findFriendsPage,
                               @RequestParam(defaultValue = "friends") String tab) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        int currentUserId = user.getUserId();
        try {
            friendService.deleteFriend(currentUserId, friendId);
            return "redirect:/users/friends/" + currentUserId + "?friendPage=" + friendPage + "&pendingPage=" + pendingPage + "&findFriendsPage=" + findFriendsPage + "&tab=" + tab;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return listFriendsAndRequests(currentUserId, friendPage, pendingPage, findFriendsPage, tab,session, model);
        }
    }
}
