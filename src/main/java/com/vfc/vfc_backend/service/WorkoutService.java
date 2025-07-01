package com.vfc.vfc_backend.service;

import com.vfc.vfc_backend.model.Workout;
import com.vfc.vfc_backend.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    public List<Workout> getWorkoutsByUserId(int userId) {
        return workoutRepository.findByUserId(userId);
    }

    public List<Workout> findWorkoutsByUserId(int userId) {
        return workoutRepository.findByUserId(userId);
    }
    public Workout getWorkoutById(int id) {
        return workoutRepository.findById(id).orElseThrow(() -> new RuntimeException("Workout not found"));
    }

    public void save(Workout workout) {
        // Ensure workoutExercises are linked to the workout
        if (workout.getWorkoutExercises() != null) {
            for (var exercise : workout.getWorkoutExercises()) {
                exercise.setWorkout(workout); // Link each WorkoutExercise to the Workout
            }
        }
        workoutRepository.save(workout); // Cascades to save WorkoutExercise entities
    }

    public Workout findById(int workoutId) {
        return workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));
    }


    public void deleteById(int theId) {
        workoutRepository.deleteById(theId);
    }

    public List<Workout> getLastThreeWorkoutsByUserId(int userId) {
        Pageable pageable = PageRequest.of(0, 3); // Get first page with 3 results
        return workoutRepository.findTop3ByUserIdOrderByWorkoutDateDesc(userId, pageable);
    }
}
