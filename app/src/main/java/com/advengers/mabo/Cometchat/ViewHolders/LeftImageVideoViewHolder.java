package com.advengers.mabo.Cometchat.ViewHolders;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Cometchat.CustomView.CircleImageView;
import com.advengers.mabo.R;
import com.advengers.mabo.Cometchat.Utils.Logger;

import static android.content.Context.WINDOW_SERVICE;

public class LeftImageVideoViewHolder extends RecyclerView.ViewHolder{
    private static final String TAG = LeftImageVideoViewHolder.class.getSimpleName();
    public TextView messageTimeStamp;
    public TextView senderName;
    public TextView imageTitle;
    public CircleImageView avatar;
    public View imageContainer;
    public ImageButton btnPlayVideo;
    public ImageView imageMessage,leftArrow;
    public Guideline leftGuideLine;
    public ProgressBar fileLoadingProgressBar;
    public LeftImageVideoViewHolder(Context context, View leftImageMessageView) {
        super(leftImageMessageView);
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();
        Logger.error(TAG, "LeftImageVideoViewHolder: orientation: "+orientation);
        leftGuideLine = leftImageMessageView.findViewById(R.id.leftGuideline);
        if(orientation == 1 || orientation == 3){
            Logger.error(TAG, "LeftImageVideoViewHolder: Landscape Mode");
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) leftGuideLine.getLayoutParams();
            params.guidePercent = 0.5f;
            leftGuideLine.setLayoutParams(params);
        }
        messageTimeStamp = leftImageMessageView.findViewById(R.id.timeStamp);
        avatar = leftImageMessageView.findViewById(R.id.imgAvatar);
        imageContainer = leftImageMessageView.findViewById(R.id.imageContainer);
        btnPlayVideo = leftImageMessageView.findViewById(R.id.btnPlayVideo);
        imageMessage = leftImageMessageView.findViewById(R.id.imageMessage);
        senderName = leftImageMessageView.findViewById(R.id.senderName);
        fileLoadingProgressBar = leftImageMessageView.findViewById(R.id.fileName);
    }
}