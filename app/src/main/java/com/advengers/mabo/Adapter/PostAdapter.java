package com.advengers.mabo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.advengers.mabo.Model.Post;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.ListPostBinding;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.advengers.mabo.Activity.MainActivity.CLOUDBASEURL;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private PosrSliderAdapter sliderAdapter;
    SimpleDateFormat formatter1;
    ArrayList<Post> postdata = new ArrayList<>();
    LikeCommentCallback mLikeCommentcallback;
    Activity activity;
    String userid;
    public  PostAdapter(Activity activity,Context context,ArrayList<Post> data,String id)
    {
        this.activity = activity;
        this.context = context;
        this.postdata = data;
        this.userid = id;
      //  sliderAdapter = new PosrSliderAdapter(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListPostBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_post, parent, false);

        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
    // holder.bindData(dataModelList.get(position), mContext);
        PrettyTime p = new PrettyTime();
       // formatter = new SimpleDateFormat("dd MMM YY");
        formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        LogUtils.e(postdata.get(position).getCreated());
        try {
            Date date = formatter1.parse(postdata.get(position).getCreated());
            LogUtils.e(date.toString());
       //     String strDate = formatter.format(date);
           holder.binding.txtDate.setText(p.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(postdata.get(position).getImage_url().isEmpty()||postdata.get(position).getImage_url().equals("[]"))
        {
            holder.binding.slideViewpager.setVisibility(View.GONE);
        }else{

            holder.binding.slideViewpager.setVisibility(View.VISIBLE);
            ArrayList<String> listimage = new ArrayList<>();
            String[] arrOfStr = postdata.get(position).getImage_url().split(",", 3);

            for (String a : arrOfStr)
            {
               if(!a.isEmpty())
               listimage.add(a);
            }
            ArrayList<String> Videoimage = new ArrayList<>();
            String[] arrOfStrs = postdata.get(position).getVideo_url().split(",", 3);

            for (String a : arrOfStrs)
            {
               if(!a.isEmpty())
                Videoimage.add(a);
            }
            sliderAdapter = new PosrSliderAdapter(activity,context,listimage,Videoimage);
            holder.binding.slideViewpager.setAdapter(sliderAdapter);
        }
        holder.binding.txtName.setText(postdata.get(position).getUserdetails().getUsername());
        holder.binding.txtAddress.setText(Tools.getAddress(context,Double.parseDouble(postdata.get(position).getLatitude()),Double.parseDouble(postdata.get(position).getLongitude())));
       if(!postdata.get(position).getUserdetails().getProfile_imagename().isEmpty())
        Picasso.get().load(postdata.get(position).getUserdetails().getProfile_imagename()).placeholder(R.drawable.ic_avatar).into(holder.binding.imgProfile);
       if(!postdata.get(position).getId().equals("55"))
        holder.binding.txtPostname.setText(StringEscapeUtils.unescapeJava(postdata.get(position).getPost_title()));
        holder.binding.txtPostdistance.setText(Utils.df2.format(Double.parseDouble(postdata.get(position).getDistance())*1.6)+" "+context.getString(R.string.str_kms));
        if(!postdata.get(position).getTag_people().isEmpty())
        {
            holder.binding.txtPostdesc.setText(context.getString(R.string.str_with)+" "+postdata.get(position).getTag_people());
        }
        if(!postdata.get(position).getTag_location().isEmpty())
        {
            holder.binding.txtPostdesc.setText(holder.binding.txtPostdesc.getText()+" @"+postdata.get(position).getTag_location());
        }
        holder.binding.likecount.setText(postdata.get(position).getLikecount()+" "+context.getString(R.string.str_likes));
        if(postdata.get(position).getLikedbyme()==null)
        {
            postdata.get(position).setLikedbyme("0");
        }
        if(postdata.get(position).getLikedbyme().equals("0"))
            holder.binding.imdLike.setImageResource(R.drawable.ic_unlike);
        else
            holder.binding.imdLike.setImageResource(R.drawable.fav);
        holder.binding.imdLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Count = Integer.parseInt(postdata.get(position).getLikecount());
                if(postdata.get(position).getLikedbyme().equals("0"))
                {
                    postdata.get(position).setLikedbyme("1");
                    holder.binding.imdLike.setImageResource(R.drawable.fav);
                    Count = Count + 1;
                    postdata.get(position).setLikecount(String.valueOf(Count));
                }else
                {
                    postdata.get(position).setLikedbyme("0");
                    holder.binding.imdLike.setImageResource(R.drawable.ic_unlike);
                    Count = Count - 1;
                    postdata.get(position).setLikecount(String.valueOf(Count));
                }
                mLikeCommentcallback.onLike(position,postdata.get(position).getId(),postdata.get(position).getLikedbyme());
                holder.binding.likecount.setText(postdata.get(position).getLikecount()+" "+context.getString(R.string.str_likes));

            }
        });
        if(userid.equals(postdata.get(position).getUser_id()))
            holder.binding.imdForward.setVisibility(View.GONE);
        holder.binding.commentcount.setText(postdata.get(position).getCommentcount()+" "+context.getString(R.string.title_comments));
        holder.binding.imdComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLikeCommentcallback.onComment(position,postdata.get(position).getId());
            }
        });
        holder.binding.commentcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLikeCommentcallback.onComment(position,postdata.get(position).getId());
            }
        });
        holder.binding.imdForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mLikeCommentcallback.onDirection(position);
            }
        });
        holder.binding.imdShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLikeCommentcallback.onShare(position);
            }
        });
        holder.binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog, User.getUser(),context,context);
                mLikeCommentcallback.onProfile(position);
            }
        });
        holder.binding.likecount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLikeCommentcallback.onLikeList(postdata.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return postdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListPostBinding binding;

        public ViewHolder(ListPostBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

    }
    public void setLikeCommentCallBackListener(PostAdapter.LikeCommentCallback mCallback) {
        this.mLikeCommentcallback = mCallback;
    }

    public interface LikeCommentCallback
    {
        public void onLike(int position,String postid,String data);
        public void onComment(int position,String postid);
        public void onDirection(int position);
        public void onShare(int position);
        public void onProfile(int position);
        public void onLikeList(String postid);
    }
}
