package com.example.msiseri.orderapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msiseri.orderapp.Common.Common;
import com.example.msiseri.orderapp.Model.User;
import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button btnSignUp, btnSignIn;
    TextView txtSlogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);

        txtSlogan = findViewById(R.id.txtSlogan);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(typeface);

        try {
            printKeyHash();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });

        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        if(user != null && pwd != null){
            if(!user.isEmpty() && !pwd.isEmpty()){
                login(user,pwd);
            }
        }
    }

    private void printKeyHash() throws NoSuchAlgorithmException {
        try{
            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.example.msiseri.orderapp",
                    PackageManager.GET_SIGNATURES);
            for(Signature signature : packageInfo.signatures){
                MessageDigest digest = MessageDigest.getInstance("SHA");
                digest.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(digest.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void login(final String phone, final String pwd) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Please wait...");
            dialog.show();
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(phone).exists()) {
                        dialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(pwd)) {
                            Toast.makeText(MainActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(intent);
                            finish(); //??
                        } else {
                            Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Account is not exist. Please sign up!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Check your Internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
