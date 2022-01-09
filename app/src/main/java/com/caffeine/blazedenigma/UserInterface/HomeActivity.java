package com.caffeine.blazedenigma.UserInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caffeine.blazedenigma.Authentication.LoginActivity;
import com.caffeine.blazedenigma.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private ImageView signOut, STATUS;
    private TextView discordID, days, hours, minutes, seconds;
    private String Days, Hours, Minutes, Seconds;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gettingLayoutIDs();
        getDiscordName();

        Thread myThread = null;
        Runnable myRunnableThread = new CountDownRunner();
        myThread= new Thread(myRunnableThread);
        myThread.start();

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void gettingLayoutIDs(){
        discordID = findViewById(R.id.user_id);
        days = findViewById(R.id.days);
        hours = findViewById(R.id.hours);
        minutes = findViewById(R.id.minutes);
        seconds = findViewById(R.id.seconds);
        signOut = findViewById(R.id.sign_out);
        STATUS = findViewById(R.id.status);
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Date systemDate = Calendar.getInstance().getTime();
                    String myDate = sdf.format(systemDate);
                    Date Date1 = sdf.parse(myDate);
                    Date Date2 = sdf.parse("2022/01/01 22:30:00");

                    long millse = Date2.getTime() - Date1.getTime();
                    long mills = Math.abs(millse);

                    int dayss = (int) (mills / (1000*60*60*24));
                    int Hourss = (int) (mills/(1000 * 60 * 60) % 24);
                    int Minss = (int) (mills/(1000*60)) % 60;
                    long Secs = (int) (mills / 1000) % 60;

                    Days = Integer.toString(dayss);
                    Hours = Integer.toString(Hourss);
                    Minutes = Integer.toString(Minss);
                    Seconds = Long.toString(Secs);

                    if (dayss < 10) Days = "0" + Days;
                    if (Hourss < 10) Hours = "0" + Hours;
                    if (Minss < 10) Minutes = "0" + Minutes;
                    if (Secs < 10) Seconds = "0" + Seconds;

                    days.setText(Days);
                    hours.setText(Hours);
                    minutes.setText(Minutes);
                    seconds.setText(Seconds);
                }
                catch (Exception e) {}
            }
        });
    }

    class CountDownRunner implements Runnable {
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000); // Pause of 1 Second
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                catch(Exception e) {}
            }
        }
    }

    private void getDiscordName(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Blazed Enigma").child("Gamers").child(FirebaseAuth.getInstance().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ID = snapshot.child("discord").getValue(String.class);
                String status = snapshot.child("status").getValue(String.class);
                discordID.setText(ID);

                switch (status){
                    case "pending":
                        STATUS.setImageResource(R.drawable.pending);
                        break;

                    case "verified":
                        STATUS.setImageResource(R.drawable.verified);
                        break;

                    case "declined":
                        STATUS.setImageResource(R.drawable.declined);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}