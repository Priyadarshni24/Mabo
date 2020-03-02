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
import com.advengers.mabo.Cometchat.Utils.Logger;

import static android.content.Context.WINDOW_SERVICE;

public class RightFileViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "RightFileViewHolder";
    public TextView fileType,fileName;

    public TextView messageTimeStamp,senderName;
    public CircleImageView avatar, messageStatus;;
    public View fileContainer;
    public Guideline rightGuideLine;

    public RightFileViewHolder(Context context ,@NonNull View itemView) {
        super(itemView);
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();
        Logger.error(TAG, "LeftImageVideoViewHolder: orientation: "+orientation);
        rightGuideLine = itemView.findViewById(R.id.rightGuideline);
        if(orientation == 1 || orientation == 3){
            Logger.error(TAG, "LeftImageVideoViewHolder: Landscape Mode");
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rightGuideLine.getLayoutParams();
            params.guidePercent = 0.5f;
            rightGuideLine.setLayoutParams(params);
        }
        messageStatus=itemView.findViewById(R.id.messageStatus);
        messageTimeStamp = itemView.findViewById(R.id.timeStamp);
        fileName=itemView.findViewById(R.id.fileName);
        fileType=itemView.findViewById(R.id.fileType);
        fileContainer = itemView.findViewById(R.id.fileContainer);


    }
}
