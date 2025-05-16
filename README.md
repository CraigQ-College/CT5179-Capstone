# Virtual Fitness Community (VFC) Backend

This is the backend service for the Virtual Fitness Community (VFC) application, a cloud-based platform designed to help users track workouts, join fitness challenges, sync data from external apps, and engage with a fitness community. The backend is built with Spring Boot and integrates with Supabase (PostgreSQL) for data storage and authentication.

## Features
- User registration, login, and profile management
- Workout logging and fitness tracking
- Community-driven challenges and real-time leaderboards
- Integration with external fitness APIs (e.g., Strava, Fitbit)
- Automated notifications and reminders
- Admin dashboard for platform management
- Scalable, cloud-native infrastructure

## Tech Stack
- **Backend:** Java, Spring Boot
- **Database:** Supabase (PostgreSQL)
- **API Integration:** RESTful APIs (Strava, Fitbit, etc.)
- **DevOps:** Docker, Jenkins, AWS EC2
- **Authentication:** JWT, Supabase Auth

## Project Structure
See [`PROJECT_STRUCTURE.md`](./PROJECT_STRUCTURE.md) for a detailed breakdown of the folder and file organization.

## Setup Instructions
1. **Clone the repository:**
   ```bash
   git clone <repo-url>
   cd vfc-backend
   ```
2. **Configure Supabase connection:**
   - Get your Supabase database credentials from your Supabase project dashboard:
     - Go to **Settings** → **Database** → **Connection string**
     - If you need to reset your password, do so in the same section
   - Add the following to `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:postgresql://db.qeztinzrtdvcbbvlywdt.supabase.co:5432/postgres
     spring.datasource.username=postgres
     spring.datasource.password=CT5179Capstone!
     spring.jpa.hibernate.ddl-auto=update
     ```
   - **Important:** Do NOT commit your real password to version control. Use environment variables or a secrets manager for production.
3. **Build and run the backend:**
   - **Mac/Linux:**
     ```bash
     ./mvnw spring-boot:run
     ```
   - **Windows:**
     ```cmd
     mvnw.cmd spring-boot:run
     ```
   - Or use your IDE's Spring Boot run configuration.

## Contributing
- Use feature branches for new work (e.g., `feature/user-auth`, `feature/challenges`)
- Open pull requests for code review before merging to `main`
- Follow the folder structure and naming conventions in `PROJECT_STRUCTURE.md`
- Document major changes in the README or as comments in your code

## Team
- Conor Daly
- Craig Quinn
- James Sheridan

---
For any questions, check the documentation or contact a team member. 