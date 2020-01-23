package com.advengers.mabo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.advengers.mabo.Activity.CreatePostActivity;
import com.advengers.mabo.Activity.MainActivity;
import com.advengers.mabo.Activity.VideoPlayerMP4Activity;
import com.advengers.mabo.Interfaces.Keys;
import com.advengers.mabo.R;
import com.advengers.mabo.Utils.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.ArrayList;

import static com.advengers.mabo.Activity.CreatePostActivity.retriveVideoFrameFromVideo;
import static com.advengers.mabo.Activity.MainActivity.CLOUDBASEURL;
import static com.advengers.mabo.Activity.MainActivity.CLOUDVIDEOBASEURL;

public class PosrSliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> imagelist = new ArrayList<>();
    ArrayList<String> videolist = new ArrayList<>();
    Activity activity;
    public PosrSliderAdapter(Activity activity,Context context,ArrayList<String> list,ArrayList<String> video){
        this.context=context;
        this.activity = activity;
        this.imagelist = list;
        this.videolist = video;
        LogUtils.e(imagelist.size()+" "+videolist.size());
    }


    @Override
    public int getCount() {
        return imagelist.size()+videolist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.adapter_post, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.iv_image_icon);
        final ProgressBar pbload = (ProgressBar) view.findViewById(R.id.pb_loading);
        final ImageView btnplay = (ImageView) view.findViewById(R.id.btn_play);
        LogUtils.e(position+" "+(imagelist.size()-1));
        if(position <= (imagelist.size()-1))
        {
            Picasso.get().load(CLOUDBASEURL+ imagelist.get(position)).into(slideImageView,new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    pbload.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
                public void onError() {

                }
            });
        }else{
            LogUtils.e(CLOUDVIDEOBASEURL+ videolist.get(position-(imagelist.size())));
            try {
                btnplay.setVisibility(View.VISIBLE);
                btnplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent video = new Intent(activity,VideoPlayerMP4Activity.class);
                        video.putExtra(Keys.VIDEO_URL,videolist.get(position - (imagelist.size())));
                    activity.startActivity(video);
                    }
                });
                pbload.setVisibility(View.GONE);
                  Glide.with(context).load(CLOUDVIDEOBASEURL+ videolist.get(position-(imagelist.size()))).into(slideImageView);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);  //todo: RelativeLayout??
    }
}
