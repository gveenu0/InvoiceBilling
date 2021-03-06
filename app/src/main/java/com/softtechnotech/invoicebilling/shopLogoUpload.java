package com.softtechnotech.invoicebilling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class shopLogoUpload extends AppCompatActivity {
    DatabaseHelper myDb;
    Button next;
    ImageView upload;
    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private ProgressBar mProgressBar;
    String imageCheck = "notUploaded";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_logo_upload);

        myDb = new DatabaseHelper(this);
        next = findViewById(R.id.next);
        upload = findViewById(R.id.upload);
        mProgressBar = findViewById(R.id.progress_bar);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog nDialog;
                nDialog = new ProgressDialog(shopLogoUpload.this);
                nDialog.setMessage("Loading..");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(true);
                nDialog.show();
                if(imageCheck == "notUploaded"){
                    Toast.makeText(shopLogoUpload.this, "Attach logo of your shop",Toast.LENGTH_LONG).show();
                    nDialog.dismiss();
                    return;
                }
                else{
                    upload.buildDrawingCache();
                    Bitmap bitmap = upload.getDrawingCache();
                    byte[] data = getBitmapAsByteArray(bitmap);
                    if(myDb.insertImage(registerPage.strUsername, data)){
                        //Toast.makeText(shopLogoUpload.this, "Image uploaded to sqlit3", Toast.LENGTH_SHORT).show();
                        startSuccessActivity();
                        uploadFile();
                    }
                    else{
                        Toast.makeText(shopLogoUpload.this, "Error in image uploading", Toast.LENGTH_SHORT).show();
                        nDialog.dismiss();
                    }
                }

                mStorageRef = FirebaseStorage.getInstance().getReference("shopLogo/").child(registerPage.username);
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(shopLogoUpload.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                } else {
                    if(imageCheck == "notUploaded"){
                        Toast.makeText(shopLogoUpload.this, "Attach logo of your shop",Toast.LENGTH_LONG).show();
                        nDialog.dismiss();
                        return;
                    }
                    uploadFile();
                }
            }
        });
    }
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(upload);
            imageCheck = "uploaded";
        }
        else{
            imageCheck = "notUploaded";
        }
    }

    private void uploadFile(){
        if(upload != null){
            StorageReference fileReference = mStorageRef.child(registerPage.username + ".jpg");
            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(0);
                        }
                    },500);

                    Toast.makeText(shopLogoUpload.this, "Upload Successfull",Toast.LENGTH_LONG).show();
                    //startNextActivity();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(shopLogoUpload.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBar.setProgress((int) progress);
                }
            });
        }
        else {
            Toast.makeText(this,"No file selected",Toast.LENGTH_SHORT).show();
        }
    }
    public void startSuccessActivity(){
        Intent intent = new Intent(this, successRegister.class);
        startActivity(intent);
    }
}
