package com.moringaschool.booapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Phone_Login extends AppCompatActivity {

    TextView resendCodeTv, codeSentDescription;
    EditText phoneEt , codeEt;
    Button phoneContinueBtn , codeSubmitBtn;
    LinearLayout phonelayout , codelayout;

  private PhoneAuthProvider.ForceResendingToken forceResendingToken;

  private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

  private String mVerificationId;

  private FirebaseAuth firebaseAuth;


  //Progress Dialog

    private ProgressDialog pd;

  private static final String TAG = "MAIN_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        codeSentDescription = findViewById(R.id.codeSentDescription);

        phoneEt = findViewById(R.id.phoneEt);
        codeEt = findViewById(R.id.codeEt);

        codeSubmitBtn = findViewById(R.id.codeSubmitBtn);
        resendCodeTv = findViewById(R.id.resendCodeTv);
        phoneContinueBtn = findViewById(R.id.phoneContinueButton);
        phonelayout = findViewById(R.id.phonelayout);
        codelayout = findViewById(R.id.codelayout);

        phonelayout.setVisibility(View.VISIBLE);
        codelayout.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(Phone_Login.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull @NotNull String verificationId, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG, "onCodeSent: " + verificationId);

                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                phonelayout.setVisibility(View.GONE);
                codelayout.setVisibility(View.VISIBLE);

                Toast.makeText(Phone_Login.this, "Verification code sent...", Toast.LENGTH_SHORT).show();


                codeSentDescription.setText("Please type the verification code we sent \n to  " + phoneEt.getText().toString().trim());

            }
        };

        phoneContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String phone = phoneEt.getText().toString().trim();
               if (TextUtils.isEmpty(phone)){
                   Toast.makeText(Phone_Login.this, "Please enter phone number.....", Toast.LENGTH_SHORT).show();
               }else {
                   startPhoneNumberVerification(phone);
               }

            }
        });

        resendCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneEt.getText().toString().trim();
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(Phone_Login.this, "Please enter phone number.....", Toast.LENGTH_SHORT).show();
                }else {
                    resendVerficationCode(phone, forceResendingToken);
                }
            }
        });

        codeSubmitBtn.setOnClickListener(v -> {
            String code = codeEt.getText().toString().trim();
            if (TextUtils.isEmpty(code)){
                Toast.makeText(Phone_Login.this, "Please enter verification code.....", Toast.LENGTH_SHORT).show();
            }else {
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });

}


    private void startPhoneNumberVerification(String phone) {

        pd.setMessage("Verifying Phone Number");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void resendVerficationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
       pd.setMessage("Resending Code");
       pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L,TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }


    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        pd.setMessage("Verifying Code ");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Loggin In");

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    //successfully signed in
                    pd.dismiss();
                    String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                    Toast.makeText(Phone_Login.this, "Logged In as " + phone, Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(Phone_Login.this, ProfileActivity.class));

                })
                .addOnFailureListener(e -> {
                    //failed signing in
                    pd.dismiss();
                    Toast.makeText(Phone_Login.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
