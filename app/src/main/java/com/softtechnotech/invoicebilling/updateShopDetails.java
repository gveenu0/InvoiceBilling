package com.softtechnotech.invoicebilling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class updateShopDetails extends AppCompatActivity {
    DatabaseHelper myDb;
    DatabaseReference rootRef, demoRef, demoRef1;
    ProgressDialog nDialog;
    Button update;
    public static int check;
    int flag = 0;
    EditText shopName, shopMobile, shopAddress, shopPincode, shopEmail, gstNumber;
    public static String strShopName, strShopMobile, strShopAddress, strShopPincode, strShopEmail, strGstNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_shop_details);

        myDb = new DatabaseHelper(this);

        update = findViewById(R.id.update);
        shopName = findViewById(R.id.shopName);
        shopMobile = findViewById(R.id.shopMobile);
        shopAddress = findViewById(R.id.shopAddress);
        shopPincode = findViewById(R.id.shopPincode);
        shopEmail = findViewById(R.id.shopEmail);
        gstNumber = findViewById(R.id.gstNumber);

        shopEmail.setText(home.preShopEmail);
        shopEmail.setEnabled(false);

        rootRef = FirebaseDatabase.getInstance().getReference();
        String strNewShopName, strNewShopAddress, strNewShopMobile, strNewShopEmail, strNewGstNumber, strNewPincode;
        Cursor res = myDb.getAllData(home.preShopEmail);
        if(res != null && res.getCount() > 0){
            res.moveToFirst();
            do{
                strNewShopEmail = res.getString(0);
                shopEmail.setText(strNewShopEmail);
                strNewShopName = res.getString(1);
                shopName.setText(strNewShopName);
                strNewShopMobile = res.getString(2);
                shopMobile.setText(strNewShopMobile);
                strNewShopAddress = res.getString(3);
                shopAddress.setText(strNewShopAddress);
                strNewPincode = res.getString(4);
                shopPincode.setText(strNewPincode);
                strNewGstNumber = res.getString(5);
                gstNumber.setText(strNewGstNumber);
                flag = 1;
            }while(res.moveToNext());
        }
        demoRef = rootRef.child("Invoice").child(home.userName);
        demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("shopDetail").exists()){
                    if(dataSnapshot.child("shopDetail").child("shopName").exists()){
                        check = 1;
                        shopName.setText(dataSnapshot.child("shopDetail").child("shopName").getValue().toString());
                    }
                    if(dataSnapshot.child("shopDetail").child("shopMobile").exists()){
                        shopMobile.setText(dataSnapshot.child("shopDetail").child("shopMobile").getValue().toString());
                    }
                    if(dataSnapshot.child("shopDetail").child("shopAddress").exists()){
                        shopAddress.setText(dataSnapshot.child("shopDetail").child("shopAddress").getValue().toString());
                    }
                    if(dataSnapshot.child("shopDetail").child("shopPincode").exists()){
                        shopPincode.setText(dataSnapshot.child("shopDetail").child("shopPincode").getValue().toString());
                    }
                    if(dataSnapshot.child("shopDetail").child("shopEmail").exists()){
                        shopEmail.setText(dataSnapshot.child("shopDetail").child("shopEmail").getValue().toString());
                    }
                    if(dataSnapshot.child("shopDetail").child("gstNumber").exists()){
                        strGstNumber = dataSnapshot.child("shopDetail").child("gstNumber").getValue().toString();
                        if(strGstNumber.equals("None")){
                            gstNumber.setText("");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                startWrongActivity();
            }
        });


        FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef = rootRef.child("Invoice").child(home.userName);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nDialog = new ProgressDialog(updateShopDetails.this);
                nDialog.setMessage("Loading..");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(true);
                nDialog.show();
                strShopName = shopName.getText().toString();
                strShopMobile = shopMobile.getText().toString();
                strShopAddress = shopAddress.getText().toString();
                strShopPincode = shopPincode.getText().toString();
                strShopEmail = shopEmail.getText().toString();
                strGstNumber = gstNumber.getText().toString();

                if(strShopName.matches("")){
                    Toast.makeText(updateShopDetails.this, "Enter Shop Name",Toast.LENGTH_LONG).show();
                    nDialog.dismiss();
                    return;
                }
                if(strShopMobile.matches("")){
                    Toast.makeText(updateShopDetails.this, "Enter Mobile No.",Toast.LENGTH_LONG).show();
                    nDialog.dismiss();
                    return;
                }
                if(strShopMobile.length() != 10){
                    Toast.makeText(updateShopDetails.this, "Enter valid mobile number",Toast.LENGTH_LONG).show();
                    nDialog.dismiss();
                    return;
                }
                if(strShopAddress.matches("")){
                    Toast.makeText(updateShopDetails.this, "Enter Address",Toast.LENGTH_LONG).show();
                    nDialog.dismiss();
                    return;
                }
                if(strShopPincode.matches("")){
                    Toast.makeText(updateShopDetails.this, "Enter Pincode",Toast.LENGTH_LONG).show();
                    nDialog.dismiss();
                    return;
                }
                if(strShopPincode.length() != 6){
                    Toast.makeText(updateShopDetails.this, "Enter valid pincode number",Toast.LENGTH_LONG).show();
                    nDialog.dismiss();
                    return;
                }
                if(strGstNumber.matches("")){
                    strGstNumber = "None";
                }
                if(strGstNumber.length() != 15){
                    if(!strGstNumber.equals("None")) {
                        Toast.makeText(updateShopDetails.this, "Enter valid GST number",Toast.LENGTH_LONG).show();
                        nDialog.dismiss();
                        return;
                    }
                }

                demoRef1 = demoRef.child("shopDetail");
                demoRef1.child("shopName").setValue(strShopName);
                demoRef1.child("shopMobile").setValue(strShopMobile);
                demoRef1.child("shopAddress").setValue(strShopAddress);
                demoRef1.child("shopPincode").setValue(strShopPincode);
                demoRef1.child("gstNumber").setValue(strGstNumber);
                demoRef1.child("shopEmail").setValue(strShopEmail);


                //Cursor res = myDb.getInfo();
                if(flag == 1){
                    if(myDb.updateData(strShopName, strShopMobile, strShopAddress, strShopPincode, strShopEmail, strGstNumber)){
                        Toast.makeText(updateShopDetails.this, "Update Successfull", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(updateShopDetails.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        startSmwActivity();
                    }
                }
                else{
                    if(myDb.insertData(strShopName, strShopMobile, strShopAddress, strShopPincode, strShopEmail, strGstNumber)){
                        Toast.makeText(updateShopDetails.this, "Update Successfull", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(updateShopDetails.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        startSmwActivity();
                    }

                }
                startUpdateShopDetailsActivity();
            }
        });
    }
    public void startUpdateShopDetailsActivity(){
        Intent intent = new Intent(this, home.class);
        Toast.makeText(updateShopDetails.this, "Shop details successfully updated",Toast.LENGTH_LONG).show();
        startActivity(intent);
        nDialog.dismiss();
    }
    public void startWrongActivity(){
        Intent intent = new Intent(this, wrg.class);
        startActivity(intent);
        nDialog.dismiss();
    }
    public void startSmwActivity(){
        Intent intent = new Intent(this, somethingWentWrong.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        startHomeActivity();
    }
    public void startHomeActivity(){
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }
}
