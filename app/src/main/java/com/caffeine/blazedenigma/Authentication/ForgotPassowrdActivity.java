package com.caffeine.blazedenigma.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

import com.caffeine.blazedenigma.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassowrdActivity extends AppCompatActivity {

    private EditText email;
    private TextView resetTxt;
    private RelativeLayout resetBtn;
    private Dialog dialog;
    private ProgressBar progressBar;
    private String Email, RT = "Reset Password";
    public static final String EMAIL_PATTERN = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_passowrd);

        email = findViewById(R.id.email);
        resetTxt = findViewById(R.id.reset_txt);
        resetBtn = findViewById(R.id.reset_btn);
        progressBar = findViewById(R.id.progress_bar);
        dialog = new Dialog(this);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email = email.getText().toString();

                if (Email.isEmpty()){
                    email.setError("Field cannot be empty");
                }

                else if (!Email.matches(EMAIL_PATTERN)){
                    email.setError("Invalid email address");
                }

                else if (isInternetAvailable(ForgotPassowrdActivity.this)){
                    progressBar.setVisibility(View.VISIBLE);
                    resetTxt.setText("");
                    sendResetEmail();
                }
            }
        });
    }

    private void sendResetEmail(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    resetTxt.setText(RT);
                    customPopup(
                            "Check Email",
                            "A password reset email has been sent to your email address.",
                            "Close"
                    );
                }

                else {
                    progressBar.setVisibility(View.GONE);
                    resetTxt.setText(RT);
                    customPopup(
                            "Error Occurred",
                            "An error occurred while sending your password reset email. Please check your email or try again latter",
                            "Close"
                    );
                }
            }
        });
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
}