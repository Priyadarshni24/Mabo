package com.advengers.mabo.Adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Model.Commentlist;
import com.advengers.mabo.Model.Tag;
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
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    ArrayList<Commentlist> likelist = new ArrayList<>();
    ProfileClick profilecallback;
    SimpleDateFormat formatter;
    String userid;
    PrettyTime p = new PrettyTime();
    public  CommentAdapter(Context context,ArrayList<Commentlist> data,String userid)
    {
        this.context = context;
        this.likelist = data;
        this.userid = userid;
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
            if(userid.equals(likelist.get(position).getUser_id()))
                holder.binding.txtName.setText("You commented on "+p.format(date));
            else
                holder.binding.txtName.setText(likelist.get(position).getUsername()+" commented "+p.format(date));
         //   holder.binding.txtDate.setText(p.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!likelist.get(position).getProfile_imagename().isEmpty())
            Picasso.get().load(likelist.get(position).getProfile_imagename()).placeholder(R.drawable.ic_avatar).into(holder.binding.imgProfile);


        String content = likelist.get(position).getComment();
        holder.binding.txtComment.setText(content);
        LogUtils.e("Position "+position);
        if(likelist.get(position).getTagpeopledata()!=null)
        {
            LogUtils.e("Coming after comment"+ likelist.get(position).getTagpeopledata().size());
        List<Tag> tagpeoples = likelist.get(position).getTagpeopledata();
       /* for(int t=0;t<tagpeoples.size();t++)
        LogUtils.e(tagpeoples.get(t).getUsername());*/
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(content);
            LogUtils.e(content+ " "+tagpeoples.size());

        ClickableSpan[] clickableSpans = new ClickableSpan[tagpeoples.size()];
        for (int i = 0; i < tagpeoples.size(); i++) {
            int finalI = i;
            clickableSpans[i] = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    profilecallback.onProfile(tagpeoples.get(finalI).getId());
                }
            };
        }

        for (int i = 0; i < tagpeoples.size(); i++) {
            LogUtils.e("tags "+content.indexOf(tagpeoples.get(i).getUsername())+" "+(content.indexOf(tagpeoples.get(i).getUsername())+tagpeoples.get(i).getUsername().length()));
            try{
            ssBuilder.setSpan(
                    clickableSpans[i],
                    content.indexOf(tagpeoples.get(i).getUsername()),
                    (content.indexOf(tagpeoples.get(i).getUsername())+tagpeoples.get(i).getUsername().length()),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );}catch (Exception r)
            {
                r.printStackTrace();
            }
        }

        // Display the spannable text to TextView
        holder.binding.txtComment.setText(ssBuilder);

        // Specify the TextView movement method
        holder.binding.txtComment.setMovementMethod(LinkMovementMethod.getInstance());
    }
        content = "";
        holder.binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profilecallback.onProfile(likelist.get(position).getId());
            }
        });
        holder.binding.txtReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilecallback.onReply(position);
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
        public void onProfile(String id);
        public void onReply(int position);
    }
}

