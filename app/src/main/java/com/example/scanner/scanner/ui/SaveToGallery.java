package com.example.scanner.scanner.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.scanner.scanner.Global;
import com.example.scanner.scanner.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IM021 on 11/25/2015.
 */
public class SaveToGallery extends Activity {
    private String TAG="SaveToGallery";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (getResources().getString(R.string.intent_share_save_to_gallery).equals(action) && type != null) {
             if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }else {
                 Toast.makeText(getApplicationContext(),"share rejected",Toast.LENGTH_SHORT).show();
             }
        } else if (getResources().getString(R.string.intent_share_save_to_gallery_multiple).equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
            Toast.makeText(getApplicationContext(),"share rejected",Toast.LENGTH_SHORT).show();
        }
        finish();
        overridePendingTransition(0, 0);
    }
    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            File file=new File(imageUri.getPath());
            try {
                Global.copyDirectory(file, new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Pictures", "IMG-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".jpg"));
                Toast.makeText(getApplicationContext(), "File saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
            for(int i=0;i<imageUris.size();i++){
                File file=new File(imageUris.get(i).getPath());
                try {
                    Global.copyDirectory(file, new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Pictures", "IMG-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) +i+ ".jpg"));
                } catch (IOException e) {
                    Log.e(TAG,e.getMessage());
                }
            }
            Toast.makeText(getApplicationContext(), "File saved to gallery", Toast.LENGTH_SHORT).show();
        }
    }
}
