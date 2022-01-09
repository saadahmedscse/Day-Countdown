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
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caffeine.blazedenigma.Model.UserData;
import com.caffeine.blazedenigma.R;
import com.caffeine.blazedenigma.UserInterface.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    private EditText discord, email, password;
    private String Discord, Email, Password, Serial;
    private TextView signUpTxt;
    private RelativeLayout signUp;
    private ProgressBar progressBar;
    private Dialog dialog;
    public static final String EMAIL_PATTERN = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";

    private FirebaseAuth auth;
    private DatabaseReference ref, serialRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        gettingLayoutIDs();

        serialRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Serial = snapshot.child("serial").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignData();
                if (validate()){
                    progressBar.setVisibility(View.VISIBLE);
                    signUpTxt.setText("");

                    if (isInternetAvailable(SignUpActivity.this)){
                        usernameExist();
                    }

                    else {
                        progressBar.setVisibility(View.GONE);
                        signUpTxt.setText("Create New Account");
                    }
                }
            }
        });
    }

    private void gettingLayoutIDs(){
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Blazed Enigma").child("Gamers");
        serialRef = FirebaseDatabase.getInstance().getReference().child("Blazed Enigma").child("Serial");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        discord = findViewById(R.id.discord_id);
        signUpTxt = findViewById(R.id.sign_up_txt);
        signUp = findViewById(R.id.signup_btn);
        progressBar = findViewById(R.id.progress_bar);
        dialog = new Dialog(this);
    }

    private void assignData(){
        Discord = discord.getText().toString();
        Email = email.getText().toString();
        Password = password.getText().toString();
    }

    private boolean validate(){
        boolean v = true;

        if (Discord.isEmpty()){
            discord.setError("Field cannot be empty");
            v = false;
        }

        else if (Email.isEmpty()){
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

    private void signUpUser(String Email, String Password){
        auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    sendDataToFirebase();
                }

                else {
                    progressBar.setVisibility(View.GONE);
                    signUpTxt.setText("Create New Account");
                    customPopup(
                            "Already Registered",
                            "The Email you entered has been already registered. Please create account using another Email address",
                            "Close"
                    );
                }
            }
        });
    }

    private void usernameExist(){

        ref.orderByChild("discord").equalTo(Discord).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    discord.setError("Discord ID already exist");
                    progressBar.setVisibility(View.GONE);
                    signUpTxt.setText("Create New Account");
                }

                else {
                    signUpUser(Email, Password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendDataToFirebase(){
        int count = Integer.parseInt(Serial) + 1;
        String fSerial = Integer.toString(count);

        DatabaseReference sRef = FirebaseDatabase.getInstance().getReference().child("Blazed Enigma").child("SUID");

        UserData user = new UserData(fSerial, FirebaseAuth.getInstance().getUid(), Discord, Email, Password, "pending");
        ref.child(FirebaseAuth.getInstance().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    serialRef.child("serial").setValue(fSerial).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sRef.child(fSerial).child("UID").setValue(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            finish();
                                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                        }

                                        else {
                                            customPopup(
                                                    "Registration Failed",
                                                    "An error occurred while creating your account. Please try again letter",
                                                    "Close"
                                            );
                                        }
                                    }
                                });
                            }

                            else {
                                customPopup(
                                        "Registration Failed",
                                        "An error occurred while creating your account. Please try again letter",
                                        "Close"
                                );
                            }
                        }
                    });
                }

                else {
                    customPopup(
                            "Registration Failed",
                            "An error occurred while creating your account. Please try again letter",
                            "Close"
                    );
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }
}