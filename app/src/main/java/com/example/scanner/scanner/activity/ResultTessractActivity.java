package com.example.scanner.scanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scanner.scanner.R;

public class ResultTessractActivity extends AppCompatActivity {
    private TextView textView;
    private ImageView shareImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_tessract);
        textView = (TextView) findViewById(R.id.text);
        textView.setText(getIntent().getExtras().getString("text"));

        shareImageView=(ImageView)findViewById(R.id.share);
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, textView.getText().toString());
                startActivity(Intent.createChooser(sharingIntent,"Share OCR using"));
            }
        });
    }
}
