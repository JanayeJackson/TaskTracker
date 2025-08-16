package com.example.elevatewebsolutions_tasktracker.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.elevatewebsolutions_tasktracker.MainActivity;
import com.example.elevatewebsolutions_tasktracker.auth.factory.AuthenticationServiceFactory;
import com.example.elevatewebsolutions_tasktracker.auth.models.AuthenticationResult;
import com.example.elevatewebsolutions_tasktracker.auth.models.LoginRequest;
import com.example.elevatewebsolutions_tasktracker.auth.models.UserSession;
import com.example.elevatewebsolutions_tasktracker.auth.services.SessionManager;
import com.example.elevatewebsolutions_tasktracker.auth.services.UserAuthenticationService;
import com.example.elevatewebsolutions_tasktracker.database.entities.Task;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TaskManagerRepository {

    private final TaskDAO taskDAO;
    private final UserDAO userDao;
    private LiveData<List<Task>> alltasks;

    private static TaskManagerRepository repository;

    /**
     * Creates single instance of Database that can be used to update database ensuring
     * there are no conflicts or collisions.
     * @param application
     */
    private TaskManagerRepository(Application application) {
        TaskManagerDatabase db = TaskManagerDatabase.getDatabase(application);
        this.taskDAO = db.taskDAO();
        this.userDao = db.userDAO();
        this.alltasks = this.taskDAO.getAllTasks();

        // Initialize authentication services
        AuthenticationServiceFactory.getInstance().initialize(application);
    }

    /**
     * Checks to see if there is an active repository, if so returns that repository
     * Else waits for an instance of the repository to be created using the databseWriteExecutor
     * Uses try and catch to attempt to return created repository, if error occurs exception is thrown
     * @param application
     * @return instance of repository
     */
    public static TaskManagerRepository getRepository(Application application){
        if (repository != null){
            return repository;
        }
        Future<TaskManagerRepository> future = TaskManagerDatabase.databaseWriteExecutor.submit(
                new Callable<TaskManagerRepository>() {
                    @Override
                    public TaskManagerRepository call() throws Exception {
                        return new TaskManagerRepository(application);
                    }
                }
        );
        try{
            return future.get();
        }catch(InterruptedException | ExecutionException e){
            Log.d(MainActivity.TAG, "Problem getting TaskManagerRepository, thread error");
        }
        return null;
    }

    /**
     * Gets all tasks in the task database
     * @return a LiveData list of all tasks
     */
    public LiveData<List<Task>> getAllTasks(){
        return  taskDAO.getAllTasks();
    }

    /**
     * Inserts a task into the task database
     * @param task
     */
    public void insertTask(Task task) {
        TaskManagerDatabase.databaseWriteExecutor.execute(() -> {
            taskDAO.insert(task);
        });
    }

    /**
     * Updates  a user in the user database
     * @param user
     */
    public void updateUser(User user) {
        TaskManagerDatabase.databaseWriteExecutor.execute(() -> {
            userDao.update(user);
        });
    }

    /**
     * Deletes a user into the user database
     * @param user
     */
    public void deleteUser(User user) {
        TaskManagerDatabase.databaseWriteExecutor.execute(() -> {
            userDao.delete(user);
        });
    }

    /**
     * Inserts a user into the user database
     * @param user
     */
    public void insertUser(User... user) {
        TaskManagerDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    /**
     * Gets user properties using username
     * @param username
     * @return a LiveData object of a User
     */
    public LiveData<User> getUserByUserName(String username) {
        return userDao.getUserByUsername(username);
    }

    /**
     * Gets user properties using user ID
     * @param id
     * @return a LiveData object of a User
     */
    public LiveData<User> getUserByUserId(int id) {
        return userDao.getUserByUserId(id);
    }

    /**
     * Gets a list of tasks by the ID of the user that is loggedIn
     * @param loggedInUserId
     * @return a LivedData object of all tasks associated with current user
     */
    public LiveData<List<Task>>getAllTasksByUserId(int loggedInUserId){
        return taskDAO.getTasksByUserId(loggedInUserId);
    }

    /**
     * Authenticates user with username and password
     * @param username Username for authentication
     * @param password Password for authentication
     * @return CompletableFuture with AuthenticationResult
     */
    public CompletableFuture<AuthenticationResult> authenticateUser(String username, String password) {
        UserAuthenticationService authService = AuthenticationServiceFactory.getInstance().getAuthenticationService();
        if (authService != null) {
            LoginRequest loginRequest = new LoginRequest(username, password);
            return authService.authenticateUser(loginRequest);
        }
        return CompletableFuture.completedFuture(
            new AuthenticationResult(AuthenticationResult.AuthError.UNKNOWN_ERROR, "Authentication service not available")
        );
    }

    /**
     * Creates a new user session after successful authentication
     * @param user Authenticated user
     * @return Created UserSession
     */
    public UserSession createUserSession(User user) {
        SessionManager sessionManager = AuthenticationServiceFactory.getInstance().getSessionManager();
        if (sessionManager != null) {
            return sessionManager.createSession(user);
        }
        return null;
    }

    /**
     * Destroys current user session (logout)
     */
    public void destroyUserSession() {
        SessionManager sessionManager = AuthenticationServiceFactory.getInstance().getSessionManager();
        if (sessionManager != null) {
            sessionManager.destroySession();
        }
    }

    /**
     * Gets current active user session
     * @return Current UserSession or null if not logged in
     */
    public UserSession getCurrentUserSession() {
        SessionManager sessionManager = AuthenticationServiceFactory.getInstance().getSessionManager();
        if (sessionManager != null) {
            return sessionManager.getCurrentSession();
        }
        return null;
    }

    /**
     * Checks if a user is currently logged in
     * @return true if user is logged in with valid session
     */
    public boolean isUserLoggedIn() {
        SessionManager sessionManager = AuthenticationServiceFactory.getInstance().getSessionManager();
        return sessionManager != null && sessionManager.isLoggedIn();
    }

    /**
     * Gets current logged in user ID
     * @return User ID or -1 if not logged in
     */
    public int getCurrentUserId() {
        SessionManager sessionManager = AuthenticationServiceFactory.getInstance().getSessionManager();
        if (sessionManager != null) {
            return sessionManager.getCurrentUserId();
        }
        return -1;
    }

    /**
     * Checks if current user is admin
     * @return true if current user is admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        SessionManager sessionManager = AuthenticationServiceFactory.getInstance().getSessionManager();
        return sessionManager != null && sessionManager.isCurrentUserAdmin();
    }

    /**
     * Creates a new user with hashed password
     * @param username Username for new user
     * @param password Plain text password
     * @param title User title
     * @param isAdmin Whether user should have admin privileges
     * @return CompletableFuture with AuthenticationResult
     */
    public CompletableFuture<AuthenticationResult> createUser(String username, String password, String title, boolean isAdmin) {
        UserAuthenticationService authService = AuthenticationServiceFactory.getInstance().getAuthenticationService();
        if (authService != null) {
            return authService.createUser(username, password, title, isAdmin);
        }
        return CompletableFuture.completedFuture(
            new AuthenticationResult(AuthenticationResult.AuthError.UNKNOWN_ERROR, "Authentication service not available")
        );
    }

    /**
     * Cleanup method to shutdown authentication services
     */
    public void shutdown() {
        AuthenticationServiceFactory.getInstance().shutdown();
    }
}