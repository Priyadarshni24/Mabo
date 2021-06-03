package com.advengers.mabo.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.advengers.mabo.Activity.CreatePostActivity;
import com.advengers.mabo.R;
import com.advengers.mabo.Utils.LogUtils;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class VideoSlideAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> imagelist = new ArrayList<>();
    TagVideo tagImagecallback;

    public VideoSlideAdapter(Context context, ArrayList<String> list){
        this.context=context;
        this.imagelist = list;
    }


    @Override
    public int getCount() {
        return imagelist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.adapter_videoupload, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.iv_image_icon);
        ImageView btnclose = (ImageView) view.findViewById(R.id.btn_close);

        try {
            slideImageView.setImageBitmap(CreatePostActivity.retriveVideoFrameFromVideo(imagelist.get(position)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagImagecallback.onVideoTag(position);
            }
        });

        container.addView(view);

        return view;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);  //todo: RelativeLayout??
    }
    public void tagVideoCallBackListener(VideoSlideAdapter.TagVideo mCallback) {
        this.tagImagecallback = mCallback;
    }

    public interface TagVideo
    {
        public void onVideoTag(int position);
    }
}
