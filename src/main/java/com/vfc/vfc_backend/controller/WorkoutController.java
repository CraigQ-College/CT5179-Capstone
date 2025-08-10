package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.*;
import com.vfc.vfc_backend.service.WorkoutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/addWorkout")
    public String showFormForAddWorkout(@ModelAttribute("workout") Workout theWorkout, HttpSession session, Model model){

        User user = (User) session.getAttribute("user");
        //int senderId = userForm.getUserId();
        int userId = user.getUserId();
        // create model attribute to bind form data
        //Employee theEmployee = new Employee();
        model.addAttribute("userId",userId);
        Workout workout  = new Workout();
        workout.setUserId(userId);
        model.addAttribute("workout", workout);
        model.addAttribute("user", user);
        //List<String> exerciseTypes = exerciseService.findDistinctTypes();
        //model.addAttribute("exerciseTypes", exerciseTypes != null ? exerciseTypes : new ArrayList<>());
        return "log-workout";
    }

    @PostMapping("/saveWorkout")
    public String saveWorkout(@ModelAttribute("workout") Workout theWorkout, HttpSession session, Model model){

        for (WorkoutExercise we :theWorkout.getWorkoutExercises()) {
            we.setWorkout(theWorkout);
        }
        workoutService.save(theWorkout);
        model.addAttribute("userId", theWorkout.getUserId());
        return "redirect:/workouts/listWorkouts";
    }


/*
    @GetMapping("/listWorkouts")
    public String listWorkouts(HttpSession session, Model model) {

            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/login";
            }

        List<Workout> workouts = workoutService.getWorkoutsByUserId(user.getUserId());
        model.addAttribute("workouts", workouts);
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("userName", user.getUserName());
        return "list-workouts";
    }*/

    @GetMapping("/listWorkouts")
    public String listWorkouts( @RequestParam(defaultValue = "1") int workoutPage,
                                        HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        int userId = user.getUserId();
        int pageSize = 5;

        // Workouts
        List<Workout> workouts = workoutService.getWorkoutsByUserId(userId, workoutPage, pageSize);
        long totalWorkouts = workoutService.countWorkoutsByUserId(userId);
        int totalWorkoutPages = (int) Math.ceil((double) totalWorkouts / pageSize);



        String name = user.getUserName();
        // Add to model
        model.addAttribute("workouts", workouts);

        model.addAttribute("userId", userId);
        //model.addAttribute("userName", sessionUser.getUserName());

        model.addAttribute("currentWorkoutPage", workoutPage);
        model.addAttribute("totalWorkoutPages", totalWorkoutPages);

        model.addAttribute("user", user);
        model.addAttribute("userName", name);

        return "list-workouts";
    }

    @GetMapping("/workouts")
    public String showWorkouts(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Workout> workouts = workoutService.findWorkoutsByUserId(user.getUserId());
        model.addAttribute("workouts", workouts);
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("userName", user.getUserName());
        return "workouts"; // Thymeleaf template name
    }

    @GetMapping("/delete/{workoutId}")
    public String delete(@PathVariable("workoutId") int workoutId,Model theModel){

        Workout workout = workoutService.getWorkoutById(workoutId);
        int userId =  workout.getUserId();
        //delete the employee
        workoutService.deleteById(workoutId);
        //redirect to the /employees/list
        return "redirect:/workouts/listWorkouts";
        //return "list-workouts";
        //return "redirect:/workouts/workouts?userId=" + userId;
    }


    @GetMapping("/viewWorkout/{workoutId}")
    public String viewWorkout(@PathVariable("workoutId") int workoutId,HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        int userId = user.getUserId();

        Workout workout = workoutService.findByIdWithExercises(workoutId); // Fetch workout with exercises
        /*if (workout == null) {
            model.addAttribute("workout", null);
            model.addAttribute("user", userService.findById(0)); // Replace with actual user logic
            model.addAttribute("friends", userService.getFriends(0)); // Replace with actual user logic
            return "view-workout";
        }*/
        model.addAttribute("workout", workout);
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        return "view-workout";
    }

    /*
    @GetMapping("/viewWorkout/{id}")
    public String viewWorkout(@PathVariable("id") int workoutId, Model model) {
        Workout workout = workoutService.findByIdWithExercises(workoutId); // Use a method that fetches exercises
        if (workout == null) {
            model.addAttribute("workout", null);
            model.addAttribute("user", userService.findById(0)); // Replace with actual user logic
            model.addAttribute("friends", userService.getFriends(0)); // Replace with actual user logic
            return "view-workout";
        }

        // Calculate daily calories burned
        LocalDate workoutDate = workout.getWorkoutDate().toLocalDate();
        LocalDateTime startOfDay = workoutDate.atStartOfDay();
        LocalDateTime endOfDay = workoutDate.atTime(23, 59, 59);
        List<Workout> dailyWorkouts = workoutService.findByUserIdAndDateRange(
                workout.getUserId(), startOfDay, endOfDay);
        int dailyCaloriesBurned = dailyWorkouts.stream()
                .flatMap(w -> w.getWorkoutExercises().stream())
                .mapToInt(ex -> ex.getCaloriesBurned() != null ? ex.getCaloriesBurned() : 0)
                .sum();

        model.addAttribute("workout", workout);
        model.addAttribute("user", userService.findById(workout.getUserId()));
        //model.addAttribute("friends", userService.getFriends(workout.getUserId()));
        model.addAttribute("dailyCaloriesBurned", dailyCaloriesBurned);

        return "view-workout";
    }*/
}
