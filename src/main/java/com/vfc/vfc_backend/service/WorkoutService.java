package com.vfc.vfc_backend.service;

import com.vfc.vfc_backend.model.Workout;
import com.vfc.vfc_backend.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    public WorkoutService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public List<Workout> getWorkoutsByUserId(int userId) {
        return workoutRepository.findByUserId(userId);
    }

    public List<Workout> findWorkoutsByUserId(int userId) {
        return workoutRepository.findByUserId(userId);
    }
    public Workout getWorkoutById(int id) {
        return workoutRepository.findById(id).orElseThrow(() -> new RuntimeException("Workout not found"));
    }

    public List<Workout> getWorkoutsByUserId(int userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // Spring uses 0-based indexing
        return workoutRepository.findByUserIdOrderByWorkoutDateDesc(userId, pageable).getContent();
    }

    public long countWorkoutsByUserId(int userId) {
        return workoutRepository.countByUserId(userId);
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

    public long getWeeklyWorkoutCount(int userId) {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Ensure start of week is Monday
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate weekStart = today.with(weekFields.dayOfWeek(), 1);
        LocalDateTime startDateTime = weekStart.atStartOfDay();
        LocalDateTime endDateTime = weekStart.plusDays(6).atTime(23, 59, 59);

        // Query workouts in the date range
        List<Workout> workouts = workoutRepository.findByUserIdAndDateRange(userId, startDateTime, endDateTime);
        return workouts.size();
    }

    public Workout findByIdWithExercises(int workoutId) {
        return workoutRepository.findByIdWithExercises(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));
    }
}
