package com.softtechnotech.invoicebilling;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class transactionDetail extends AppCompatActivity {
    DatabaseHelper myDb;
    DatabaseReference rootRef, demoRef, demoRefCount;
    private StorageReference storageRef, dataRef;
    public  static String date, time;
    RelativeLayout invoicePage;
    LinearLayout tableHeader, bottomLine, bottomLine2;
    TextView invoiceNo, invoiceDate, cgst_tax, otherCharges, total, sgst_tax, amountCustomer, header;
    TextView[] qty= new TextView[8];
    TextView[] rate= new TextView[8];
    TextView[] amount= new TextView[8];
    TextView[] hsn_sac_no= new TextView[8];
    TextView[] sNo= new TextView[8];
    TextView[] description= new TextView[8];
    float[] t = new float[8];
    float tax1, ext1, tax2, totFinal;
    TextView shopName,customerName, customerMobile, customerAddress, shopAddress, shopMobile, shopEmail, gstNumber;
    ImageView shopLogo;
    private Button save;
    String dirpath, strBackground, strGstrate;
    public static int count;
    private Bitmap bitmap;
    private LinearLayout llPdf;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.right_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.blue:
                header.setBackgroundResource(R.color.lightBlue);
                tableHeader.setBackgroundResource(R.color.lightBlue);
                bottomLine.setBackgroundResource(R.color.lightBlue);
                bottomLine2.setBackgroundResource(R.color.lightBlue);
                break;
            case R.id.red:
                header.setBackgroundResource(R.color.lightRed);
                tableHeader.setBackgroundResource(R.color.lightRed);
                bottomLine.setBackgroundResource(R.color.lightRed);
                bottomLine2.setBackgroundResource(R.color.lightRed);
                break;
            case R.id.green:
                header.setBackgroundResource(R.color.lightGreen);
                tableHeader.setBackgroundResource(R.color.lightGreen);
                bottomLine.setBackgroundResource(R.color.lightGreen);
                bottomLine2.setBackgroundResource(R.color.lightGreen);
                break;
            case R.id.violet:
                header.setBackgroundResource(R.color.lightViolet);
                tableHeader.setBackgroundResource(R.color.lightViolet);
                bottomLine.setBackgroundResource(R.color.lightViolet);
                bottomLine2.setBackgroundResource(R.color.lightViolet);
                break;
            case R.id.yellow:
                header.setBackgroundResource(R.color.lightYellow);
                tableHeader.setBackgroundResource(R.color.lightYellow);
                bottomLine.setBackgroundResource(R.color.lightYellow);
                bottomLine2.setBackgroundResource(R.color.lightYellow);
                break;
            case R.id.skyblue:
                header.setBackgroundResource(R.color.lightSkyBlue);
                tableHeader.setBackgroundResource(R.color.lightSkyBlue);
                bottomLine.setBackgroundResource(R.color.lightSkyBlue);
                bottomLine2.setBackgroundResource(R.color.lightSkyBlue);
                break;
            case R.id.orange:
                header.setBackgroundResource(R.color.lightOrange);
                tableHeader.setBackgroundResource(R.color.lightOrange);
                bottomLine.setBackgroundResource(R.color.lightOrange);
                bottomLine2.setBackgroundResource(R.color.lightOrange);
                break;
            case R.id.grey:
                header.setBackgroundResource(R.color.lightGrey);
                tableHeader.setBackgroundResource(R.color.lightGrey);
                bottomLine.setBackgroundResource(R.color.lightGrey);
                bottomLine2.setBackgroundResource(R.color.lightGrey);
                break;
            case R.id.indigo:
                header.setBackgroundResource(R.color.lightIndigo);
                tableHeader.setBackgroundResource(R.color.lightIndigo);
                bottomLine.setBackgroundResource(R.color.lightGrey);
                bottomLine2.setBackgroundResource(R.color.lightGrey);
                break;
            default:
                header.setBackgroundResource(R.color.colorwhite);
                tableHeader.setBackgroundResource(R.color.colorwhite);
                bottomLine.setBackgroundResource(R.color.colorwhite);
                bottomLine2.setBackgroundResource(R.color.colorwhite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        myDb = new DatabaseHelper(this);
        invoicePage = (RelativeLayout)findViewById(R.id.invoicePage);
        invoiceNo = (TextView) findViewById(R.id.invoiceNo);
        invoiceDate = (TextView)findViewById(R.id.invoiceDate);
        customerName = (TextView) findViewById(R.id.customerName);
        customerMobile = (TextView) findViewById(R.id.customerMobile);
        customerAddress = (TextView) findViewById(R.id.customerAddress);
        header = findViewById(R.id.header);
        tableHeader = findViewById(R.id.table_header);
        bottomLine = findViewById(R.id.bottom_line);
        bottomLine2 = findViewById(R.id.bottom_line2);





        sNo[0] = (TextView)findViewById(R.id.sNo1);
        description[0] = (TextView)findViewById(R.id.description1);
        qty[0] = (TextView)findViewById(R.id.qty1);
        rate[0] = (TextView)findViewById(R.id.rate1);
        hsn_sac_no[0] = (TextView)findViewById(R.id.hsn_sac_no1);
        amount[0] = findViewById(R.id.amount1);
        amount[0].setEnabled(false);

        sNo[1] = (TextView)findViewById(R.id.sNo2);
        description[1] = (TextView)findViewById(R.id.description2);
        qty[1] = (TextView)findViewById(R.id.qty2);
        rate[1] = (TextView)findViewById(R.id.rate2);
        hsn_sac_no[1] = (TextView)findViewById(R.id.hsn_sac_no2);
        amount[1] = findViewById(R.id.amount2);
        amount[1].setEnabled(false);

        sNo[2] = (TextView)findViewById(R.id.sNo3);
        description[2] = (TextView)findViewById(R.id.description3);
        qty[2] = (TextView)findViewById(R.id.qty3);
        rate[2] = (TextView)findViewById(R.id.rate3);
        hsn_sac_no[2] = (TextView)findViewById(R.id.hsn_sac_no3);
        amount[2] = findViewById(R.id.amount3);
        amount[2].setEnabled(false);

        sNo[3] = (TextView)findViewById(R.id.sNo4);
        description[3] = (TextView)findViewById(R.id.description4);
        qty[3] = (TextView)findViewById(R.id.qty4);
        rate[3] = (TextView)findViewById(R.id.rate4);
        hsn_sac_no[3] = (TextView)findViewById(R.id.hsn_sac_no4);
        amount[3] = findViewById(R.id.amount4);
        amount[3].setEnabled(false);

        sNo[4] = (TextView)findViewById(R.id.sNo5);
        description[4] = (TextView)findViewById(R.id.description5);
        qty[4] = (TextView)findViewById(R.id.qty5);
        rate[4] = (TextView)findViewById(R.id.rate5);
        hsn_sac_no[4] = (TextView)findViewById(R.id.hsn_sac_no5);
        amount[4] = findViewById(R.id.amount5);
        amount[4].setEnabled(false);

        sNo[5] = (TextView)findViewById(R.id.sNo6);
        description[5] = (TextView)findViewById(R.id.description6);
        qty[5] = (TextView)findViewById(R.id.qty6);
        rate[5] = (TextView)findViewById(R.id.rate6);
        hsn_sac_no[5] = (TextView)findViewById(R.id.hsn_sac_no6);
        amount[5] = findViewById(R.id.amount6);
        amount[5].setEnabled(false);

        sNo[6] = (TextView)findViewById(R.id.sNo7);
        description[6] = (TextView)findViewById(R.id.description7);
        qty[6] = (TextView)findViewById(R.id.qty7);
        rate[6] = (TextView)findViewById(R.id.rate7);
        hsn_sac_no[6] = (TextView)findViewById(R.id.hsn_sac_no7);
        amount[6] = findViewById(R.id.amount7);
        amount[6].setEnabled(false);

        sNo[7] = (TextView)findViewById(R.id.sNo8);
        description[7] = (TextView)findViewById(R.id.description8);
        qty[7] = (TextView)findViewById(R.id.qty8);
        rate[7] = (TextView)findViewById(R.id.rate8);
        hsn_sac_no[7] = (TextView)findViewById(R.id.hsn_sac_no8);
        amount[7] = findViewById(R.id.amount8);
        amount[7].setEnabled(false);

        for(int i = 0; i<8;i++) {
            if (!itemDetail.strRate[i].equals("")) {
                description[i].setText(itemDetail.strProductName[i]);
                qty[i].setText(itemDetail.strQty[i]);
                rate[i].setText(itemDetail.strRate[i]);
            }
            else{
                break;
            }
        }

        cgst_tax = findViewById(R.id.cgst_tax);
        sgst_tax = findViewById(R.id.sgst_tax);
        otherCharges = findViewById(R.id.otherCharges);
        total = (TextView)findViewById(R.id.total);
        total.setEnabled(false);
        sgst_tax = (TextView)findViewById(R.id.sgst_tax);
        amountCustomer = (TextView)findViewById(R.id.amountCustomer);
        amountCustomer.setEnabled(false);
        shopName = (TextView)findViewById(R.id.shopName);
        shopAddress = (TextView)findViewById(R.id.shopAddress);
        shopMobile = (TextView)findViewById(R.id.shopMobile);
        shopEmail = (TextView)findViewById(R.id.shopEmail);
        gstNumber = (TextView)findViewById(R.id.gstNumber);
        shopLogo = (ImageView)findViewById(R.id.shopLogo);

        shopName = findViewById(R.id.shopName);
        llPdf = findViewById(R.id.ll_bill);


        save = (Button) findViewById(R.id.save);
        strBackground = customerDetail.strRBackground;
        switch (strBackground) {
            case "Blue":
                header.setBackgroundResource(R.color.lightBlue);
                tableHeader.setBackgroundResource(R.color.lightBlue);
                bottomLine.setBackgroundResource(R.color.lightBlue);
                bottomLine2.setBackgroundResource(R.color.lightBlue);
                break;
            case "Red":
                header.setBackgroundResource(R.color.lightRed);
                tableHeader.setBackgroundResource(R.color.lightRed);
                bottomLine.setBackgroundResource(R.color.lightRed);
                bottomLine2.setBackgroundResource(R.color.lightRed);
                break;
            case "Green":
                header.setBackgroundResource(R.color.lightGreen);
                tableHeader.setBackgroundResource(R.color.lightGreen);
                bottomLine.setBackgroundResource(R.color.lightGreen);
                bottomLine2.setBackgroundResource(R.color.lightGreen);
                break;
            case "Violet":
                header.setBackgroundResource(R.color.lightViolet);
                tableHeader.setBackgroundResource(R.color.lightViolet);
                bottomLine.setBackgroundResource(R.color.lightViolet);
                bottomLine2.setBackgroundResource(R.color.lightViolet);
                break;
            case "Yellow":
                header.setBackgroundResource(R.color.lightYellow);
                tableHeader.setBackgroundResource(R.color.lightYellow);
                bottomLine.setBackgroundResource(R.color.lightYellow);
                bottomLine2.setBackgroundResource(R.color.lightYellow);
                break;
            case "Sky Blue":
                header.setBackgroundResource(R.color.lightSkyBlue);
                tableHeader.setBackgroundResource(R.color.lightSkyBlue);
                bottomLine.setBackgroundResource(R.color.lightSkyBlue);
                bottomLine2.setBackgroundResource(R.color.lightSkyBlue);
                break;
            case "Orange":
                header.setBackgroundResource(R.color.lightOrange);
                tableHeader.setBackgroundResource(R.color.lightOrange);
                bottomLine.setBackgroundResource(R.color.lightOrange);
                bottomLine2.setBackgroundResource(R.color.lightOrange);
                break;
            case "Grey":
                header.setBackgroundResource(R.color.lightGrey);
                tableHeader.setBackgroundResource(R.color.lightGrey);
                bottomLine.setBackgroundResource(R.color.lightGrey);
                bottomLine2.setBackgroundResource(R.color.lightGrey);
                break;
            case "Indigo":
                header.setBackgroundResource(R.color.lightIndigo);
                tableHeader.setBackgroundResource(R.color.lightIndigo);
                bottomLine.setBackgroundResource(R.color.lightIndigo);
                bottomLine2.setBackgroundResource(R.color.lightIndigo);
                break;
            default:
                header.setBackgroundResource(R.color.colorwhite);
                tableHeader.setBackgroundResource(R.color.colorwhite);
                bottomLine.setBackgroundResource(R.color.colorwhite);
                bottomLine2.setBackgroundResource(R.color.colorwhite);
                break;
        }

        strGstrate = customerDetail.strGstrate;
        switch (strGstrate) {
            case "5%":
                cgst_tax.setText(Double.toString(2.5));
                sgst_tax.setText(Double.toString(2.5));
                break;
            case "12%":
                cgst_tax.setText(Integer.toString(6));
                sgst_tax.setText(Integer.toString(6));
                break;
            case "18%":
                cgst_tax.setText(Integer.toString(9));
                sgst_tax.setText(Integer.toString(9));
                break;
            case "28%":
                cgst_tax.setText(Integer.toString(14));
                sgst_tax.setText(Integer.toString(14));
                break;
            default:
                cgst_tax.setText(Integer.toString(0));
                sgst_tax.setText(Integer.toString(0));
                break;
        }

        customerName.setText(customerDetail.strCustomerName);
        customerMobile.setText(customerDetail.strCustomerMobile);
        customerAddress.setText(customerDetail.strCustomerAddress + " " + customerDetail.strCustomerPincode);


        date = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
        invoiceDate.setText(date);
        invoiceDate.setEnabled(false);
        time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        storageRef = FirebaseStorage.getInstance().getReference().child("shopLogo").child(home.userName);
        dataRef = storageRef.child(home.userName + ".jpg");
        dataRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(transactionDetail.this).load(uri).into(shopLogo);
            }
        });
        byte[] data = new byte[0];
        Cursor res1 = myDb.getImageInfo(home.preShopEmail);
        if(res1 != null && res1.getCount() > 0){
            res1.moveToFirst();
            data = res1.getBlob(1);
            res1.moveToNext();
        }
        Bitmap bitmaps = getImage(data);
        shopLogo.setImageBitmap(bitmaps);

        rootRef = FirebaseDatabase.getInstance().getReference();
        demoRefCount = rootRef.child("Invoice").child(home.userName).child("count");
        demoRefCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = Integer.parseInt(dataSnapshot.getValue().toString()) + 1;
                invoiceNo.setText(Integer.toString(count));
                Log.d("Veenu_count", String.valueOf(count));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                startErrorActivity();
            }
        });

        String strShopName = null, strShopAddress = null, strShopMobile = null, strShopEmail = null, strGstNumber = null, strPincode = null;
        Cursor res = myDb.getInfo(home.preShopEmail);
        if(res != null && res.getCount() > 0){
            res.moveToFirst();
            do{
                strShopEmail = res.getString(0);
                strShopName = res.getString(1);
                strShopMobile = res.getString(2);
                strShopAddress = res.getString(3);
                strPincode = res.getString(4);
                strGstNumber = res.getString(5);
            }while(res.moveToNext());
        }
        shopName.setText(strShopName);
        shopEmail.setText(strShopEmail);
        shopAddress.setText(strShopAddress + " " + strPincode);
        shopMobile.setText(strShopMobile);
        gstNumber.setText(strGstNumber);
        invoiceNo.setText(Integer.toString(count));


        demoRef = rootRef.child("Invoice").child(home.userName).child("shopDetail");
        demoRef.child("shopName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shopName.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                startErrorActivity();
            }
        });
        demoRef.child("shopAddress").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shopAddress.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                startErrorActivity();
            }
        });
        demoRef.child("shopMobile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shopMobile.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                startErrorActivity();
            }
        });

        demoRef.child("shopEmail").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shopEmail.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                startErrorActivity();
            }
        });
        demoRef.child("gstNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gstNumber.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                startErrorActivity();
            }
        });

        for(int i= 0; i<8; i++){
            amount[i].setText(calc_amt(i));
            Log.d("veenu",amount[i].getText().toString());
            if(amount[i].getText().length()==0){
                t[i]=0;
            }
            else{
                t[i] = Float.parseFloat(amount[i].getText().toString());
            }
        }
        totFinal = 0;
        for(float val:t){
            totFinal+=val;
        }
        total.setText(Float.toString(totFinal));
        amountCustomer.setText(calcFinal());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("size"," "+llPdf.getWidth() +"  "+llPdf.getWidth());
                bitmap = loadBitmapFromView(llPdf, llPdf.getWidth(), llPdf.getHeight());
                demoRefCount.setValue(count);
                save.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                createPdf(customerDetail.strCustomerName);
            }
        });
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private void createPdf(String strCustomerName){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);


        // write the document content
        dirpath = Environment.getExternalStorageDirectory().toString();
        File f = new File(Environment.getExternalStorageDirectory() +"/Invoice/" + date);
        if (!f.mkdirs()) {
            Toast.makeText(this,
                    f.getName() + " could not be created", Toast.LENGTH_LONG).show();
        }
        String targetPdf = dirpath +"/Invoice" + "/" + date+ "/" + time.replace(":","") + "_" + strCustomerName + ".pdf";
        File filePath;
        filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e, Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show();
        startSaveActivity();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,itemDetail.class);
        startActivity(intent);
    }

    public void startSaveActivity(){
        Intent intent = new Intent(this, invoiceSend.class);
        startActivity(intent);
    }
    public void startErrorActivity(){
        Intent intent = new Intent(this, offline.class);
        startActivity(intent);
        save.setVisibility(View.VISIBLE);
    }


    private String calc_amt(int i) {
        Log.d("veenu_qty",qty[i].getText().toString());
        if(qty[i].getText().toString().equals("") || qty[i].getText().length() == 0){
            return "";
        }
        float number1;
        float number2;
        if(!qty[i].getText().toString().equals("") && qty[i].getText().length() > 0) {
            number1 = Float.parseFloat(qty[i].getText().toString());
        } else {
            number1 = 0;
        }
        if(!rate[i].getText().toString().equals("") && rate[i].getText().length() > 0) {
            number2 = Float.parseFloat(rate[i].getText().toString());
        } else {
            number2 = 0;
        }
        sNo[i].setText(Integer.toString(i+1));
        return Float.toString(number1 * number2);
    }
    private String calcFinal() {
        if(!total.getText().toString().equals("") && total.getText().length() > 0) {
            totFinal = Float.parseFloat(total.getText().toString());
        } else {
            totFinal = 0;
        }
        if(!otherCharges.getText().toString().equals("") && otherCharges.getText().length() > 0) {
            ext1 = Float.parseFloat(otherCharges.getText().toString());
        } else {
            ext1 = 0;
        }
        if(!cgst_tax.getText().toString().equals("") && cgst_tax.getText().length() > 0) {
            tax1 = Float.parseFloat(cgst_tax.getText().toString());
        } else {
            tax1 = 0;
        }
        if(!sgst_tax.getText().toString().equals("") && sgst_tax.getText().length() > 0) {
            tax2 = Float.parseFloat(sgst_tax.getText().toString());
        } else {
            tax2 = 0;
        }
        return Float.toString(totFinal + ext1 + (totFinal * tax1) / 100 + (totFinal * tax2) / 100);
    }
}
