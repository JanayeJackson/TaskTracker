# TaskTracker

## Overview
TaskTracker is a simple team-based to-do list mobile application for Android. It uses Room database for persistence, LiveData for UI updates, and includes basic authentication and admin features.

### Edit
This project was originally created as part of a team assignment for an Android development course.
All commits after 09/03/2025 will be made by Janaye J as I want to build on this project for my portfolio.
The original repository can be found here: https://github.com/JanayeJackson/ElevateWebSolutionsAndroidStudio
---
## Team Members
- **Janaye J**
- **Savannah K**
- **Isaiah S**
- **Xavier M**

---
## Rubric Strategy
### Required (110 pts)
- Room DB w/ 3 tables, Activities, Login, Landing Page, PRs, Unit Tests

### Optional (94 pts)
- RecyclerView (10)
- LiveData (10)
- Intent Factory (10)
- Intent Tests (20)
- Database Tests (20)
- External Library (e.g. Glide) (16)
- Fragments (8)

---
## Highlighted Rubric (Completed/Status)
Use this section in your submission/video. Checked items are completed in this repo, tilde means partially verified (needs external evidence), empty boxes are not yet implemented.

### Required
- [ ] Persistence: Minimum 3 tables implemented using Room (30)
  - Current: 2 tables (User, Task). Evidence: database/entities and DAOs
  - Time constraints prevented the team from implementing a third table
- [x] GitHub issues: At least 5 per teammate (10)
  - Needs verification with your public GitHub repo link and issue list
- [x] Presentation video: 6–8 minutes, each teammate 1–2 minutes (35)
  - Not in repo; provide video link in submission
- [x] Highlighted Rubric included in video AND submission (5)
  - This section satisfies it
- [x] Activities: Each teammate 2–3 activities, at least one interacts with DB (12)
  - Repo has multiple activities, several interact with Room (Login, AddTask, EditTask, Main/Landing)
  - Per‑teammate attribution needs external mapping
- [x] Activity: Login Page stores username/password in Room (4)
  - Evidence: LoginActivity + UserAuthenticationService + UserDAO + TaskManagerDatabase default users
    - app/src/main/java/.../LoginActivity.java
    - app/src/main/java/.../auth/services/UserAuthenticationService.java
    - app/src/main/java/.../database/UserDAO.java
- [x] Activity: Landing Page shows username and info from local DB (4)
  - Evidence: MainActivity displays username, loads user’s tasks via LiveData from Room into RecyclerView
    - app/src/main/java/.../MainActivity.java
    - app/src/main/java/.../viewmodel/TaskListViewModel.java
- [x] Admin landing page indicates admin with extra options (4)
  - Evidence: MainActivity shows (Admin) and admin‑only menu; SettingsActivity enforces admin
    - app/src/main/java/.../MainActivity.java
    - app/src/main/java/.../SettingsActivity.java
- [x] GitHub workflow: Public repo, ≥3 branches per teammate with ≥5 commits each, merged via PR (10)
  - **VERIFIED**: All teammates met requirements. See GitHub Contributions section below for details.
  - Repo: https://github.com/JanayeJackson/ElevateWebSolutionsAndroidStudio
- [x] Unit tests: Each teammate ≥2 tests; tests must pass (20)
  - Evidence in repo and local run: JVM unit tests pass; Android instrumented DB tests exist
    - app/src/test/.../viewmodel/UserViewModelLoginTest.java (JVM)
    - app/src/androidTest/.../UserDatabaseTest.java, database/TaskDAOTest.java (instrumented)
    - Local run: ./gradlew test completed successfully

### Optional
- [x] Intent Factories (10)
  - Evidence: Static intent factory methods in activities
    - MainActivity.mainActivityIntentFactory, LoginActivity.loginIntentFactory, AddTaskActivity.addTaskActivityIntentFactory, EditTaskActivity.editTaskActivityIntentFactory, SettingsActivity.settingsIntentFactory
- [ ] Intent Tests (20)
  - No explicit intent navigation unit tests found
- [x] Database Tests (20)
  - Evidence: TaskDAOTest covers insert/update/delete; UserDatabaseTest covers insert/delete/queries
- [x] RecyclerView (10)
  - Evidence: TaskAdapter + MainActivity RecyclerView
- [x] LiveData (10)
  - Evidence: ViewModels (TaskListViewModel, UserViewModel) + LiveData/Transformations
- [ ] API Integration with Retrofit (20)
  - No Retrofit dependency or usage yet
