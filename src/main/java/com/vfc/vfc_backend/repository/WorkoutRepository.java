package com.vfc.vfc_backend.repository;

import com.vfc.vfc_backend.model.Workout;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Integer> {

    @Query("SELECT w FROM Workout w WHERE w.userId = :userId")
    List<Workout> findByUserId(@Param("userId") int userId);

    @Query("SELECT w FROM Workout w LEFT JOIN FETCH w.workoutExercises WHERE w.workoutId = :workoutId")
    Optional<Workout> findByIdWithExercises(@Param("workoutId") int workoutId);

    @Query("SELECT w FROM Workout w WHERE w.userId = :userId ORDER BY w.workoutDate DESC")
    List<Workout> findTop3ByUserIdOrderByWorkoutDateDesc(@Param("userId") int userId, Pageable pageable);
}
