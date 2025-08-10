package com.vfc.vfc_backend.repository;

import com.vfc.vfc_backend.model.Meal;
import com.vfc.vfc_backend.model.Workout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Integer> {

    @Query("SELECT m FROM Meal m WHERE m.userId = :userId ORDER BY m.mealDate DESC")
    List<Meal> findTop3ByUserIdOrderByMealDateDesc(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT w FROM Meal w WHERE w.userId = :userId ORDER BY w.mealDate DESC")
    List<Meal> findByUserId(@Param("userId") int userId);

    @Query("SELECT m FROM Meal m WHERE m.userId = :userId ORDER BY m.mealDate DESC")
    Page<Meal> findByUserId(@Param("userId") int userId, Pageable pageable);

    long countByUserId(int userId);

    @Query("SELECT m FROM Meal m JOIN FETCH m.mealFoods WHERE m.userId = :userId AND CAST(m.mealDate AS DATE) = :date ORDER BY m.mealDate DESC")
    List<Meal> findByUserIdAndDate(@Param("userId") int userId, @Param("date") LocalDate date);

    @Query("SELECT m FROM Meal m JOIN FETCH m.mealFoods WHERE m.userId = :userId AND m.mealDate BETWEEN :startDate AND :endDate ORDER BY m.mealDate DESC")
    List<Meal> findByUserIdAndDateRange(@Param("userId") int userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


}
