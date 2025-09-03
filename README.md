# TaskTracker

## Overview
TaskTracker is a simple team-based to-do list mobile application for Android. It uses Room database for persistence, LiveData for UI updates, and includes basic authentication and admin features.

### Edit
This project was originally created as part of a team assignment for an Android development course.
All commits after 09/03/2025 will be made by Janaye J as I want to build on this project for my portfolio.
The original repository can be found here: https://github.com/JanayeJackson/ElevateWebSolutionsAndroidStudio

---
## User Stories
- As a user, I want to log in so I can access my tasks
- As a user, I want to see a list of my tasks
- As a user, I want to add, edit, and delete tasks
- As an admin, I want to access admin-only features such as clearing all tasks and managing users
- As a user, I want the task list to update automatically
- As a developer, I want to test database and navigation logic

---
## PLANNED Layout

```mermaid
flowchart TD
    A[App Launch] --> B[LoginActivity]
    B -->|Valid Credentials| C[LandingActivity]
    B -->|Invalid Credentials| B[Show Error Toast]

    C --> D[TaskListActivity]
    C -->|Admin Only| E[Admin Controls Panel]

    D --> F[RecyclerView: Task List]
    F --> G[Click Task Item --> EditTaskActivity]

    G --> H[Update Task or Delete]

    C -->|Logout| B

```

---
## PLANNED Entity Layout
The assignment MUST use a database and must have at least three tables
```mermaid
erDiagram

    USER ||--o{ TASK : creates
    USER {
        INT userId PK
        STRING username
        STRING password
        BOOLEAN isAdmin
    }

    TASK {
        INT taskId PK
        STRING title
        STRING description
        STRING status
        INT assignedUserId FK
    }

    TASK ||--o{ COMMENT : has
    COMMENT {
        INT commentId PK
        INT taskId FK
        INT authorId FK
        STRING text
        DATETIME timestamp
    }

    USER ||--o{ COMMENT : writes

```
