package com.example.elevatewebsolutions_tasktracker.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.elevatewebsolutions_tasktracker.database.entities.TaskManager;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TaskManagerRepository {

    private final TaskManagerDAO taskManagerDAO;
    private final UserDAO userDao;
    private ArrayList<TaskManager> alltasks;

    private static TaskManagerRepository repository;

    private TaskManagerRepository(Application application) {
        TaskManager db = TaskManager.getDatabase(application);
        this.taskManagerDAO = db.taskMangerDAO();
        this.userDao = db.userDAO();
        this.alltasks = (ArrayList<TaskManager>) this.taskManagerDAO.getAllRecords();
    }

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

    public ArrayList<TaskManager> getAllTasks(){
        Future<ArrayList<TaskManager>> future = TaskManagerDatabase.databaseWriteExecutor.submit(
                new Callable<ArrayList<TaskManager>>() {
                    @Override
                    public ArrayList<TaskManager> call() throws Exception {
                        return (ArrayList<TaskManager>) taskManagerDAO.getAllRecords();
                    }
                });
        try{
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.i(MainActivity.TAG, "Problem when getting all TaskManager in the repository");
        }
        return null;
    }

    public void insertTaskManager(TaskManager taskManager) {
        TaskManagerDatabase.databaseWriteExecutor.execute(() -> {
            taskManagerDAO.insert(taskManager);
        });
    }

    public void insertUser(User... user) {
        TaskManagerDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    public LiveData<User> getUserByUserName(String username) {
        return userDao.getUserByUsername(username);
    }

    public LiveData<User> getUserByUserId(int id) {
        return userDao.getUserByUserId(id);
    }

    public LiveData<List<TaskManager>>getAllTasksByUserId(int loggedInUserId){
        return taskManagerDAO.getRecordsByUserIdLiveData(loggedInUserId);
    }
}
