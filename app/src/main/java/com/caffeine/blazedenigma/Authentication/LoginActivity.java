package com.caffeine.blazedenigma.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caffeine.blazedenigma.R;
import com.caffeine.blazedenigma.UserInterface.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private String Email, Password;
    private TextView forgotPass, loginTxt;
    private RelativeLayout login, signUp;
    private ProgressBar progressBar;
    private Dialog dialog;
    public static final String EMAIL_PATTERN = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gettingLayoutIDs();
        onClickEvents();
    }

    private void gettingLayoutIDs(){
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgotPass = findViewById(R.id.forgot_btn);
        login = findViewById(R.id.login_btn);
        loginTxt = findViewById(R.id.login_txt);
        signUp = findViewById(R.id.signup_btn);
        progressBar = findViewById(R.id.progress_bar);
        dialog = new Dialog(this);
    }

    private void intentToActivity(Class c){
        startActivity(new Intent(LoginActivity.this, c));
    }

    private void assignData(){
        Email = email.getText().toString();
        Password = password.getText().toString();
    }

    private boolean validate(){
        boolean v = true;

        if (Email.isEmpty()){
            email.setError("Field cannot be empty");
            v = false;
        }

        else if (!Email.matches(EMAIL_PATTERN)){
            email.setError("Invalid email address");
            v = false;
        }

        else if (Password.isEmpty()){
            password.setError("Field cannot be empty");
            v = false;
        }

        else if (Password.length() < 6 || Password.length() >20){
            password.setError("Password should be 6 to 20 characters");
            v = false;
        }

        return v;
    }

    public boolean isInternetAvailable(Activity activity){
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null){
            customPopup(
                    "No Internet",
                    "Please check your internet connection and try again",
                    "Close"
            );
            return false;
        }
        else {
            if(!info.isConnected()) {
                customPopup(
                        "No Internet",
                        "Please check your internet connection and try again",
                        "Close"
                );
                return false;
            }
            else {
                return true;
            }

        }
    }

    private void SignInUser(String Email, String Password){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    intentToActivity(HomeActivity.class);
                    finish();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    loginTxt.setText("Login");
                    customPopup(
                            "Invalid Credentials",
                            "The Email and Password you entered do not match with any account in our database",
                            "Close"
                    );
                }
            }
        });
    }

    private void onClickEvents(){
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToActivity(SignUpActivity.class);
                finish();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToActivity(ForgotPassowrdActivity.class);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignData();
                if (validate()){
                    progressBar.setVisibility(View.VISIBLE);
                    loginTxt.setText("");

                    if (isInternetAvailable(LoginActivity.this)){
                        SignInUser(Email, Password);
                    }

                    else {
                        progressBar.setVisibility(View.GONE);
                        loginTxt.setText("Login");
                    }
                }
            }
        });
    }

    private void customPopup(String Title, String Message, String Action){
        dialog.setContentView(R.layout.popup_dialog);
        TextView title, message, action;
        title = dialog.findViewById(R.id.popup_title);
        message = dialog.findViewById(R.id.popup_message);
        action = dialog.findViewById(R.id.popup_action);

        title.setText(Title);
        message.setText(Message);
        action.setText(Action);

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}