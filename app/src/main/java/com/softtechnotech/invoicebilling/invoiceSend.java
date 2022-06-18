package com.softtechnotech.invoicebilling;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;

public class invoiceSend extends AppCompatActivity {

    Button viewPage, send, makeAnotherInvoice;
    ImageView home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_send);
        viewPage = findViewById(R.id.view);
        send = findViewById(R.id.send);
        makeAnotherInvoice = findViewById(R.id.makeAnotherInvoice);
        home = findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeActivity();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFile();
            }
        });
        makeAnotherInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMakeInvoiceActivity();
            }
        });

        viewPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGeneratedPDF();
            }
        });
    }
    public void shareFile(){
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Invoice");
        File pdfFile = new File(docsFolder.getAbsolutePath(), transactionDetail.date + "/" + transactionDetail.time.replace(":","") + "_" + customerDetail.strCustomerName + ".pdf");
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(pdfFile.getAbsolutePath());
        if(fileWithinMyDir.exists()) {
            Uri screenshotUri = FileProvider.getUriForFile(invoiceSend.this, getApplicationContext().getPackageName() + ".provider", pdfFile);
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM,screenshotUri);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }
    public void startHomeActivity(){
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }
    public void startMakeInvoiceActivity(){
        Intent intent = new Intent(this, customerDetail.class);
        startActivity(intent);
    }
    private void openGeneratedPDF(){
        String dirpath = Environment.getExternalStorageDirectory().toString();
        String targetPdf = dirpath +"/Invoice" + "/" + transactionDetail.date+ "/" + transactionDetail.time.replace(":","") + "_" + customerDetail.strCustomerName + ".pdf";
        Log.d("Veenu_FilePath",targetPdf);
        File file = new File(targetPdf);
        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = GenericFileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",file);;
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(invoiceSend.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }
}
