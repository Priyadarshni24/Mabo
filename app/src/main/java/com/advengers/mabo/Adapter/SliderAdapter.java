package com.advengers.mabo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.advengers.mabo.R;


public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context=context;
    }

    public int[] slideImages = {
            R.drawable.ic_discover,
            R.drawable.ic_share,
            R.drawable.ic_promotion,
            R.drawable.ic_video
    };

    public String[] slideHeadings ={
            "Discover",
            "Share",
            "Promote",
            "Video Call"

    };

    public String[] slideDescriptions ={
            "See intersting people close by or far away. Friends and strange alike.",
            "Leave your digital mark on a specific location. Enjoy other people post around you.",
            "Bring your message just where needed. Tell everyone what is happening near by.",
            "Receive the call while the app is running in foreground or background"
    };


    @Override
    public int getCount() {
        return slideHeadings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (LinearLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.adapter_introscreen, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.iv_image_icon);
        TextView slideHeading = (TextView) view.findViewById(R.id.tv_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.tv_description);

        slideImageView.setImageResource(slideImages[position]);
        slideHeading.setText(slideHeadings[position]);
        slideDescription.setText(slideDescriptions[position]);

        container.addView(view);

        return view;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);  //todo: RelativeLayout??
    }
}