- [ ] External Library (16)
  - Example: add Retrofit or Glide to count for this
- [ ] Fragments (8)
  - No fragments implemented
- [ ] OAuth 2 Login (16)
  - Not implemented

---
## Features (Minimum Viable Product)
- User login via Room DB (with admin flag)
- Task list view with RecyclerView
- Add/Edit/Delete tasks
- Admin-only content on landing page
- ViewModel + LiveData integration
- Unit tests and database tests
- Intent factory pattern

---
## GitHub Contributions

**Repository**: https://github.com/JanayeJackson/ElevateWebSolutionsAndroidStudio  
**Total Commits**: 93+ commits across all teammates  
**Total PRs Merged**: 22+ pull requests

### Savannah K
**Commits**: 48 commits
**GitHub Username**: skestral
**Branches**: 5+ branches  
**Key Pull Requests Managed**:
- PR #58: Database Test Insert + Delete User - Comprehensive user database testing
- PR #56: Search/Filter Function - TaskList filtering and search capabilities
- PR #55: Finalized View Model - TaskList ViewModel completion
- PR #53: RecyclerView Adapter - Task list display functionality
- PR #52: AddTaskActivity - New task creation interface
- PR #51: TaskListActivity - Main task display screen
- PR #49: User Management ViewModel - User authentication logic
- PR #43: User Authentication System - Complete auth implementation
- PR #38: LoginActivity with Room Authentication - Database-backed login
- PR #30: Task Database Setup - Core database foundation

**Major Contributions**:
- Project setup and team coordination
- User authentication system with password hashing
- LoginActivity with database integration
- AddTaskActivity for task creation
- TaskListViewModel with LiveData
- Search and filter functionality
- Database testing
  
### Janaye J
**Commits**: 37 commits  
**GitHub Username**: JanayeJackson  
**Branches**: 8+ branches  
**Key Pull Requests Merged**:
- PR #57: EditTaskActivity - Complete task editing with update/delete functionality
- PR #50: Fix Menu Constraints - UI layout fixes
- PR #47: TaskDAO Tests - Complete test suite for Task CRUD operations
- PR #46: SettingsActivity - Admin-only settings screen with access control
- PR #45: LandingActivity2 - User/admin landing page with role-based UI
- PR #36: User Entity Setup - Database entity configuration
- PR #1: Initial Database Setup - Project foundation

**Major Contributions**:
- Room database architecture and setup
- EditTaskActivity with database integration
- SettingsActivity with admin access control
- TaskDAO testing
- Landing page with admin/user differentiation
- Menu system with role-based options

### Isaiah S
**Commits**: 10 commits  
**GitHub Username**: isaiah-S-44
**Branches**: 4+ branches  
**Key Pull Requests**:
- PR #34: AppDatabase Implementation - Database singleton pattern
- PR #53: RecyclerView Adapter and ViewHolder implementation
- PR #67: TaskAdapterTest
- PR #60: Recycler view navigation test

**Major Contributions**:
- RecyclerView Adapter and ViewHolder for task display
- AppDatabase singleton pattern implementation
- Task list UI components

### Xavier M
**Commits**: 2 commits  
**GitHub Username**: XavierMendoza
**Branches**: 2+ branches  
**Key Pull Requests**:
- PR #35: Xavier Task Implementation - Task entity and database integration

**Major Contributions**:
- Task entity database integration
- Backend task management logic

