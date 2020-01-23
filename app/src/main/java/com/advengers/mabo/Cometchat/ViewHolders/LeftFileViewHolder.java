package com.advengers.mabo.Cometchat.ViewHolders;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.RecyclerView;


import com.advengers.mabo.Cometchat.CustomView.CircleImageView;
import com.advengers.mabo.R;
import com.cometchat.pro.helpers.Logger;

import static android.content.Context.WINDOW_SERVICE;

public class LeftFileViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "LeftFileViewHolder";
    public TextView fileName;

    public TextView fileType;

    public TextView messageTimeStamp,senderName;
    public CircleImageView avatar;
    public View fileContainer;
    public Guideline leftGuideLine;


    public LeftFileViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();
        Logger.error(TAG, "LeftImageVideoViewHolder: orientation: "+orientation);
        leftGuideLine = itemView.findViewById(R.id.leftGuideline);
        if(orientation == 1 || orientation == 3){
            Logger.error(TAG, "LeftImageVideoViewHolder: Landscape Mode");
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) leftGuideLine.getLayoutParams();
            params.guidePercent = 0.5f;
            leftGuideLine.setLayoutParams(params);
        }
        fileType=itemView.findViewById(R.id.fileType);
        fileName=itemView.findViewById(R.id.fileName);
        messageTimeStamp = itemView.findViewById(R.id.timeStamp);
        avatar = itemView.findViewById(R.id.imgAvatar);
        fileContainer = itemView.findViewById(R.id.fileContainer);
        senderName = itemView.findViewById(R.id.senderName);

    }
}
