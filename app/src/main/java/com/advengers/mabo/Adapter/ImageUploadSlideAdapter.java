package com.advengers.mabo.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageUploadSlideAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Uri> imagelist = new ArrayList<>();
    TagImages tagImagecallback;

    public ImageUploadSlideAdapter(Context context, ArrayList<Uri> list){
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
        View view = layoutInflater.inflate(R.layout.adapter_imageupload, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.iv_image_icon);
        ImageView btnclose = (ImageView) view.findViewById(R.id.btn_close);

        Picasso.get().load(imagelist.get(position)).into(slideImageView);

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagImagecallback.onImageTag(position);
            }
        });

        container.addView(view);

        return view;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);  //todo: RelativeLayout??
    }
    public void tagImageCallBackListener(ImageUploadSlideAdapter.TagImages mCallback) {
        this.tagImagecallback = mCallback;
    }

    public interface TagImages
    {
        public void onImageTag(int position);
    }
}
