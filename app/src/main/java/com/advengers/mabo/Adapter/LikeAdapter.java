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
import com.advengers.mabo.databinding.ListLikeBinding;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {
    private Context context;
    ArrayList<Likelist> likelist = new ArrayList<>();
    ProfileClick profilecallback;
    SimpleDateFormat formatter;
    PrettyTime p = new PrettyTime();
    public  LikeAdapter(Context context,ArrayList<Likelist> data)
    {
        this.context = context;
        this.likelist = data;

    }
    @NonNull
    @Override
    public LikeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListLikeBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_like, parent, false);

        return new LikeAdapter.ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull LikeAdapter.ViewHolder holder, final int position) {
        // holder.bindData(dataModelList.get(position), mContext);
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = formatter.parse(likelist.get(position).getCreated());
            holder.binding.txtDate.setText(p.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.binding.txtName.setText(likelist.get(position).getUsername());
        if(!likelist.get(position).getProfile_imagename().isEmpty())
        Picasso.get().load(likelist.get(position).getProfile_imagename()).placeholder(R.drawable.ic_avatar).into(holder.binding.imgProfile);
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

    public void CallBackListener(LikeAdapter.ProfileClick mCallback) {
        this.profilecallback = mCallback;
    }

    public interface ProfileClick
    {
        public void onProfile(int position);
    }
}

