package com.advengers.mabo.Tools;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.advengers.mabo.R;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class ImageZoomActivity extends MyActivity {
    Toolbar toolbar;
    SubsamplingScaleImageView image;
    String imageUri = null;
    void menuHome() {
        onBackPressed();
    }
    void saveImage() {

        try {
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(imageUri));
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "image.jpg");
            //TODO
            dm.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        image = (SubsamplingScaleImageView)findViewById(R.id.image);
        imageUri = getIntent().getStringExtra("imageUri");
        setup();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                menuHome();
                break;
            case R.id.action_image_zoom_save:
                saveImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    void setup() {


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageLoader.getInstance().loadImage(imageUri, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_image_loading);
                image.setImage(ImageSource.bitmap(icon));
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                image.setImage(ImageSource.bitmap(loadedImage));

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
       image.setMaxScale(8);
    }
}
