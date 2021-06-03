package com.advengers.mabo.Adapter;

import android.app.Activity;
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


import com.advengers.mabo.Model.Post;
import com.advengers.mabo.Model.Tag;
import com.advengers.mabo.R;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.ListPostBinding;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        PrettyTime p = new PrettyTime();
        formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        LogUtils.e(postdata.get(position).getCreated());
        try {
            Date date = formatter1.parse(postdata.get(position).getCreated());
            LogUtils.e(date.toString());
           holder.binding.txtDate.setText(p.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if((postdata.get(position).getImage_url().isEmpty()||postdata.get(position).getImage_url().equals("[]"))&&(postdata.get(position).getVideo_url().isEmpty()||postdata.get(position).getVideo_url().equals("[]")))
        {
            holder.binding.slideViewpager.setVisibility(View.GONE);
            holder.binding.pageIndicatorView.setVisibility(View.GONE);
        }else{
            LogUtils.e(position+"Coming in video slider");
            holder.binding.slideViewpager.setVisibility(View.VISIBLE);
            holder.binding.pageIndicatorView.setVisibility(View.VISIBLE);
            ArrayList<String> listimage = new ArrayList<>();
            String[] arrOfStr = postdata.get(position).getImage_url().split(",", 3);

            for (String a : arrOfStr)
            {
               if(!a.isEmpty())
               listimage.add(a);
            }
            ArrayList<String> Videoimage = new ArrayList<>();
            String[] arrOfStrs = postdata.get(position).getVideo_url().split(",", 3);
          //  LogUtils.e("Video length "+arrOfStr.length);
            for (String a : arrOfStrs)
            {
               if(!a.isEmpty())
                Videoimage.add(a);
               LogUtils.e(a);
            }
            sliderAdapter = new PosrSliderAdapter(activity,context,listimage,Videoimage);
            holder.binding.slideViewpager.setAdapter(sliderAdapter);
          //  PageIndicatorView pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
            holder.binding.pageIndicatorView.setViewPager(holder.binding.slideViewpager);
            holder.binding.pageIndicatorView.setSelection(0);
            holder.binding.pageIndicatorView.setSelectedColor(R.color.apptheme);
            holder.binding.pageIndicatorView.setUnselectedColor(R.color.grey);
            holder.binding.pageIndicatorView.setDynamicCount(true);
        }try {
            if (postdata.get(position).getUserdetails().getUsername() != null)
                holder.binding.txtName.setText(postdata.get(position).getUserdetails().getUsername());
            holder.binding.txtAddress.setText(Tools.getAddress(context, Double.parseDouble(postdata.get(position).getLatitude()), Double.parseDouble(postdata.get(position).getLongitude())));
            if (!postdata.get(position).getUserdetails().getProfile_imagename().isEmpty())
                Picasso.get().load(postdata.get(position).getUserdetails().getProfile_imagename()).placeholder(R.drawable.ic_avatar).into(holder.binding.imgProfile);

            if (!postdata.get(position).getId().equals("55"))
            {
                holder.binding.txtPostname.setText(StringEscapeUtils.unescapeJava(postdata.get(position).getPost_title()));
                holder.binding.txtPostname.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if(postdata.get(position).getDistance()!=null)
            holder.binding.txtPostdistance.setText(Utils.df2.format(Double.parseDouble(postdata.get(position).getDistance()) * 1.6) + " " + context.getString(R.string.str_kms));
            String content = "";
            if (!postdata.get(position).getTag_location().isEmpty()) {
               // content = "@" + postdata.get(position).getTag_location();
             //   holder.binding.txtPostdesc.setText(content);
                holder.binding.rlTag.setVisibility(View.VISIBLE);
                holder.binding.imgTag.setVisibility(View.VISIBLE);
                holder.binding.txtTag.setText(postdata.get(position).getTag_location());
            }else
                holder.binding.rlTag.setVisibility(View.GONE);
            if (!postdata.get(position).getTag_people().isEmpty()) {

                String tagpeople = "";

                List<Tag> tagpeoples = postdata.get(position).getTagpeopledata();
                for (int i = 0; i < tagpeoples.size(); i++) {
                    if (i == 0)
                        tagpeople = tagpeoples.get(0).getUsername();
                    else
                        tagpeople = tagpeople + " " + tagpeoples.get(i).getUsername();
                }
                content = content + " with " + tagpeople;
                tagpeople = "";
                LogUtils.e(content);
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(content);
                ClickableSpan[] clickableSpans = new ClickableSpan[tagpeoples.size()];
                for (int i = 0; i < tagpeoples.size(); i++) {
                    int finalI = i;
                    clickableSpans[i] = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            LogUtils.e("" + tagpeoples.get(finalI).getUsername());
                            mLikeCommentcallback.onProfile(tagpeoples.get(finalI).getId());
                        }
                    };
                }
                for (int i = 0; i < tagpeoples.size(); i++)
                    ssBuilder.setSpan(
                            clickableSpans[i],
                            content.indexOf(tagpeoples.get(i).getUsername()),
                            content.indexOf(tagpeoples.get(i).getUsername()) + String.valueOf(tagpeoples.get(i).getUsername()).length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                // Display the spannable text to TextView
                holder.binding.txtPostdesc.setText(ssBuilder);

                // Specify the TextView movement method
                holder.binding.txtPostdesc.setMovementMethod(LinkMovementMethod.getInstance());

            }

           holder.binding.likecount.setText(postdata.get(position).getLikecount()+" "+context.getString(R.string.str_likes));
            if (postdata.get(position).getLikedbyme() == null) {
                postdata.get(position).setLikedbyme("0");
            }
            if (postdata.get(position).getLikedbyme().equals("0"))
                holder.binding.imdLike.setImageResource(R.drawable.ic_unlike);
            else
                holder.binding.imdLike.setImageResource(R.drawable.ic_like);
            holder.binding.rlFeatures.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int Count = Integer.parseInt(postdata.get(position).getLikecount());
                    if (postdata.get(position).getLikedbyme().equals("0")) {
                        postdata.get(position).setLikedbyme("1");
                        holder.binding.imdLike.setImageResource(R.drawable.ic_like);
                        Count = Count + 1;
                        postdata.get(position).setLikecount(String.valueOf(Count));
                    } else {
                        postdata.get(position).setLikedbyme("0");
                        holder.binding.imdLike.setImageResource(R.drawable.ic_unlike);
                        Count = Count - 1;
                        postdata.get(position).setLikecount(String.valueOf(Count));
                    }
                    mLikeCommentcallback.onLike(position, postdata.get(position).getId(), postdata.get(position).getLikedbyme());
                   holder.binding.likecount.setText(postdata.get(position).getLikecount() + " "+context.getString(R.string.str_likes));

                }
            });
            if (userid.equals(postdata.get(position).getUser_id()))
                holder.binding.imdForward.setVisibility(View.GONE);
            holder.binding.commentcount.setText(postdata.get(position).getCommentcount() + " " + context.getString(R.string.title_comments));
            holder.binding.imdComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLikeCommentcallback.onComment(position, postdata.get(position).getId());
                }
            });
            holder.binding.commentcount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLikeCommentcallback.onComment(position, postdata.get(position).getId());
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
                    mLikeCommentcallback.onProfile(postdata.get(position).getUser_id());
                }
            });
            holder.binding.likecount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLikeCommentcallback.onLikeList(postdata.get(position).getId());
                }
            });
            holder.binding.postlayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    mLikeCommentcallback.onDeletePost(position);
                    return false;
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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
        public void onProfile(String userid);
        public void onLikeList(String postid);
        public void onDeletePost(int position);
    }
}
