package com.advengers.mabo.Cometchat.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Cometchat.CustomView.CircleImageView;
import com.advengers.mabo.R;

public class RightMessageViewHolder extends RecyclerView.ViewHolder{
    public TextView textMessage;
    public TextView messageTimeStamp;
    public CircleImageView messageStatus;

    public RightMessageViewHolder(View itemView) {
        super(itemView);
        textMessage = itemView.findViewById(R.id.textViewMessage);
        messageStatus = itemView.findViewById(R.id.img_message_status);
        messageTimeStamp = itemView.findViewById(R.id.timestamp);
    }

}
