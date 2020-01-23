package com.advengers.mabo.Cometchat.ViewHolders;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Cometchat.CustomView.CircleImageView;
import com.advengers.mabo.R;

public class LeftMessageViewHolder extends RecyclerView.ViewHolder {
    public TextView textMessage;
    public TextView messageTimeStamp;
    public TextView senderName;
    public CircleImageView avatar;

    public LeftMessageViewHolder(View leftTextMessageView) {
        super(leftTextMessageView);
        textMessage = leftTextMessageView.findViewById(R.id.textViewMessage);
        messageTimeStamp = leftTextMessageView.findViewById(R.id.timeStamp);
        avatar = leftTextMessageView.findViewById(R.id.imgAvatar);
        senderName = leftTextMessageView.findViewById(R.id.senderName);
    }
}
