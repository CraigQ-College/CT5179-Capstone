# Project Structure Overview

This document explains the main folders in our Spring Boot backend project and gives examples of the types of files each will contain, tailored to the features of the Virtual Fitness Community (VFC) app. Please use this as a reference for organizing your code.

---

## Folder Descriptions

### `config/`
**Purpose:** Holds configuration classes for the application (e.g., security, database, general app settings).
**Example files:**
- `WebSecurityConfig.java` (JWT/Supabase security setup)
- `DatabaseConfig.java` (custom DB settings)
- `CorsConfig.java` (CORS policy for frontend-backend communication)

### `controller/`
**Purpose:** Contains REST API controllers that handle HTTP requests and responses.
**Example files:**
- `AuthController.java` (user registration, login, authentication endpoints)
- `UserController.java` (user profile management)
- `WorkoutController.java` (logging and retrieving workouts)
- `ChallengeController.java` (creating, joining, and tracking challenges)
- `LeaderboardController.java` (leaderboard endpoints)
- `NotificationController.java` (notifications and messaging)
- `AdminController.java` (admin dashboard and management)
- `ApiIntegrationController.java` (external API sync, e.g., Strava, Fitbit)

### `service/`
**Purpose:** Business logic and service classes. These coordinate between controllers and repositories.
**Example files:**
- `AuthService.java` (authentication and user session logic)
- `UserService.java` (user profile and settings logic)
- `WorkoutService.java` (workout logging and analytics)
- `ChallengeService.java` (challenge management and progress tracking)
- `LeaderboardService.java` (leaderboard calculation and updates)
- `NotificationService.java` (sending notifications and reminders)
- `AdminService.java` (admin-specific operations)
- `ApiIntegrationService.java` (syncing with external APIs)

### `repository/`
**Purpose:** Data access layer. Interfaces for interacting with the database (usually extends `JpaRepository`).
**Example files:**
- `UserRepository.java` (CRUD for users)
- `WorkoutRepository.java` (CRUD for workouts)
- `ChallengeRepository.java` (CRUD for challenges)
- `LeaderboardRepository.java` (leaderboard data)
- `NotificationRepository.java` (notification data)

### `model/`
**Purpose:** Data models/entities and DTOs. Represents the structure of data stored or transferred.
**Example files:**
- `User.java` (user entity)
- `Workout.java` (workout entity)
- `Challenge.java` (challenge entity)
- `LeaderboardEntry.java` (leaderboard entry entity)
- `Notification.java` (notification entity)
- `ApiToken.java` (external API token entity)
- `UserDTO.java`, `ChallengeDTO.java` (data transfer objects)

### `util/`
**Purpose:** Utility/helper classes and constants used throughout the project.
**Example files:**
- `JwtUtil.java` (JWT token helpers)
- `DateUtils.java` (date formatting helpers)
- `LeaderboardUtils.java` (helper methods for leaderboard calculations)
- `Constants.java` (application-wide constants)

### `VfcBackendApplication.java`
**Purpose:** Main entry point for the Spring Boot application. Contains the `main` method to start the backend.

---

**Tip:**
- Place each new file in the folder that best matches its purpose.
- If unsure, ask the team or check this document! 