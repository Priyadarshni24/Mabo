package com.advengers.mabo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Model.Commentlist;
import com.advengers.mabo.R;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.databinding.ListCommentBinding;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    ArrayList<Commentlist> likelist = new ArrayList<>();
    ProfileClick profilecallback;
    SimpleDateFormat formatter;
    PrettyTime p = new PrettyTime();
    public  CommentAdapter(Context context,ArrayList<Commentlist> data)
    {
        this.context = context;
        this.likelist = data;

    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListCommentBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_comment, parent, false);

        return new CommentAdapter.ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, final int position) {
        // holder.bindData(dataModelList.get(position), mContext);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(likelist.get(position).getCreated());
            holder.binding.txtDate.setText(p.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.binding.txtName.setText(likelist.get(position).getUsername());
        if(!likelist.get(position).getProfile_imagename().isEmpty())
        Picasso.get().load(likelist.get(position).getProfile_imagename()).placeholder(R.drawable.ic_avatar).into(holder.binding.imgProfile);
        holder.binding.txtComment.setText(StringEscapeUtils.unescapeJava(likelist.get(position).getComment()));
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
        public ListCommentBinding binding;

        public ViewHolder(ListCommentBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

    }

    public void CallBackListener(CommentAdapter.ProfileClick mCallback) {
        this.profilecallback = mCallback;
    }

    public interface ProfileClick
    {
        public void onProfile(int position);
    }
}

