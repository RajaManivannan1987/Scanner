package com.example.scanner.scanner.Utility;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Im033 on 12/30/2016.
 */

public class CommonMethods extends AppCompatActivity {

    public static void checkmarshmallowPermission(Activity activity, String[] permision, int requestCode) {
        for (int i = 0; i < permision.length; i++) {
            if (ActivityCompat.checkSelfPermission(activity, permision[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permision[i]}, requestCode);
            }
        }

    }
}
