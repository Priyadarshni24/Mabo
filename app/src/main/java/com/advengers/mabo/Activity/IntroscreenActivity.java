package com.advengers.mabo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.advengers.mabo.Adapter.SliderAdapter;
import com.advengers.mabo.R;

public class IntroscreenActivity extends AppCompatActivity implements View.OnClickListener{

            private ViewPager mSlideViewPager;
            private LinearLayout mDotsLayout;

            private TextView[] mDots;

            private SliderAdapter sliderAdapter;

            private Button buttonNext,buttonSkip;

            private  int mCurrentPage;
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_introscreen);
                mSlideViewPager = (ViewPager) findViewById(R.id.slide_viewpager);
                mDotsLayout = (LinearLayout) findViewById(R.id.dots_layout);
                buttonNext = (Button) findViewById(R.id.btn_next);
                buttonSkip = (Button) findViewById(R.id.btn_skip);
                buttonNext.setEnabled(true);
                buttonSkip.setEnabled(true);
                buttonNext.setText("Continue");
                mDotsLayout.setVisibility(View.GONE);

                sliderAdapter = new SliderAdapter(this);
                mSlideViewPager.setAdapter(sliderAdapter);

               /* TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                tabLayout.setupWithViewPager(mSlideViewPager, true);*/
              //  addDotsIndicator(0);

                mSlideViewPager.addOnPageChangeListener(viewListener);
                buttonNext.setOnClickListener(this);
                buttonSkip.setOnClickListener(this);

            }

            public void addDotsIndicator(int position){

                mDots = new TextView[4];
                mDotsLayout.removeAllViews(); //without this multiple number of dots will be created

                for (int i = 0; i< 4; i++){
                    mDots[i] = new TextView(this);
                    mDots[i].setText(Html.fromHtml("&#8226;")); //code for the dot icon like thing
                    mDots[i].setTextSize(35);
                    mDots[i].setTextColor(getResources().getColor(R.color.grey));

                    mDotsLayout.addView(mDots[i]);
                }

                if (mDots.length>0){
                    mDots[position] .setTextColor(getResources().getColor(R.color.white)); //setting currently selected dot to white
                }
            }

            ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    //addDotsIndicator(position);

                    mCurrentPage = position;

                    if (position == 0){//we are on first page
                        buttonNext.setVisibility(View.INVISIBLE);
                        buttonSkip.setVisibility(View.VISIBLE);
                    } else if (position == 3){
                        buttonNext.setVisibility(View.VISIBLE);
                        buttonSkip.setVisibility(View.INVISIBLE);
                    } else {
                        buttonSkip.setVisibility(View.VISIBLE);
                        buttonNext.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };

            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_next:
                        startActivity(new Intent(IntroscreenActivity.this, LoginActivity.class));
                        finish();
                        break;
                    case R.id.btn_skip:
                        startActivity(new Intent(IntroscreenActivity.this, LoginActivity.class));
                        finish();
                        break;
                    default: break;
                }
            }
        }
