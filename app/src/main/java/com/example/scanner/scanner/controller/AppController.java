package com.example.scanner.scanner.controller;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.SparseArray;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by IM021 on 11/21/2015.
 */
public class AppController extends Application {
    public Bitmap cropped = null;

    public AppController() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        deleteMailFiles();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void deleteMailFiles() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString() + "/Scan Rite/Mail");
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        } else {
            String[] children = mediaStorageDir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    new File(mediaStorageDir, children[i]).delete();
                }
            }
        }
    }
}
