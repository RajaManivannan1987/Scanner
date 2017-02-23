package com.example.scanner.scanner.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.scanner.scanner.R;

/**
 * Created by IM021 on 12/30/2015.
 */
public class ViewPagerAdapter extends PagerAdapter {
    Activity activity;

    public ViewPagerAdapter(Activity activity) {
        this.activity = activity;
    }

    int[] screen = {R.drawable.splash_1, R.drawable.splash_2, R.drawable.splash_3};

    @Override
    public int getCount() {
        return screen.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ImageView imageView = new ImageView(activity);
//        Picasso.with(activity).load(screen[position]).into(imageView);
        imageView.setImageResource(screen[position]);
        imageView.setBackgroundColor(Color.BLACK);
        imageView.setLayoutParams(imageParams);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }


}
