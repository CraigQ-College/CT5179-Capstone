package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.*;
import com.vfc.vfc_backend.service.MealService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/meals")
public class MealController {

    @Autowired
    private MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping("/addMeal")
    public String createMeal(@ModelAttribute("meal") Meal theMeal,HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        int userId = user.getUserId();

        //model.addAttribute("userId",userId);
        Meal meal = new Meal();
        meal.setUserId(userId);
        model.addAttribute("meal", meal);
        model.addAttribute("user", user);
        //List<String> exerciseTypes = exerciseService.findDistinctTypes();
        //model.addAttribute("exerciseTypes", exerciseTypes != null ? exerciseTypes : new ArrayList<>());
        return "log-meal";

    }
        @PostMapping("/saveMeal")
        public String saveMeal (@ModelAttribute("meal") Meal theMeal, HttpSession session, Model model){

            for (MealFood mf : theMeal.getMealFoods()) {
                mf.setMeal( theMeal);
            }
            mealService.saveMeal(theMeal);
            model.addAttribute("userId", theMeal.getUserId());
            return "redirect:/meals/listMeals";
        }

    /*@GetMapping("/listMeals")
    public String listMeal (@ModelAttribute("meal") Meal theMeal, HttpSession session, Model model){

        User user = (User) session.getAttribute("user");

        int userId = user.getUserId();

        mealService.saveMeal(theMeal);
        model.addAttribute("userId", theMeal.getUserId());
        return "redirect:/users/dashboard";
    }*/

    /*
    @GetMapping("/listMeals")
    public String listWorkouts(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Meal> meals = mealService.getMealsByUserId(user.getUserId());
        model.addAttribute("meals", meals);
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("userName", user.getUserName());
        return "list-meals";
    }*/

    @GetMapping("/listMeals")
    public String showDashboard2ForUser(@RequestParam(defaultValue = "1") int mealPage,HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        String name = user.getUserName();
        int userId = user.getUserId();

        int pageSize = 4;

        //Pageable pageable = PageRequest.of(mealPage - 1, pageSize, Sort.by("mealDate").descending());
        //Page<Meal> mealPageResult = mealService.findMealsByUserId(userId, pageable);

        // Meals
        List<Meal> meals = mealService.getMealsByUserId(userId, mealPage, pageSize);
        long totalMeals = mealService.countMealsByUserId(userId);
        int totalMealPages = (int) Math.ceil((double) totalMeals / pageSize);


        // Add to model

        model.addAttribute("meals", meals);
        model.addAttribute("userId", userId);
        //model.addAttribute("userName", sessionUser.getUserName());
        model.addAttribute("currentMealPage", mealPage);
        model.addAttribute("totalMealPages", totalMealPages);

        model.addAttribute("user", user);
        model.addAttribute("userName", name);

        return "list-meals";
    }

    @GetMapping("/delete/{mealId}")
    public String delete(@PathVariable("mealId") int mealId,Model theModel){

        Optional<Meal> meal = mealService.getMealById(mealId);
        //int userId =  meal.getUserId();
        //delete the employee
        mealService.deleteById(mealId);
        //redirect to the /employees/list
        return "redirect:/meals/listMeals";
        //return "list-workouts";
        //return "redirect:/workouts/workouts?userId=" + userId;
    }

    /*
    @GetMapping("/viewMeal/{mealId}")
    public String viewMeal(@PathVariable("mealId") int mealId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Optional<Meal> mealOpt = mealService.getMealById(mealId);
        if (mealOpt.isEmpty()) {
            return "redirect:/meals/listMeals";
        }
        Meal meal = mealOpt.get();
        LocalDate mealDate = meal.getMealDate().toLocalDate();
        List<Meal> mealsOnDate = mealService.getMealsByUserIdAndDate(meal.getUserId(), mealDate);

        // Calculate total calories for the day
        double totalCalories = mealsOnDate.stream()
                .flatMap(m -> m.getMealFoods().stream())
                .mapToDouble(MealFood::getMealFoodCalories)
                .sum();

        model.addAttribute("meal", meal);
        model.addAttribute("mealsOnDate", mealsOnDate);
        model.addAttribute("totalCalories", totalCalories);
        model.addAttribute("mealDate", mealDate);
        model.addAttribute("user", user);
        return "view-meal";
    }*/
    @GetMapping("/viewMeal/{id}")
    public String viewMeal(@PathVariable("id") int mealId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        Meal meal = mealService.getMealById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found with ID: " + mealId));
        LocalDate mealDate = meal.getMealDate().toLocalDate();
        List<Meal> mealsOnDate = mealService.getMealsByUserIdAndDate(user.getUserId(), mealDate);
        double dailyCalories = mealsOnDate.stream()
                .flatMap(m -> m.getMealFoods().stream())
                .mapToDouble(MealFood::getMealFoodCalories)
                .sum();
        //List<User> friends = friendService.getFriends(user.getUserId());

        model.addAttribute("meal", meal);
        model.addAttribute("mealDate", mealDate);
        model.addAttribute("dailyCalories", (int) dailyCalories); // Total calories for the meal's date
        model.addAttribute("user", user);
        //model.addAttribute("friends", friends); // For tribe section
        return "view-meal";
    }
}
