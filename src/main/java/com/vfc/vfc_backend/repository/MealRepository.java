package com.vfc.vfc_backend.repository;

import com.vfc.vfc_backend.model.Meal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Integer> {

    @Query("SELECT m FROM Meal m WHERE m.userId = :userId ORDER BY m.mealDate DESC")
    List<Meal> findTop3ByUserIdOrderByMealDateDesc(@Param("userId") int userId, Pageable pageable);
}
