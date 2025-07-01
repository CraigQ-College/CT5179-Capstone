package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.*;
import com.vfc.vfc_backend.service.MealService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/meals")
public class MealController {

    @Autowired
    private MealService mealService;

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
            return "redirect:/users/dashboard";
        }

    @GetMapping("/listMeals")
    public String listMeal (@ModelAttribute("meal") Meal theMeal, HttpSession session, Model model){

        User user = (User) session.getAttribute("user");

        int userId = user.getUserId();

        mealService.saveMeal(theMeal);
        model.addAttribute("userId", theMeal.getUserId());
        return "redirect:/users/dashboard";
    }

}
