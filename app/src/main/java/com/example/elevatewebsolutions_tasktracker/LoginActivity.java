package com.example.elevatewebsolutions_tasktracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.elevatewebsolutions_tasktracker.database.TaskManagerRepository;
import com.example.elevatewebsolutions_tasktracker.database.entities.User;
import com.example.elevatewebsolutions_tasktracker.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private TaskManagerRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = TaskManagerRepository.getRepository(getApplication());

        binding.loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                verifyUser();
            }
        });
    }

    /**
     * Checks to make sure entered username and password match an entry in the database
     * If no match is found message is displayed indicating an unsuccessful login attempt
     *
     */
    private void verifyUser(){
        String username = binding.userNameLoginEditText.getText().toString(); //gets username

        if(username.isEmpty()) { //ensure username is not blank
            toastMaker("username should not be blank");
            return;
        }

        LiveData<User> userObserver = repository.getUserByUserName(username); //gets user object
        userObserver.observe(this, user -> {
            if(user != null){ //checks if valid user
                String password = binding.passwordLoginEditText.getText().toString(); //gets password
                if(password.equals(user.getPassword())){ //if password matches, user is successfully logged in
                    startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), user.getId()));
                }else{ //else error message is displayed
                    toastMaker("Invalid Username and/or Password");
                    binding.passwordLoginEditText.setSelection(0);
                }
            }else { //if no user is found error message is displayed
                toastMaker("Invalid Username and/or Password");
                binding.userNameLoginEditText.setSelection(0);
            }
        });
    }

    /**
     * Displays a message
     * @param message to be displayed
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates intent of LoginActivityy
     * @param context of current tactician running
     * @return Intent of LoginActivity
     */
    static Intent loginIntentFactory(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}