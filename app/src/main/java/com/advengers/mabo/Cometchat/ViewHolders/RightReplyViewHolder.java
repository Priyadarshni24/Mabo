package com.advengers.mabo.Cometchat.ViewHolders;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Cometchat.CustomView.CircleImageView;
import com.advengers.mabo.R;

public class RightReplyViewHolder extends RecyclerView.ViewHolder {

    public TextView tvTimeStamp;

    public RelativeLayout rlMain;

    public LinearLayout llReplyContainer;

    public TextView tvNameReply;

    public TextView tvReplyTextMessage;

    public ImageView ivReplyImage;

    public RelativeLayout rlMessageContainer;

    public TextView tvNewMessage;

    public ImageView ivNewImage;

    public RelativeLayout rlAudioConatiner;

    public ImageView ivPlayButton;

    public SeekBar sbAudioSeekBar;

    public TextView tvAudioLength;

    public ImageView ivPlayVideoButton;

    public RelativeLayout rlFileContainer;

    public TextView tvFileType;

    public TextView tvFileName;

    public CircleImageView ivMessageStatus;

    public RelativeLayout rlImageContainer;




    public RightReplyViewHolder(@NonNull View itemView) {
        super(itemView);

        ivMessageStatus=itemView.findViewById(R.id.messageStatus);
        tvAudioLength=itemView.findViewById(R.id.audioLength);
        tvFileName=itemView.findViewById(R.id.fileName);
        tvFileType=itemView.findViewById(R.id.fileType);
        rlAudioConatiner=itemView.findViewById(R.id.audioContainer);
        rlFileContainer=itemView.findViewById(R.id.fileContainer);
        sbAudioSeekBar =itemView.findViewById(R.id.audioSeekBar);
        ivPlayButton=itemView.findViewById(R.id.playButton);
        ivNewImage=itemView.findViewById(R.id.ivNewMessage);
        tvNewMessage=itemView.findViewById(R.id.txtNewmsg);
        ivPlayVideoButton=itemView.findViewById(R.id.ivVideoPlay);
        tvTimeStamp=itemView.findViewById(R.id.timeStamp);
        rlMain=itemView.findViewById(R.id.rl_main);
        llReplyContainer=itemView.findViewById(R.id.rlReplyContainer);
        tvNameReply=itemView.findViewById(R.id.tvNameReply);
        rlMessageContainer=itemView.findViewById(R.id.rlMessageContainer);
        ivReplyImage=itemView.findViewById(R.id.ivReplyImage);
        tvReplyTextMessage=itemView.findViewById(R.id.tvReplyTextMessage);
        rlImageContainer=itemView.findViewById(R.id.imageContainer);

    }
}
