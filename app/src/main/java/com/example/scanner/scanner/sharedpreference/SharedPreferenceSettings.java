package com.example.scanner.scanner.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceSettings {

    private static final String PREF_NAME = "ScannerSettings";
    private final String defaultPageSize = "defaultPageSize";
    private final String autoImageEnhancement = "autoImageEnhancement";
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SharedPreferenceSettings(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setDefaultPageSize(int position) {
        editor.putInt(defaultPageSize, position);
        editor.commit();
    }

    public int getDefaultPageSize() {
        return pref.getInt(defaultPageSize, 0);
    }

    public void setAutoImageEnhancement(boolean isDo) {
        editor.putBoolean(autoImageEnhancement, isDo);
        editor.commit();
    }

    public boolean getAutoImageEnhancement() {
        return pref.getBoolean(autoImageEnhancement, true);
    }
}
