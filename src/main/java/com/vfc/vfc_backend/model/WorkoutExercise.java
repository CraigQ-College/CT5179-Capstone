package com.vfc.vfc_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "workout_exercise1")
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workout_exercise_id")
    private int workoutExerciseId;



    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @Column(name = "exercise_type")
    String exerciseType ;

    @Column(name = "cardio_exercise_name")
    String cardioExerciseName;

    @Column(name = "weight_exercise_name")
    String weightExerciseName;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "distance")
    private Float distance;

    @Column(name = "calories_burned")
    private Integer caloriesBurned;

    @Column(name = "set_number")
    private Integer setNumber;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "reps")
    private Integer reps;

    // Default constructor
    public WorkoutExercise() {
    }


    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public int getWorkoutExerciseId() {
        return workoutExerciseId;
    }

    public void setWorkoutExerciseId(int workoutExerciseId) {
        this.workoutExerciseId = workoutExerciseId;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public Integer getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(Integer setNumber) {
        this.setNumber = setNumber;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getCardioExerciseName() {
        return cardioExerciseName;
    }

    public void setCardioExerciseName(String cardioExerciseName) {
        this.cardioExerciseName = cardioExerciseName;
    }

    public String getWeightExerciseName() {
        return weightExerciseName;
    }

    public void setWeightExerciseName(String weightExerciseName) {
        this.weightExerciseName = weightExerciseName;
    }

    @Override
    public String toString() {
        return "WorkoutExercise{" +
                "workoutExerciseId=" + workoutExerciseId +

                ", durationMinutes=" + durationMinutes +
                ", distance=" + distance +
                ", caloriesBurned=" + caloriesBurned +
                ", setNumber=" + setNumber +
                ", weight=" + weight +
                ", reps=" + reps +
                '}';
    }
}
