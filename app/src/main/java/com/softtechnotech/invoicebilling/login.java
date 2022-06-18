package com.softtechnotech.invoicebilling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class login extends AppCompatActivity {

    DatabaseHelper myDb;
    DatabaseReference rootRef, demoRef;
    private FirebaseAuth mAuth;

    public static String strUsername,strNewUsername, strPassword;
    Button login;
    EditText username, password;
    TextView forgotPassword;
    ProgressDialog nDialog;
    public static int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myDb = new DatabaseHelper(this);
        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        forgotPassword = findViewById(R.id.forgotPassword);
        nDialog = new ProgressDialog(login.this);
        myDb.getWritableDatabase();
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef = rootRef.child("Invoice");


//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Firebase Helper xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx//

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nDialog.setMessage("Loading..");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(true);
                nDialog.show();
                strNewUsername = username.getText().toString();
                strPassword = password.getText().toString();
                strUsername = strNewUsername.replaceAll("[@.]","");
                if(strNewUsername.isEmpty()){
                    Toast.makeText(login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    nDialog.dismiss();
                    return;
                }
                if(strPassword.isEmpty()){
                    Toast.makeText(login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    nDialog.dismiss();
                    return;
                }
                if(!(strNewUsername.matches("[a-zA-Z0-9.]+@[a-z]+\\.+[a-z]+") || strNewUsername.matches("[a-zA-Z0-9.]+@[a-z]+\\.+[a-z]+\\.+[a-z]+"))){
                    Toast.makeText(login.this, "Enter valid email",Toast.LENGTH_LONG).show();
                    nDialog.dismiss();
                    return;
                }

                demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(strUsername).exists())
                        {
                            mAuth.signInWithEmailAndPassword(strNewUsername, strPassword).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            flag = 2;
                                            startActivityLogin();
                                        }
                                    }
                                    else{
                                        startWrongActivity();
                                    }
                                }
                            });
                        }
                        else{
                            startWrongActivity();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        startErrorActivity();
                    }
                });

            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startForgotPasswordActivity();
            }
        });
    }

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Firebase Helper xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx//

    public void startActivityLogin(){
        Intent intent = new Intent(this, home.class);
        File myDir = new File(Environment.getExternalStorageDirectory().toString(), "Invoice");

        final String newUser, newEmail;
        newUser = strPassword;
        newEmail = strNewUsername;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LOGIN", newUser);
        editor.putString("EMAIL", newEmail);
        editor.putString("USERNAME", strNewUsername.replaceAll("[@.]",""));
        editor.commit();

        if(myDir.exists()){
            startActivity(intent);
        }
        else{
            myDir.mkdir();
            startActivity(intent);
        }
    }
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx//

    public void startWrongActivity(){
        Intent intent = new Intent(this, wrg.class);
        startActivity(intent);
        nDialog.dismiss();
    }
    public void startErrorActivity(){
        Intent intent = new Intent(this, somethingWentWrong.class);
        startActivity(intent);
        nDialog.dismiss();
    }
    public void startForgotPasswordActivity(){
        Intent intent = new Intent(this, forgotPassword.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        startLoginActivity();
    }
    public void startLoginActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
