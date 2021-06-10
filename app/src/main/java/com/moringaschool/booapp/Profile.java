package com.moringaschool.booapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends AppCompatActivity {

    TextView phoneTv;
    Button logoutButton;


    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        phoneTv = findViewById(R.id.phoneTv);
        logoutButton = findViewById(R.id.logoutBtn);


        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUserStatus();
            }
        });


    }

    private void checkUserStatus() {
        //getting the current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            //user is logged in
            String phone = firebaseUser.getPhoneNumber();
            phoneTv.setText(phone);

        }else {
            //user is not logged in
            finish();
        }

    }
}