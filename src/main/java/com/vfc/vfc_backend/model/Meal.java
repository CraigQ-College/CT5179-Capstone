package com.vfc.vfc_backend.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private int mealId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "meal_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime mealDate;

    @Column(name = "notes")
    private String mealNotes;

    public List<MealFood> getMealFoods() {
        return mealFoods;
    }

    public void setMealFoods(List<MealFood> mealFoods) {
        this.mealFoods = mealFoods;
    }

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealFood> mealFoods = new ArrayList<>();

    public Meal() {
    }

    public Meal(int userId, LocalDateTime mealDate, String mealNotes) {
        this.userId = userId;
        this.mealDate = mealDate;
        this.mealNotes = mealNotes;
    }

    public Meal(int mealId, int userId, LocalDateTime mealDate, String mealNotes) {
        this.mealId = mealId;
        this.userId = userId;
        this.mealDate = mealDate;
        this.mealNotes = mealNotes;
    }



    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDateTime mealDateDate) {
        this.mealDate = mealDateDate;
    }

    public String getMealNotes() {
        return mealNotes;
    }

    public void setMealNotes(String mealNotes) {
        this.mealNotes = mealNotes;
    }

}


