package com.example.scanner.scanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;
import com.example.scanner.scanner.asynctask.DownloadFile;
import com.example.scanner.scanner.asynctask.OCRTess;
import com.example.scanner.scanner.interfaceclass.OcrInterface;
import com.squareup.picasso.Picasso;

import java.io.File;


public class ImagePreviewActivity extends AppCompatActivity {

    String path = "";
    File file;
    ImageView mImageView, back, share, export;
    private String TAG = "ImagePreviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        mImageView = (ImageView) findViewById(R.id.iv_photo);
        back = (ImageView) findViewById(R.id.backButton);
        share = (ImageView) findViewById(R.id.share);
        export = (ImageView) findViewById(R.id.export);

        Intent intent = getIntent();
        path = intent.getExtras().getString("path");
        file = new File(path);
        Picasso.with(getApplicationContext()).load(file).placeholder(R.drawable.default_error).into(mImageView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(getResources().getString(R.string.intent_share_save_to_gallery));
//                shareIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//                shareIntent.setType("image/*");
////                startActivity(Intent.createChooser(shareIntent, "Share Image"));
//
//                Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                sendIntent.setType("image/*");
//                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//                Intent chooserIntent = Intent.createChooser(sendIntent, "Share Image");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{shareIntent});
//                startActivity(chooserIntent);

                Intent intent1=new Intent(ImagePreviewActivity.this,ExportActivity.class);
                intent1.putExtra("path",path);
                startActivity(intent1);


//                sendIntent.setDataAndType(uri, type);
//                editIntent.setDataAndType(uri, type);
//                Intent openInChooser = Intent.createChooser(viewIntent, "Open in...");

            }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().toString() + "/Scan Rite/tessdata/eng.traineddata";
                File tscFile = new File(path);
                if (tscFile.exists()) {
                    try {
                        new OCRTess(ImagePreviewActivity.this, new OcrInterface() {
                            @Override
                            public void result(String result) {
                                if (!result.equalsIgnoreCase(""))
                                    startActivity(new Intent(ImagePreviewActivity.this, ResultTessractActivity.class).putExtra("text", result));
                                else
                                    Toast.makeText(ImagePreviewActivity.this, "No text is detected", Toast.LENGTH_SHORT).show();
                            }
                        }).execute(file.getAbsolutePath());
                    } catch (Exception e) {
                        Log.e(TAG, e + "");
                    }
                } else {
                    new DownloadFile(ImagePreviewActivity.this).execute(Global.tesseractOcr);
                }
            }
        });
    }

}
