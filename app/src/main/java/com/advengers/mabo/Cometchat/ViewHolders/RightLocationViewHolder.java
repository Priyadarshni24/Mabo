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
import com.advengers.mabo.Cometchat.Utils.Logger;
import com.advengers.mabo.R;

import static android.content.Context.WINDOW_SERVICE;

public class RightLocationViewHolder extends RecyclerView.ViewHolder{
    private static final String TAG = RightImageVideoViewHolder.class.getSimpleName();
    public TextView messageTimeStamp,imageTitle;
    public ImageView imageMessage;
    public TextView txtPlace;
    public CircleImageView messageStatus;
    public View imageContainer;
    public ImageButton btnPlayVideo;
    public Guideline rightGuideLine;
    public ProgressBar fileLoadingProgressBar;
    public RightLocationViewHolder(Context context, View rightImageMessageView) {
        super(rightImageMessageView);
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();
        Logger.error(TAG, "RightLocationViewHolder: orientation: "+orientation);
        rightGuideLine = rightImageMessageView.findViewById(R.id.rightGuideline);
        if(orientation == 1 || orientation == 3){
            Logger.error(TAG, "RightLocationViewHolder: Landscape Mode");
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rightGuideLine.getLayoutParams();
            params.guidePercent = 0.5f;
            rightGuideLine.setLayoutParams(params);
        }
        messageTimeStamp = rightImageMessageView.findViewById(R.id.timeStamp);
        imageMessage = rightImageMessageView.findViewById(R.id.imageMessage);
        imageContainer = rightImageMessageView.findViewById(R.id.imageContainer);
        btnPlayVideo = rightImageMessageView.findViewById(R.id.btnPlayVideo);
        messageStatus = rightImageMessageView.findViewById(R.id.messageStatus);
        fileLoadingProgressBar = rightImageMessageView.findViewById(R.id.fileName);
        txtPlace = rightImageMessageView.findViewById(R.id.txt_place);
    }
}
