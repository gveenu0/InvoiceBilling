package com.softtechnotech.invoicebilling;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class aboutPage extends AppCompatActivity {
    Button home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        home = findViewById(R.id.home);
        home.setOnClickListener(v -> startHomeActivity());
    }
    public void startHomeActivity(){
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }
}
