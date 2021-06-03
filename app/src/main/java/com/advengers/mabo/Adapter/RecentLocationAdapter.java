package com.advengers.mabo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Activity.DashboardActivity;
import com.advengers.mabo.Model.RecentLocation;
import com.advengers.mabo.R;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.databinding.ListLikeBinding;
import com.advengers.mabo.databinding.ListRecentlocationBinding;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecentLocationAdapter extends RecyclerView.Adapter<RecentLocationAdapter.ViewHolder> {
private Context context;
        ArrayList<RecentLocation> likelist = new ArrayList<>();
        SimpleDateFormat formatter;
        String username;
        String profilepic;
        PrettyTime p = new PrettyTime();
public  RecentLocationAdapter(Context context,String username,String profilepic,ArrayList<RecentLocation> data)
        {
        this.context = context;
        this.likelist = data;
        this.username = username;
        this.profilepic = profilepic;
        }
@NonNull
@Override
public RecentLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListRecentlocationBinding binding = DataBindingUtil.inflate(
        LayoutInflater.from(parent.getContext()),
        R.layout.list_recentlocation, parent, false);

        return new RecentLocationAdapter.ViewHolder(binding);

        }

@Override
public void onBindViewHolder(@NonNull RecentLocationAdapter.ViewHolder holder, final int position) {
        // holder.bindData(dataModelList.get(position), mContext);
        formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
        Date date = formatter.parse(likelist.get(position).getCreated_at());
        holder.binding.txtDate.setText(p.format(date));
        } catch (ParseException e) {
        e.printStackTrace();
        }
        holder.binding.txtName.setText(username);
        if(!profilepic.isEmpty())
        Picasso.get().load(profilepic).placeholder(R.drawable.ic_avatar).into(holder.binding.imgProfile);
        holder.binding.txtPlace.setText(Tools.getAddress(context, Double.parseDouble(likelist.get(position).getLatitude()), Double.parseDouble(likelist.get(position).getLongitude())));
        }



@Override
public int getItemCount() {
        return likelist.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder {
    public ListRecentlocationBinding binding;

    public ViewHolder(ListRecentlocationBinding itemRowBinding) {
        super(itemRowBinding.getRoot());
        this.binding = itemRowBinding;
    }

}

}


