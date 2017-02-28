package com.example.scanner.scanner.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.scanner.scanner.R;

/**
 * Created by Im033 on 2/28/2017.
 */

public class AboutActivity extends AppCompatActivity {

    private Button clickButton;
    private Button buyButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        buyButton = (Button) findViewById(R.id.buyButton);
        clickButton = (Button) findViewById(R.id.clickButton);
        clickButton.setEnabled(false);
    }

    public void buttonClicked(View v) {
        clickButton.setEnabled(false);
        buyButton.setEnabled(true);
    }
}
