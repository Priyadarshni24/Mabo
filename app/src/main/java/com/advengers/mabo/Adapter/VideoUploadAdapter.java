package com.advengers.mabo.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Activity.CreatePostActivity;
import com.advengers.mabo.Multipleimageselect.models.Image;
import com.advengers.mabo.R;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.databinding.ListPostimagesBinding;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class VideoUploadAdapter extends RecyclerView.Adapter<VideoUploadAdapter.ViewHolder> {
    private Context context;
    ArrayList<String> likelist = new ArrayList<>();
    CloseClick callback;
    SimpleDateFormat formatter;
    PrettyTime p = new PrettyTime();
    public VideoUploadAdapter(Context context,ArrayList<String> data)
    {
        this.context = context;
        this.likelist = data;
        LogUtils.e(likelist.size()+"size");

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListPostimagesBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_postimages, parent, false);
        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.txtImagename.setText(likelist.get(position));
        holder.binding.txtSize.setText(getFolderSizeLabel(new File(likelist.get(position))));
        try {
            holder.binding.roundedImageView.setImageBitmap(CreatePostActivity.retriveVideoFrameFromVideo(likelist.get(position)));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callback.onCloseVideo(position);
            }
        });
    }

    public static String getFolderSizeLabel(File file) {
        long size = getFolderSize(file) / 1024; // Get size and convert bytes into Kb.
        if (size >= 1024) {
            return (size / 1024) + " Mb";
        } else {
            return size + " Kb";
        }
    }
    public static long getFolderSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                size += getFolderSize(child);
            }
        } else {
            size = file.length();
        }
        return size;
    }

    @Override
    public int getItemCount() {
        return likelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListPostimagesBinding binding;

        public ViewHolder(ListPostimagesBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

    }

    public void CallBackListener(VideoUploadAdapter.CloseClick mCallback) {
        this.callback = mCallback;
    }

    public interface CloseClick
    {
        public void onCloseVideo(int position);
    }
}

