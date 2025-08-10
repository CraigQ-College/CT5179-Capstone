package com.vfc.vfc_backend.service;

import com.vfc.vfc_backend.model.Meal;
import com.vfc.vfc_backend.model.MealFood;
import com.vfc.vfc_backend.model.Workout;
import com.vfc.vfc_backend.repository.MealRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.time.temporal.WeekFields;

import java.util.Locale;

@Service
public class MealService {

    private  MealRepository mealRepository;

    @Autowired
    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public List<Meal> getLastThreeMealsByUserId(int userId) {
        Pageable pageable = PageRequest.of(0, 3); // Get first page with 3 results
        return mealRepository.findTop3ByUserIdOrderByMealDateDesc(userId, pageable);
    }

    @Transactional
    public Meal saveMeal(Meal mealDTO) {
        // Create Meal entity
        Meal meal = new Meal(
                mealDTO.getUserId(),
                mealDTO.getMealDate(),
                mealDTO.getMealNotes()
        );

        // Add associated MealFood entities
        if (mealDTO.getMealFoods() != null) {
            List<MealFood> mealFoods = mealDTO.getMealFoods().stream()
                    .map(foodDTO -> new MealFood(
                            meal,
                            foodDTO.getMealFoodCalories(),
                            foodDTO.getMealFoodName()
                    ))
                    .collect(Collectors.toList());
            meal.getMealFoods().addAll(mealFoods);
        }

        return mealRepository.save(meal);
    }

    public Optional<Meal> getMealById(int mealId) {
        return mealRepository.findById(mealId);
    }

    public List<Meal> getAllMealsByUserId(int userId) {
        return mealRepository.findAll().stream()
                .filter(meal -> meal.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(int mealId) {
        if (!mealRepository.existsById(mealId)) {
            throw new RuntimeException("Meal not found with ID: " + mealId);
        }
        mealRepository.deleteById(mealId); // Cascades to delete associated MealFood entries
    }

    @Transactional
    public Meal updateMeal(int mealId, Meal mealDTO) {
        Meal existingMeal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found with ID: " + mealId));

        // Update meal fields
        existingMeal.setUserId(mealDTO.getUserId());
        existingMeal.setMealDate(mealDTO.getMealDate());
        existingMeal.setMealNotes(mealDTO.getMealNotes());

        // Clear and update associated meal foods
        existingMeal.getMealFoods().clear();
        if (mealDTO.getMealFoods() != null) {
            List<MealFood> mealFoods = mealDTO.getMealFoods().stream()
                    .map(foodDTO -> new MealFood(
                            existingMeal,
                            foodDTO.getMealFoodCalories(),
                            foodDTO.getMealFoodName()
                    ))
                    .collect(Collectors.toList());
            existingMeal.getMealFoods().addAll(mealFoods);
        }

        return mealRepository.save(existingMeal);
    }

    public List<Meal> getMealsByUserId(int userId, int mealPage, int pageSize) {

        Pageable pageable = PageRequest.of(mealPage - 1, pageSize); // Spring uses 0-based indexing
        return mealRepository.findByUserId(userId, pageable).getContent();
    }

    public List<Meal> getMealsByUserId(int userId) {

        //Pageable pageable = PageRequest.of(mealPage - 1, pageSize); // Spring uses 0-based indexing
        return mealRepository.findByUserId(userId);
    }

    public long countMealsByUserId(int userId) {
        return mealRepository.countByUserId(userId);
    }

    public List<Meal> getMealsByUserIdAndDate(int userId, LocalDate date) {
        return mealRepository.findByUserIdAndDate(userId, date);
    }

    public double getTodaysCalories(int userId) {
        LocalDate today = LocalDate.now();
        List<Meal> meals = mealRepository.findByUserIdAndDate(userId, today);

        return meals.stream()
                .flatMap(meal -> meal.getMealFoods().stream())
                .mapToDouble(MealFood::getMealFoodCalories)
                .sum();
    }

    public long getWeeklyMealCount(int userId) {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Ensure start of week is Monday
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate weekStart = today.with(weekFields.dayOfWeek(), 1);
        LocalDateTime startDateTime = weekStart.atStartOfDay();
        LocalDateTime endDateTime = weekStart.plusDays(6).atTime(23, 59, 59);

        // Query meals in the date range
        List<Meal> meals = mealRepository.findByUserIdAndDateRange(userId, startDateTime, endDateTime);
        return meals.size();
    }
}
