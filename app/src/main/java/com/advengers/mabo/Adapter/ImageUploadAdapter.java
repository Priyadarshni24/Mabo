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

import com.advengers.mabo.Model.Likelist;
import com.advengers.mabo.Multipleimageselect.models.Image;
import com.advengers.mabo.R;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.databinding.ListLikeBinding;
import com.advengers.mabo.databinding.ListPostimagesBinding;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageUploadAdapter extends RecyclerView.Adapter<ImageUploadAdapter.ViewHolder> {
    private Context context;
    ArrayList<Image> likelist = new ArrayList<>();
    CloseClick callback;
    SimpleDateFormat formatter;
    PrettyTime p = new PrettyTime();
    public ImageUploadAdapter(Context context,ArrayList<Image> data)
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // holder.bindData(dataModelList.get(position), mContext);
        /*formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = formatter.parse(likelist.get(position).getCreated());
            holder.binding.txtDate.setText(p.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        holder.binding.txtImagename.setText(likelist.get(position).name);
        holder.binding.txtSize.setText(getFolderSizeLabel(new File(likelist.get(position).path)));
        Bitmap myBitmap = BitmapFactory.decodeFile(likelist.get(position).path);
        holder.binding.roundedImageView.setImageBitmap(myBitmap);
        holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callback.onClose(position);
            }
        });
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

    public void CallBackListener(ImageUploadAdapter.CloseClick mCallback) {
        this.callback = mCallback;
    }

    public interface CloseClick
    {
        public void onClose(int position);
    }
}
