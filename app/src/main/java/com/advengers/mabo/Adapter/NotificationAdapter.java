package com.advengers.mabo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Model.Likelist;
import com.advengers.mabo.Model.Notification;
import com.advengers.mabo.R;
import com.advengers.mabo.databinding.ListLikeBinding;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    ArrayList<Notification> likelist = new ArrayList<>();
    NotificationAdapter.ProfileClick profilecallback;
    SimpleDateFormat formatter;
    PrettyTime p = new PrettyTime();
    public NotificationAdapter(Context context,ArrayList<Notification> data)
    {
        this.context = context;
        this.likelist = data;

    }
    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListLikeBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_like, parent, false);

        return new NotificationAdapter.ViewHolder(binding);

    }



    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, final int position) {
        // holder.bindData(dataModelList.get(position), mContext);
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = formatter.parse(likelist.get(position).getDetails().getCreated_at());
            holder.binding.txtDate.setText(p.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(likelist.get(position).getCategory().equals("comment")) {
            if(Integer.parseInt(likelist.get(position).getCount())==1)
                holder.binding.txtName.setText(likelist.get(position).getDetails().getUsername() +" commented on your post");
            else
                holder.binding.txtName.setText(likelist.get(position).getDetails().getUsername() + " and " +
                    (Integer.parseInt(likelist.get(position).getCount()) - 1) + " others commented on your post");
        }else if(likelist.get(position).getCategory().equals("replycomment")) {
            if(Integer.parseInt(likelist.get(position).getCount())==1)
                holder.binding.txtName.setText(likelist.get(position).getDetails().getUsername() + " replied to your comment");
            else
            holder.binding.txtName.setText(likelist.get(position).getDetails().getUsername() + " and " +
                    (Integer.parseInt(likelist.get(position).getCount()) - 1) + " others replied to your comment");
        }else if(likelist.get(position).getCategory().equals("commenttaggedpost")) {
            if(Integer.parseInt(likelist.get(position).getCount())==1)
                holder.binding.txtName.setText(likelist.get(position).getDetails().getUsername() +" tagged you on comment");
            else
                holder.binding.txtName.setText(likelist.get(position).getDetails().getUsername() + " and " +
                    (Integer.parseInt(likelist.get(position).getCount()) - 1) + " others tagged you on comment");
        }else if(likelist.get(position).getCategory().equals("like")) {
            if(Integer.parseInt(likelist.get(position).getCount())==1)
                holder.binding.txtName.setText(likelist.get(position).getDetails().getUsername()+" likes your post");
            else
                holder.binding.txtName.setText(likelist.get(position).getDetails().getUsername() + " and " +
                        (Integer.parseInt(likelist.get(position).getCount()) - 1) + " others likes your post");
        }else if(likelist.get(position).getCategory().equals("taggedpost")) {
            if(Integer.parseInt(likelist.get(position).getCount())==1)
                holder.binding.txtName.setText(likelist.get(position).getDetails().getUsername()+" tagged on post");
            else
                holder.binding.txtName.setText(likelist.get(position).getDetails().getUsername() + " and " +
                        (Integer.parseInt(likelist.get(position).getCount()) - 1) + " others tagged on post");
        }
      //  holder.binding.imgProfile.setVisibility(View.GONE);
        holder.binding.rlRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilecallback.onItem(position);
            }
        });
        if(!likelist.get(position).getDetails().getProfile_imagename().isEmpty())
            Picasso.get().load(likelist.get(position).getDetails().getProfile_imagename()).placeholder(R.drawable.ic_avatar).into(holder.binding.imgProfile);
        holder.binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profilecallback.onProfile(position);
            }
        });
    }



    @Override
    public int getItemCount() {
        return likelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListLikeBinding binding;

        public ViewHolder(ListLikeBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

    }

    public void CallBackListener(NotificationAdapter.ProfileClick mCallback) {
        this.profilecallback = mCallback;
    }

    public interface ProfileClick
    {
        public void onItem(int position);
        public void onProfile(int position);
    }
}

