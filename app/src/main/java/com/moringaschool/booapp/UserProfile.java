package com.moringaschool.booapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

public class UserProfile extends AppCompatActivity {


    TextView phoneTv;
    EditText userPhoneNumber;
    Button logoutButton;


    FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        userPhoneNumber = findViewById(R.id.userPhoneNumber);
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
            userPhoneNumber.setText(phone);

        }else {
            //user is not logged in
            finish();
        }

    }


}