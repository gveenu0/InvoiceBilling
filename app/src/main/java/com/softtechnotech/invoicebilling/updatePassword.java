package com.softtechnotech.invoicebilling;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class updatePassword extends AppCompatActivity {
    DatabaseReference rootRef, demoRef;
    private FirebaseAuth mAuth;

    EditText shopEmail;
    Button update;
    public static String strShopEmail;
    ProgressDialog nDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        shopEmail = findViewById(R.id.shopEmail);
        update = findViewById(R.id.update);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef = rootRef.child("Invoice").child(home.userName);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nDialog = new ProgressDialog(updatePassword.this);
                nDialog.setMessage("Loading..");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(true);
                nDialog.show();
                strShopEmail = shopEmail.getText().toString();
                if(strShopEmail.isEmpty()){
                    Toast.makeText(updatePassword.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    nDialog.dismiss();
                    return;
                }
                if(login.strNewUsername.equals(strShopEmail)){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(login.strNewUsername)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(updatePassword.this, "Password reset link send to your email",
                                                Toast.LENGTH_SHORT).show();
                                        startActivityUpdate();
                                    }
                                    else{
                                        startSomethingWentWrongActivity();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(updatePassword.this, "Enter correct shop email",
                            Toast.LENGTH_SHORT).show();
                    nDialog.dismiss();
                }
            }
        });
    }
    public void startActivityUpdate(){
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
        nDialog.dismiss();
    }
    public void startSomethingWentWrongActivity(){
        Intent intent = new Intent(this, somethingWentWrong.class);
        startActivity(intent);
        nDialog.dismiss();
    }
}
