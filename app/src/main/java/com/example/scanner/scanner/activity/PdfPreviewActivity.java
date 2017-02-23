package com.example.scanner.scanner.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by IM028 on 4/27/16.
 */
public class PdfPreviewActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView mWebView = new WebView(this);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.loadUrl(getIntent().getStringExtra("path"));
        setContentView(mWebView);
    }
}
