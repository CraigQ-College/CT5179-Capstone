package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.User;
import com.vfc.vfc_backend.model.Workout;
import com.vfc.vfc_backend.model.WorkoutExercise;
import com.vfc.vfc_backend.service.WorkoutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

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


    @GetMapping("/listWorkouts")
    public String listWorkouts(HttpSession session, Model model) {

            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/login";
            }

        List<Workout> workouts = workoutService.getWorkoutsByUserId(user.getUserId());
        model.addAttribute("workouts", workouts);
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("userName", user.getUserName());
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
    public String viewWorkout(@PathVariable("workoutId") int workoutId, Model model) {
        Workout workout = workoutService.getWorkoutById(workoutId);
        model.addAttribute("workout", workout);
        return "view-workout";
    }
}
