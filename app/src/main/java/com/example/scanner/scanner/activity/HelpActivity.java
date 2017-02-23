package com.example.scanner.scanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.scanner.scanner.R;
import com.example.scanner.scanner.adapter.ViewPagerAdapter;


public class HelpActivity extends AppCompatActivity {
    ViewPager viewPager;
    Handler handler;
    Runnable mRunnable;
    public static final int DELAY = 5000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_help);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewPager = (ViewPager) findViewById(R.id.helpActivityViewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        Intent intent = getIntent();
        try {
            viewPager.setCurrentItem(intent.getIntExtra("position", 0), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        handler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getCurrentItem() == 2) {
                    onBackPressed();
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                }
            }
        };
        handler.postDelayed(mRunnable, DELAY);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("", position + "");
            }

            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks( mRunnable );
                handler.postDelayed(mRunnable, DELAY);
                Log.d("", position + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("", state + "");
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HelpActivity.this, MainActivity.class));
        handler.removeCallbacks( mRunnable );
        finish();
    }
}
