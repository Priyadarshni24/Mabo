package com.cometchat.pro.uikit.ui_components.messages.media_view;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;

import java.io.File;
import java.util.ArrayList;


import static com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants.IntentStrings.INTENT_MEDIA_MESSAGE;

public class CometChatImageViewActivity extends AppCompatActivity {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
   // private static final Integer[] IMAGES= {R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.five};
    private ArrayList<String> ImagesArray = new ArrayList<String>();
    String receivedimage,senderName;
    long sentAt;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comet_chat_image_view);
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.NAME))
            senderName = getIntent().getStringExtra(UIKitConstants.IntentStrings.NAME);
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.SENTAT))
            sentAt = getIntent().getLongExtra(UIKitConstants.IntentStrings.SENTAT,0);
        toolbar = findViewById(R.id.toolbar);
        toolbar = findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setTint(getResources().getColor(R.color.textColorWhite));
        toolbar.setTitle(senderName);
        toolbar.setSubtitle(Utils.getLastMessageDate(sentAt));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setTint(getResources().getColor(R.color.textColorWhite));
        toolbar.setTitle(getString(R.string.images));



        init();
    }
    private void init() {
       /* for(int i=0;i<IMAGES.length;i++)
            ImagesArray.add(IMAGES[i]);*/

        receivedimage = getIntent().getStringExtra(INTENT_MEDIA_MESSAGE);

        Log.e("Mabo","recvd image "+receivedimage);

        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();
        String targetPath = ExternalStorageDirectoryPath + "/Mabo/Mabo_Images";

        File targetDirector = new File(targetPath);

        File[] files = targetDirector.listFiles();
        ImagesArray.clear();
        for (File file : files) {

            ImagesArray.add(file.getAbsolutePath());
            String filename=file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")+1);
            if(receivedimage.equals(filename)){
                currentPage = ImagesArray.size()-1;
               Log.e("Mabo","Coming here"+filename+" "+currentPage);
            }
           // currentPage++;
      //      Log.e("Mabo","Coming here"+file.getAbsolutePath()+" "+currentPage+ " "+ImagesArray.size());
        }
        mPager = (ViewPager) findViewById(R.id.pager);


        mPager.setAdapter(new SlidingImage_Adapter(CometChatImageViewActivity.this,ImagesArray));

        mPager.setCurrentItem(currentPage,true);

      /*  CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);*/

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
   //     indicator.setRadius(5 * density);

        NUM_PAGES =ImagesArray.size();

        // Auto start of viewpager
       /* final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);*/

        // Pager listener over indicator
     /*   indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });*/

    }

}