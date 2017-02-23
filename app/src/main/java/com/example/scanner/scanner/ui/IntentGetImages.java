package com.example.scanner.scanner.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.activity.CameraActivity;
import com.example.scanner.scanner.adapter.DatabaseAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IM021 on 11/25/2015.
 */
public class IntentGetImages extends Activity {
    DatabaseAdapter db;
    String TAG="Intent Get Images";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db=new DatabaseAdapter(this);
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.w(TAG,"Action:"+action);
        Log.w(TAG,"Type:"+type);
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Log.w(TAG,"Inside Action send");
             if (type.startsWith("image/")) {
                 Log.w(TAG,"Inside Image");
                handleSendImage(intent); // Handle single image being sent
            }else {
                 Toast.makeText(getApplicationContext(),"Category Type not accepted",Toast.LENGTH_SHORT).show();
             }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            Log.w(TAG,"Inside Action send multiple");
            if (type.startsWith("image/")) {
                Log.w(TAG,"Inside Image");
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }else{
                Toast.makeText(getApplicationContext(),"Category Type not accepted",Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle other intents, such as being started from the home screen
            Toast.makeText(getApplicationContext(),"Action is not accepted",Toast.LENGTH_SHORT).show();
        }
        finish();
        overridePendingTransition(0, 0);
    }
    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            File file=new File(getRealPathFromURI(imageUri));
            try {
                File desFile=Global.getOutputMediaFile(0,getApplicationContext());
                Global.copyDirectory(file, desFile);
                db.insertDefaultLocation(desFile.getAbsolutePath());
                Toast.makeText(getApplicationContext(), "Image saved to snapshot", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(IntentGetImages.this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            } catch (IOException e) {
                Log.e(TAG,e.toString());
            }
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
            int i=1;
            for (Uri imageUri:imageUris) {
                File file = new File(getRealPathFromURI(imageUri));
                try {
                    File desFile = Global.getOutputMediaFile(i, getApplicationContext());
                    i++;
                    Global.copyDirectory(file, desFile);
                    db.insertDefaultLocation(desFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
            startActivity(new Intent(IntentGetImages.this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            Toast.makeText(getApplicationContext(), "Images saved to snapshot", Toast.LENGTH_SHORT).show();
        }
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
