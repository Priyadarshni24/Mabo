package com.advengers.mabo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Model.Likelist;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.databinding.ListFriendsBinding;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FriendsAdapter extends  RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private Context context;
    ArrayList<User> friendslist = new ArrayList<>();
    FriendsAdapter.ProfileClick profilecallback;
    SimpleDateFormat formatter;
    PrettyTime p = new PrettyTime();
    public  FriendsAdapter(Context context,ArrayList<User> data)
    {
        this.context = context;
        this.friendslist = data;

    }
    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListFriendsBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_friends, parent, false);

        return new FriendsAdapter.ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, final int position) {
        // holder.bindData(dataModelList.get(position), mContext);
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        holder.binding.txtName.setText(friendslist.get(position).getUsername());
        if(!friendslist.get(position).getprofile_imagename().isEmpty())
            Picasso.get().load(friendslist.get(position).getprofile_imagename()).placeholder(R.drawable.ic_avatar).into(holder.binding.imgProfile);
        holder.binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               profilecallback.onProfile(position);
            }
        });
        holder.binding.makechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilecallback.onitemClick(position);
            }
        });
        holder.binding.videocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilecallback.onCallClick(position);
            }
        });
        holder.binding.lytRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilecallback.onitemClick(position);
            }
        });
    }



    @Override
    public int getItemCount() {
        return friendslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListFriendsBinding binding;

        public ViewHolder(ListFriendsBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

    }

    public void CallBackListener(FriendsAdapter.ProfileClick mCallback) {
        this.profilecallback = mCallback;

    }

    public interface ProfileClick
    {
        public void onProfile(int position);
        public void onitemClick(int position);
        public void onCallClick(int position);
    }
}