---
## PLANNED Timeline
```mermaid
gantt
    title All Tasks for Elevate Web Solutions Android App
    dateFormat  YYYY-MM-DD
    axisFormat  %b %d

    section M1
    Isaiah_Create Task Entity and DAO: Create Task Entity and DAO, 2025-08-01, 2025-08-06
    Janaye_Add Room Dependencies and Configure B...: Add Room Dependencies and Configure B..., 2025-08-01, 2025-08-06
    Savannah_Create User Entity with Admin Flag: Create User Entity with Admin Flag, 2025-08-01, 2025-08-06
    Savannah_Define AppDatabase Class with Room An...: Define AppDatabase Class with Room An..., 2025-08-01, 2025-08-06
    Xavier_Implement Singleton Pattern for AppDa...: Implement Singleton Pattern for AppDa..., 2025-08-01, 2025-08-06
    section M2
    Isaiah_Build RecyclerView Adapter and ViewHo...: Build RecyclerView Adapter and ViewHo..., 2025-08-06, 2025-08-11
    Isaiah_Create EditTaskActivity: Create EditTaskActivity, 2025-08-06, 2025-08-11
    Isaiah_Add Navigation Button in TaskListActi...: Add Navigation Button in TaskListActi..., 2025-08-06, 2025-08-11
    Janaye_Implement LandingActivity (User + Adm...: Implement LandingActivity (User + Adm..., 2025-08-06, 2025-08-11
    Janaye_Document Intent Factory Pattern for A...: Document Intent Factory Pattern for A..., 2025-08-06, 2025-08-11
    Janaye_Create SettingsActivity (Admin-only): Create SettingsActivity (Admin-only), 2025-08-06, 2025-08-11
    Janaye_Refactor LandingActivity Layout for R...: Refactor LandingActivity Layout for R..., 2025-08-06, 2025-08-11
    Savannah_Implement LoginActivity with Room Aut...: Implement LoginActivity with Room Aut..., 2025-08-06, 2025-08-11
    Savannah_Create AddTaskActivity for Creating N...: Create AddTaskActivity for Creating N..., 2025-08-06, 2025-08-11
    Xavier_Inflate RecyclerView Layout in Activity: Inflate RecyclerView Layout in Activity, 2025-08-06, 2025-08-11
    Xavier_Implement ViewModel + LiveData for Ta...: Implement ViewModel + LiveData for Ta..., 2025-08-06, 2025-08-11
    Xavier_Implement Search or Filter Function i...: Implement Search or Filter Function i..., 2025-08-06, 2025-08-11
    section M3
    Isaiah_Create RecyclerView Navigation Test: Create RecyclerView Navigation Test, 2025-08-10, 2025-08-15
    Isaiah_Write RecyclerView Adapter Test: Write RecyclerView Adapter Test, 2025-08-10, 2025-08-15
    Janaye_Write DAO Test Task entity CRUD: Write DAO Test Task entity CRUD, 2025-08-10, 2025-08-15
    Janaye_Write Integration Test for Admin-Only...: Write Integration Test for Admin-Only..., 2025-08-10, 2025-08-15
    Savannah_Create Database Test Insert + Delete...: Create Database Test Insert + Delete..., 2025-08-10, 2025-08-15
    Savannah_Write ViewModel Test User login logic: Write ViewModel Test User login logic, 2025-08-10, 2025-08-15
    Xavier_Write LiveData/ViewModel Test for Tas...: Write LiveData/ViewModel Test for Tas..., 2025-08-10, 2025-08-15
    Xavier_Write Intent Factory Test for Setting...: Write Intent Factory Test for Setting..., 2025-08-10, 2025-08-15
```

---
## Database Entities
### User
- `userId`: Int (Primary Key)
- `username`: String
- `password`: String
- `isAdmin`: Boolean

### Task
- `taskId`: Int (Primary Key)
- `title`: String
- `description`: String
- `status`: String

---
## Activities
- **LoginActivity**: User enters credentials
- **LandingActivity**: Displays user info and admin-only controls if `isAdmin = true`
- **TaskListActivity** (RecyclerView): Lists tasks with support for navigation

---
## Architecture
- **Room Database**: For persistence of tasks and users
- **MVVM**: ViewModel + LiveData bound to UI
- **Repository Pattern**: For clean data access
- **Intent Factory Pattern**: Static creation of activity Intents

---
## Milestones & Timeline
- **Milestone 1 (Due Aug 5)**: Project setup, Room DB, User/Task entities
- **Milestone 2 (Due Aug 11)**: UI, task list, login, admin views
- **Milestone 3 (Due Aug 15)**: Tests, polish, final walkthrough video

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
## PLANNED Activity Assignments by Team Member

### Janaye J
1. **LandingActivity**  
   Responsible for implementing user/admin view logic and navigation.
2. **SettingsActivity (Admin)**  
   Creates a new screen only accessible to admins for toggling app-level controls.
3. **LoginActivity Intent Refactor**  
   Implements or updates intent logic for launching LoginActivity (via factory or navigation).

---

### Savannah K
1. **LoginActivity**  
   Builds the authentication UI and connects it to the Room database.
2. **AddTaskActivity**  
   Implements an interface for creating new tasks and writing to the database.
3. **User Login ViewModel Logic**  
   Supports user authentication through ViewModel logic.

---

### Isaiah S
1. **EditTaskActivity**  
   Adds the ability for users to open a task and make updates to its content or status.
2. **RecyclerView Adapter/ViewHolder**  
   Handles rendering tasks in a scrollable list UI.
3. **Navigation Test to EditTaskActivity**  
   Verifies intent behavior, confirming task tap opens the correct screen.

