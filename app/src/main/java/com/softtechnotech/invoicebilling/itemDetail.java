package com.softtechnotech.invoicebilling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class itemDetail extends AppCompatActivity {
    DatabaseHelper myDb;
    DatabaseReference rootRef, demoRef;
    Button next;
    EditText[] productName= new EditText[8];
    EditText[] qty= new EditText[8];
    EditText[] rate= new EditText[8];
    EditText[] hsn_sac_no= new EditText[8];
    static String[] strProductName = new String[8];
    static String[] strQty = new String[8];
    static String[] strRate = new String[8];
    static String[] strHsn_sac_no = new String[8];
    ProgressDialog nDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        myDb = new DatabaseHelper(this);
        productName[0] = findViewById(R.id.productName1);
        productName[1] = findViewById(R.id.productName2);
        productName[2] = findViewById(R.id.productName3);
        productName[3] = findViewById(R.id.productName4);
        productName[4] = findViewById(R.id.productName5);
        productName[5] = findViewById(R.id.productName6);
        productName[6] = findViewById(R.id.productName7);
        productName[7] = findViewById(R.id.productName8);

        qty[0] = findViewById(R.id.qty1);
        qty[1] = findViewById(R.id.qty2);
        qty[2] = findViewById(R.id.qty3);
        qty[3] = findViewById(R.id.qty4);
        qty[4] = findViewById(R.id.qty5);
        qty[5] = findViewById(R.id.qty6);
        qty[6] = findViewById(R.id.qty7);
        qty[7] = findViewById(R.id.qty8);

        rate[0] = findViewById(R.id.rate1);
        rate[1] = findViewById(R.id.rate2);
        rate[2] = findViewById(R.id.rate3);
        rate[3] = findViewById(R.id.rate4);
        rate[4] = findViewById(R.id.rate5);
        rate[5] = findViewById(R.id.rate6);
        rate[6] = findViewById(R.id.rate7);
        rate[7] = findViewById(R.id.rate8);

        hsn_sac_no[0] = findViewById(R.id.hsn_sac_no1);
        hsn_sac_no[1] = findViewById(R.id.hsn_sac_no2);
        hsn_sac_no[2] = findViewById(R.id.hsn_sac_no3);
        hsn_sac_no[3] = findViewById(R.id.hsn_sac_no4);
        hsn_sac_no[4] = findViewById(R.id.hsn_sac_no5);
        hsn_sac_no[5] = findViewById(R.id.hsn_sac_no6);
        hsn_sac_no[6] = findViewById(R.id.hsn_sac_no7);
        hsn_sac_no[7] = findViewById(R.id.hsn_sac_no8);

        next = findViewById(R.id.Item_next);

        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRef = rootRef.child("Invoice").child(home.userName);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nDialog = new ProgressDialog(itemDetail.this);
                nDialog.setMessage("Loading..");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(true);
                nDialog.show();

                for(int i=0; i<8; i++){
                    strProductName[i] = productName[i].getText().toString();
                    strQty[i] = qty[i].getText().toString();
                    strRate[i] = rate[i].getText().toString();
                    strHsn_sac_no[i] = hsn_sac_no[i].getText().toString();
                }
                startNextActivity();
            }
        });
    }
    public void startNextActivity(){
        Intent intent = new Intent(this, transactionDetail.class);
        startActivity(intent);
        nDialog.dismiss();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,customerDetail.class);
        startActivity(intent);
    }
}
