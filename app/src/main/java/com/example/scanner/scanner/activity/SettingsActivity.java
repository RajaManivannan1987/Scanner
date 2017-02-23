package com.example.scanner.scanner.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.scanner.scanner.R;
import com.example.scanner.scanner.sharedpreference.SharedPreferenceSettings;

public class SettingsActivity extends AppCompatActivity {
    private LinearLayout defaultPageLinearLayout,tellFriendLinearLayout,sendFeedBackLinearLayout;
    private Switch autoEnhanceSwitch;
    private TextView defaultPageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        defaultPageLinearLayout=(LinearLayout)findViewById(R.id.settingsActivityDefaultPageLinearLayout);
        tellFriendLinearLayout=(LinearLayout)findViewById(R.id.settingsActivityTellAFriendLinearLayout);
        sendFeedBackLinearLayout=(LinearLayout)findViewById(R.id.settingsActivitySendFeedbackLinearLayout);
        autoEnhanceSwitch=(Switch)findViewById(R.id.settingsActivityAutoEnhancementSwitch);
        defaultPageTextView=(TextView)findViewById(R.id.settingsActivityDefaultPageSizeTextView);

        defaultPageTextView.setText(getResources().getStringArray(R.array.doc_details_listview)[new SharedPreferenceSettings(this).getDefaultPageSize()]);
        autoEnhanceSwitch.setChecked(new SharedPreferenceSettings(this).getAutoImageEnhancement());
        defaultPageLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Select Default page size");
                builder.setItems(getResources().getStringArray(R.array.doc_details_listview), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        defaultPageTextView.setText(getResources().getStringArray(R.array.doc_details_listview)[item]);
                        new SharedPreferenceSettings(SettingsActivity.this).setDefaultPageSize(item);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        tellFriendLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
                emailIntent.putExtra(Intent.EXTRA_CC, "");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Scan Rite");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "This is nice App");
                startActivity(Intent.createChooser(emailIntent, "Tell your Friend"));
            }
        });
        autoEnhanceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new SharedPreferenceSettings(SettingsActivity.this).setAutoImageEnhancement(isChecked);
            }
        });
        sendFeedBackLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] TO = {"raja@imaginetventures.com"};

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                emailIntent.setData(Uri.parse("mailto:karthik@imaginetventures.com"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, "");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Scan Rite - Feed back");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
            }
        });
        findViewById(R.id.settingsActivityBackImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
