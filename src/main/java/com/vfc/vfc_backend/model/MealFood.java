package com.vfc.vfc_backend.model;


import jakarta.persistence.*;


@Entity
@Table(name = "meal_foods")
public class MealFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_food_id")
    private int mealFoodId;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "meal_id")
    private int mealId;*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    @Column(name = "meal_food_calories")

    private double mealFoodCalories;

    @Column(name = "meal_food_name")
    private String mealFoodName;

    public MealFood() {
    }



    public MealFood(Meal meal, double mealFoodCalories, String mealFoodName) {
        this.meal = meal;
        this.mealFoodCalories = mealFoodCalories;
        this.mealFoodName = mealFoodName;
    }

    public int getMealFoodId() {
        return mealFoodId;
    }

    public void setMealFoodId(int mealFoodId) {
        this.mealFoodId = mealFoodId;
    }
/*
    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }*/

    public double getMealFoodCalories() {
        return mealFoodCalories;
    }

    public void setMealFoodCalories(double mealfoodCalories) {
        this.mealFoodCalories = mealfoodCalories;
    }

    public String getMealFoodName() {
        return mealFoodName;
    }

    public void setMealFoodName(String mealFoodName) {
        this.mealFoodName = mealFoodName;
    }
}
