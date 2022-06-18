package com.softtechnotech.invoicebilling;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home extends AppCompatActivity {

    DatabaseHelper myDb;
    DatabaseReference rootRef, demoRef;
    NavigationView nav_view;
    private FirebaseAuth mAuth;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private SharedPreferences sharedPreferences;
    public final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    public static int flag = 0, flagLogin = 0;
    public static String preShopEmail, userName,strCustomerName,strCustomerMobile,strCustomerAddress,strCustomerPincode,strCustomerEmail,strOtherDetail;
    private String strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myDb = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        nav_view = (NavigationView)findViewById(R.id.nav_view);

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Access Permission xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            });
//                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Database Helper And Firebase Authentication xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx//
        flagLogin = login.flag;
        flag = MainActivity.flag;
        sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
        String login_done = sharedPreferences.getString("LOGIN", null);
        if(login_done != null){
            flag = 2;
        }
        else{
            // Check if user is signed in (non-null) and update UI accordingly.
            if(mAuth.getCurrentUser() != null){
                flag = 1;
            }
        }
        if(flag == 1 || flagLogin == 1){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            preShopEmail = currentUser.getEmail();
            userName = preShopEmail.replaceAll("[@.]","");
            Menu nav_menu = nav_view.getMenu();
            nav_menu.findItem(R.id.updatePassword).setVisible(false);
        }
        else if(flag == 2 || flagLogin == 2){
            sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
            strPassword = sharedPreferences.getString("LOGIN", null);
            preShopEmail = sharedPreferences.getString("EMAIL", null);
            userName = sharedPreferences.getString("USERNAME", null);
            if(strPassword != null){
                mAuth.signInWithEmailAndPassword(preShopEmail, strPassword).addOnCompleteListener(home.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                boolean emailVerified = user.isEmailVerified();
                                if(!emailVerified){
                                    new AlertDialog.Builder(home.this)
                                            .setTitle("Email Verification")
                                            .setMessage("Please verify your email id")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).show();
                                }
                            }
                        }
                        else{
                            Toast.makeText(home.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                startSmwActivity();
            }
        }
        else{
            Toast.makeText(home.this, "Flag 1",
                        Toast.LENGTH_SHORT).show();
            startSmwActivity();
        }

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Firebase Helper and Firebase Authentication xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx//
        sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
        strCustomerName = sharedPreferences.getString("CUSTOMERNAME", null);
        strCustomerMobile = sharedPreferences.getString("CUSTOMERMOBILE", null);
        strCustomerAddress = sharedPreferences.getString("CUSTOMERADDRESS", null);
        strCustomerPincode = sharedPreferences.getString("CUSTOMERPINCODE", null);
        strCustomerEmail = sharedPreferences.getString("CUSTOMEREMAIL", null);
        strOtherDetail = sharedPreferences.getString("OTHERDETAIL", null);
        Log.d("MyTag", Integer.toString(flag));
        demoRef = rootRef.child("Invoice").child(userName).child("shopDetail");
        demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("shopEmail").exists()){
                    String preShopEmail = dataSnapshot.child("shopEmail").getValue().toString();
                    sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
                    strPassword = sharedPreferences.getString("LOGIN", null);
                    mAuth.signInWithEmailAndPassword(preShopEmail, strPassword).addOnCompleteListener(home.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    boolean emailVerified = user.isEmailVerified();
                                    if(emailVerified == false){
                                        new AlertDialog.Builder(home.this)
                                                .setTitle("Email Verification")
                                                .setMessage("Please verify your email id")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                }).show();
                                    }
                                }
                            }
                            else{
                                Toast.makeText(home.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx//

        dl = (DrawerLayout) findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this,dl,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.aboutEreceipt){
                    startAboutEreceiptActivity();
                }
                else if(id == R.id.updatePassword){
                    startUpdatePasswordActivity();
                }
                else if (id == R.id.updateShopDetails){
                    startUpdateShopDetailsActivity();
                }
                else if (id == R.id.updateShopLogo){
                    startUpdateShopLogoActivity();
                }
                else if(id == R.id.contactUs){
                    startContactUsActivity();
                }
                else if(id == R.id.Logout){
                    if(flag == 1 || flagLogin == 1){
                        FirebaseAuth.getInstance().signOut();
                        startLogoutActivity();
                    }
                    else if(flag == 2 || flagLogin == 2){
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("LOGIN");
                        editor.commit();
                        FirebaseAuth.getInstance().signOut();
                        startLogoutActivity();
                    }
                    else{
                        startSmwActivity();
                    }
                }
                return false;
            }
        });

        final BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.invoice){
                    startInvoiceActivity();
                }
                else if(id == R.id.transferMoney){
                    startTransferMoneyActivity();
                }
                else if(id == R.id.customerDetails){
                    startPastInvoiceActivity();
                }
                return false;
            }
        });
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
    public void startInvoiceActivity(){
        Intent intent = new Intent(this, customerDetail.class);
        startActivity(intent);
    }

    public void startUpdatePasswordActivity(){
        Intent intent = new Intent(this, updatePassword.class);
        startActivity(intent);
    }
    public void startUpdateShopDetailsActivity(){
        Intent intent = new Intent(this, updateShopDetails.class);
        startActivity(intent);
    }
    public void startUpdateShopLogoActivity(){
        Intent intent = new Intent(this, updateLogo.class);
        startActivity(intent);
    }
    public void startContactUsActivity(){
        Intent intent = new Intent(this, contactUs.class);
        startActivity(intent);
    }
    public void startAboutEreceiptActivity(){
        Intent intent = new Intent(this, aboutPage.class);
        startActivity(intent);
    }
    public void startPastInvoiceActivity(){
        Intent intent = new Intent(this, pdfMainActivity.class);
        startActivity(intent);
    }

    public void startTransferMoneyActivity(){
        Intent intent = new Intent(this, transferMoney.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(home.this, "Access denied", Toast.LENGTH_SHORT).show();
    }
    public void startSmwActivity(){
        Intent intent = new Intent(this, somethingWentWrong.class);
        startActivity(intent);
    }

    public void startLogoutActivity(){
        flag = 0;
        flagLogin = 0;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

