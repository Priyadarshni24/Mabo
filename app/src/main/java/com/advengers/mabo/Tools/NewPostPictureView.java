package com.advengers.mabo.Tools;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.advengers.mabo.R;
import com.advengers.mabo.Utils.Tools;
import com.andexert.library.RippleView;

import java.io.File;

public class NewPostPictureView extends RelativeLayout {
    LayoutInflater mInflater;

    ImageView image;
    RippleView deleteImage;
    private NewPostPicture mNewPostPicture;
    Context context;
    public NewPostPictureView(Context context) {
        super(context);
        this.context =context;
        mInflater = LayoutInflater.from(context);
        init();
    }


    public NewPostPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
        mInflater = LayoutInflater.from(context);
        init();
    }

    public NewPostPictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context =context;
        mInflater = LayoutInflater.from(context);
        init();
    }

    public void bind(final NewPostPicture newPostPicture) {
        mNewPostPicture = newPostPicture;
        image.setImageBitmap(mNewPostPicture.bitmap);
      //  deleteImage.setVisibility(!newPostPicture.showDeletete ? VISIBLE : GONE);
        deleteImage.setVisibility(VISIBLE);
        Tools.setAfterRippleClick(deleteImage, mNewPostPicture.onClickDelete);
    }
    public void init()
    {
        View v = mInflater.inflate(R.layout.activity_new_post_picture, this, true);
        deleteImage = (RippleView)v.findViewById(R.id.deleteImage);
        image = (ImageView) v.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    void image() {
        String filename = Tools.writeFileToInternalStorage(getContext(), mNewPostPicture.bitmap);
        Intent zoomint = new Intent(context, ImageZoomActivity.class);
        zoomint.putExtra("imageUri", getContext().getApplicationContext().getFilesDir() + File.separator + filename);
        context.startActivity(zoomint);

    }

}

