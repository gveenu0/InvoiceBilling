package com.softtechnotech.invoicebilling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class customerDetail extends AppCompatActivity {
    DatabaseHelper myDb;
    DatabaseReference rootRef, demoRef;
    Button next;
    EditText customerName, customerMobile, customerAddress, customerPincode, customerEmail;
    Spinner rBackground,gst_rate;
    public static String strRBackground, strGstrate, strCustomerName, strCustomerMobile, strCustomerAddress, strCustomerPincode, strCustomerEmail;
    String preShopEmail = null, detailsCheck = "Incorrect";
    ProgressDialog nDialog;
    public static List<String> list = new ArrayList<>();
    public static List<String> list_gst_rate = new ArrayList<>();

    @Override
    protected void onStart() {
        Cursor res = myDb.getAllData(home.preShopEmail);
        if(res != null && res.getCount() > 0){
            preShopEmail = home.preShopEmail;
            detailsCheck = "correct";
        }
        else{
            Toast.makeText(customerDetail.this, "Please update your shop details", Toast.LENGTH_LONG).show();
            startUpdateShopDetailAvtivity();
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        myDb = new DatabaseHelper(this);
        customerName = findViewById(R.id.customerName);
        customerMobile = findViewById(R.id.customerMobile);
        customerAddress = findViewById(R.id.customerAddress);
        customerPincode = findViewById(R.id.customerPincode);
        customerEmail = findViewById(R.id.customerEmail);
        rBackground = findViewById(R.id.rBackground);
        gst_rate = findViewById(R.id.gst_rate);
        next = findViewById(R.id.next);

        rBackground.setSelection(0);
        list.add("Select Color");
        list.add("Blue");
        list.add("Red");
        list.add("Green");
        list.add("Violet");
        list.add("Yellow");
        list.add("Sky Blue");
        list.add("Orange");
        list.add("Grey");
        list.add("Indigo");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rBackground.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                rBackground.setSelection(i);
                strRBackground = list.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strRBackground = "Select Color";
            }
        });
        rBackground.setAdapter(arrayAdapter);

        gst_rate.setSelection(0);
        list_gst_rate.clear();
        list_gst_rate.add("Select GST Rate (Default 0%)");
        list_gst_rate.add("5%");
        list_gst_rate.add("12%");
        list_gst_rate.add("18%");
        list_gst_rate.add("28%");
        ArrayAdapter<String> arrayAdapter_gst_rate = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_gst_rate);
        arrayAdapter_gst_rate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gst_rate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                gst_rate.setSelection(i);
                strGstrate = list_gst_rate.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strGstrate = "Select GST Rate (Default 18%)";

            }
        });
        gst_rate.setAdapter(arrayAdapter_gst_rate);

        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef = rootRef.child("Invoice").child(home.userName);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nDialog = new ProgressDialog(customerDetail.this);
                nDialog.setMessage("Loading..");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(true);
                nDialog.show();

                demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("shopDetail").exists()){
                            detailsCheck = "correct";
                        }
                        else{
                            Toast.makeText(customerDetail.this, "Please update your shop details",Toast.LENGTH_LONG).show();
                            nDialog.dismiss();
                            return;
                        }
                        if(preShopEmail != null){
                            if(detailsCheck.equals("correct")){
                                strCustomerName = customerName.getText().toString();
                                strCustomerMobile = customerMobile.getText().toString();
                                strCustomerAddress = customerAddress.getText().toString();
                                strCustomerPincode = customerPincode.getText().toString();
                                strCustomerEmail = customerEmail.getText().toString();
                                if(strCustomerName.matches("")){
                                    Toast.makeText(customerDetail.this, "Enter customer name",Toast.LENGTH_LONG).show();
                                    nDialog.dismiss();
                                    return;
                                }
                                if(!strCustomerName.matches("[a-zA-Z\\s]*")){
                                    Toast.makeText(customerDetail.this, "Enter valid name(alphabate and space only)",Toast.LENGTH_LONG).show();
                                    nDialog.dismiss();
                                    return;
                                }
                                if(strCustomerMobile.matches("")){
                                    Toast.makeText(customerDetail.this, "Enter mobile number",Toast.LENGTH_LONG).show();
                                    nDialog.dismiss();
                                    return;
                                }
                                if(strCustomerMobile.length() != 10){
                                    Toast.makeText(customerDetail.this, "Enter valid mobile number",Toast.LENGTH_LONG).show();
                                    nDialog.dismiss();
                                    return;
                                }
                                if(strCustomerAddress.matches("")){
                                    Toast.makeText(customerDetail.this, "Enter address",Toast.LENGTH_LONG).show();
                                    nDialog.dismiss();
                                    return;
                                }
                                if(strCustomerPincode.matches("")){
                                    //Toast.makeText(customerDetail.this, "Enter pincode",Toast.LENGTH_LONG).show();
                                    strCustomerPincode = "-";
                                }
                                if(strCustomerPincode.length() != 6){
                                    Toast.makeText(customerDetail.this, "Enter valid pincode number",Toast.LENGTH_LONG).show();
                                    nDialog.dismiss();
                                    return;
                                }
                                if(strCustomerEmail.matches("")){
                                    strCustomerEmail = "None";
                                }
                                else if(!(strCustomerEmail.matches("[a-zA-Z0-9.]+@[a-z]+\\.+[a-z]+") || strCustomerEmail.matches("[a-zA-Z0-9.]+@[a-z]+\\.+[a-z]+\\.+[a-z]+"))){
                                    Toast.makeText(customerDetail.this, "Enter valid email",Toast.LENGTH_LONG).show();
                                    nDialog.dismiss();
                                    return;
                                }
                            startNextActivity();
                            }
                            else{
                                Toast.makeText(customerDetail.this, "Preshop Email is null",Toast.LENGTH_LONG).show();
                                nDialog.dismiss();
                            }
                        }
                        else{
                            Toast.makeText(customerDetail.this, "Please update your shop details",Toast.LENGTH_LONG).show();
                            startUpdateShopDetailAvtivity();
                            nDialog.dismiss();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        startSomethingWentWrongActivity();
                    }
                });
            }
        });
    }
    public void startNextActivity(){
        Intent intent = new Intent(this, itemDetail.class);
        list.clear();
        list_gst_rate.clear();
        startActivity(intent);
        nDialog.dismiss();
    }
    public void startSomethingWentWrongActivity(){
        Intent intent = new Intent(this, somethingWentWrong.class);
        list.clear();
        list_gst_rate.clear();
        startActivity(intent);
        nDialog.dismiss();
    }
    public void startHomeActivity(){
        Intent intent = new Intent(this, home.class);
        list.clear();
        list_gst_rate.clear();
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        startHomeActivity();
    }
    public void startUpdateShopDetailAvtivity(){
        Intent intent = new Intent(this, updateShopDetails.class);
        startActivity(intent);
    }
}
