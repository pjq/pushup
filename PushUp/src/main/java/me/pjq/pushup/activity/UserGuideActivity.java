package me.pjq.pushup.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import me.pjq.pushup.AppPreference;
import me.pjq.pushup.R;
import me.pjq.pushup.navigation.MainActivity;
import me.pjq.pushup.widget.CustomerViewPager;

public class UserGuideActivity extends Activity {

    private static final int MAX_PAGE = 3;

    public static final String EXTRAS_START_FROM = "from";
    public static final int START_FROM_SPLASH= 100;
    public static final int START_FROM_SETTINGS = START_FROM_SPLASH + 1;
    private int mStartFrom;
    private CustomerViewPager mCustomerViewPager;
    private MyPagerAdapter mMyPagerAdapter;
    private ImageView mIndexImageView;
    private boolean mIsFinished = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.page_user_guide);
        mCustomerViewPager = (CustomerViewPager) findViewById(R.id.guide_view_pager);
        mIndexImageView = (ImageView) findViewById(R.id.page_user_guide_index_imageview);
        mIndexImageView.setImageLevel(0);
        mMyPagerAdapter = new MyPagerAdapter();
        mCustomerViewPager.setAdapter(mMyPagerAdapter);
        mCustomerViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
               mIndexImageView.getBackground().setLevel(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if(arg0==(MAX_PAGE-1)){
                    if(mIsFinished == false && arg1>0.1){
                        mIsFinished = true;
                        finishUserGuide();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        mStartFrom = getIntent().getIntExtra(EXTRAS_START_FROM, START_FROM_SPLASH);

        // Already show the user guide.
        AppPreference.getInstance(getApplicationContext()).setShouldShowUserGuard(false);
    }

    private void finishUserGuide() {
        if (START_FROM_SPLASH == mStartFrom) {
            Intent intent = new Intent(UserGuideActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (START_FROM_SETTINGS == mStartFrom) {
            finish();
        } else {
            finish();
        }
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return MAX_PAGE + 1;
        }

        @Override
        public Object instantiateItem(View collection, int position) {

            LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            int resId = 0;
            View view = null;
            switch (position) {
            case 0:
                resId = R.layout.page_user_guide_item1;
                view = inflater.inflate(resId, null);
                break;
            case 1:
                resId = R.layout.page_user_guide_item2;
                view = inflater.inflate(resId, null);
                break;
            case 2:
                resId = R.layout.page_user_guide_item4;
                view = inflater.inflate(resId, null);

//                ImageView startImageView = (ImageView) view.findViewById(R.id.page_user_guide_start_imageview);
//
//                startImageView.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // finish();
//                        Intent intent = new Intent(UserGuideActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
                break;

            case MAX_PAGE:
                resId = R.layout.page_user_guide_empty;
                view = inflater.inflate(resId, null);
                break;
            }
            ((ViewPager) collection).addView(view, 0);

            return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);

        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finishUserGuide();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