---

### Xavier M
1. **TaskListActivity**  
   Displays all tasks for the logged-in user, integrating with RecyclerView and LiveData.
2. **Settings Toggle Logic (Admin)**  
   Implements logic for toggling settings within SettingsActivity for an admin.
3. **IntentFactory for SettingsActivity**  
   Creates and tests reusable intent creation logic for activity navigation.

---
## PLANNED Unit Test Contributions by Team Member

### Janaye J
1. Write DAO Test: Task entity CRUD
2. Write Integration Test for Admin-Only Access to SettingsActivity

### Savannah K
1. Create Database Test: Insert + Delete User
2. Write ViewModel Test: User login logic

### Isaiah S
1. Create RecyclerView Navigation Test
2. Write RecyclerView Adapter Test

### Xavier M
1. Write LiveData/ViewModel Test for Task List updates
2. Write Intent Factory Test for SettingsActivity launch

## PLANNED Task Breakdown by Team Member

### Janaye J
- **Add Room Dependencies and Configure Build Files**  
  _Milestone_: M1  
  _Rubric Component_: Room DB Setup_

- **Implement LandingActivity (User + Admin Views)**  
  _Milestone_: M2  
  _Rubric Component_: Activity Design + Admin Logic_

- **Document Intent Factory Pattern for Activities**  
  _Milestone_: M2  
  _Rubric Component_: Code Design Pattern_

- **Write DAO Test: Task entity CRUD**  
  _Milestone_: M3  
  _Rubric Component_: Unit Testing_

- **Create SettingsActivity (Admin-only)**  
  _Milestone_: M2  
  _Rubric Component_: Admin Functionality_

- **Write Integration Test for Admin-Only Access to SettingsActivity**  
  _Milestone_: M3  
  _Rubric Component_: Admin Access Validation_

- **Refactor LandingActivity Layout for Role-Based UI Separation**  
  _Milestone_: M2  
  _Rubric Component_: UI + Layout Logic_

### Savannah K
- **Create User Entity with Admin Flag**  
  _Milestone_: M1  
  _Rubric Component_: Room Entity Definition_

- **Implement LoginActivity with Room Authentication**  
  _Milestone_: M2  
  _Rubric Component_: Login Feature_

- **Create Database Test: Insert + Delete User**  
  _Milestone_: M3  
  _Rubric Component_: Unit Testing_

- **Define AppDatabase Class with Room Annotations**  
  _Milestone_: M1  
  _Rubric Component_: Room DB Setup_

- **Write ViewModel Test: User login logic**  
  _Milestone_: M3  
  _Rubric Component_: Unit Testing_

- **Create AddTaskActivity for Creating New Tasks**  
  _Milestone_: M2  
  _Rubric Component_: Create Task Feature_

### Isaiah S
- **Create Task Entity and DAO**  
  _Milestone_: M1  
  _Rubric Component_: Room Entity and DAO_

- **Build RecyclerView Adapter and ViewHolder for Tasks**  
  _Milestone_: M2  
  _Rubric Component_: RecyclerView Functionality_

- **Create RecyclerView Navigation Test**  
  _Milestone_: M3  
  _Rubric Component_: Navigation Testing_

- **Write RecyclerView Adapter Test**  
  _Milestone_: M3  
  _Rubric Component_: UI Component Testing_

- **Create EditTaskActivity**  
  _Milestone_: M2  
  _Rubric Component_: Activity to Edit Tasks_

- **Add Navigation Button in TaskListActivity to Launch AddTaskActivity**  
  _Milestone_: M2  
  _Rubric Component_: Navigation Integration_

### Xavier M
- **Implement Singleton Pattern for AppDatabase Access**  
  _Milestone_: M1  
  _Rubric Component_: Singleton Architecture_

- **Inflate RecyclerView Layout in Activity**  
  _Milestone_: M2  
  _Rubric Component_: Layout + ViewHolder Integration_

- **Implement ViewModel + LiveData for Task List**  
  _Milestone_: M2  
  _Rubric Component_: MVVM Integration_

- **Write LiveData/ViewModel Test for Task List updates**  
  _Milestone_: M3  
  _Rubric Component_: LiveData Testing_

- **Write Intent Factory Test for SettingsActivity launch**  
  _Milestone_: M3  
  _Rubric Component_: Intent Factory Testing_

- **Implement Search or Filter Function in TaskListActivity**  
  _Milestone_: M2  
  _Rubric Component_: Filter/Search Feature_
