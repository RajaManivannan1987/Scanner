package com.example.scanner.scanner.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.controller.AppController;
import com.example.scanner.scanner.view.CropImageView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class CropActivity extends AppCompatActivity {

    CropImageView cropImageView;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    ImageView submit;
    Long key, docId;
    String path = "";
    Long folderId = 0l;
    String TAG = CropActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Intent i = getIntent();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        submit = (ImageView) findViewById(R.id.cropActivityCropSubmitImageView);
        cropImageView = (CropImageView) findViewById(R.id.cropActivityCropImageView);
        cropImageView.setCropMode(CropImageView.CropMode.RATIO_FREE);
        cropImageView.setBackgroundColor(Color.BLACK);
        cropImageView.setFrameColor(Color.parseColor("#50AC90"));
        cropImageView.setOverlayColor(Color.TRANSPARENT);
        cropImageView.setGuideShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH);
        cropImageView.setGuideColor(Color.TRANSPARENT);
        cropImageView.setHandleColor(Color.parseColor("#9050AC90"));

        cropImageView.setHandleSizeInDp(100);

        // Set Image for Crop View Starts

        key = i.getExtras().getLong("key");
        path = i.getExtras().getString("path");
        folderId = i.getExtras().getLong(Global.folderId);

        File file = new File(path);
        Picasso.with(getApplicationContext())
                .load(file)
                .placeholder(R.drawable.default_error)
                .into(cropImageView);

        // Set Image for Crop View Ends

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppController) getApplication()).cropped = cropImageView.getCroppedBitmap();
                Intent i = new Intent(CropActivity.this, ImagingActivity.class);
                i.putExtra("key", key);
                i.putExtra("path", path);
                i.putExtra(Global.folderId, folderId);
                startActivity(i);
                finish();
            }
        });
    }
}
