package com.advengers.mabo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Interfaces.Keys;
import com.advengers.mabo.Model.Interest;
import com.advengers.mabo.R;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.ListInterestBinding;
import com.advengers.mabo.databinding.ListPostBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.advengers.mabo.Interfaces.Keys.INTEREST;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {
    private Context context;
    ArrayList<Interest> interest = new ArrayList<Interest>();
    SelectedInterest selectinterestcallback;
    String selected;

    public  InterestAdapter(Context context,ArrayList<Interest> data,String selected)
    {
        this.context = context;
        this.interest = data;
        this.selected = selected;
    }
    @NonNull
    @Override
    public InterestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListInterestBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_interest, parent, false);

        return new InterestAdapter.ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull InterestAdapter.ViewHolder holder, final int position) {
        // holder.bindData(dataModelList.get(position), mContext);
        holder.binding.checkid.setText(interest.get(position).getInterest());
        holder.binding.checkid.setChecked(interest.get(position).isEnable());
        holder.binding.checkid.setEnabled(interest.get(position).isEditable());
        holder.binding.checkid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String interests = selected;//Utils.getInstance(context).getString(INTEREST);
                if(b)
                {
                if(interests.length()>0)
                {

                   interests = interests + "," + interest.get(position).getId();
                }else
                {
                    interests = interest.get(position).getId();
                }

           //     Utils.getInstance(context).setString(INTEREST,interests);
                }else
                {
                    String[] data = interests.split(",");
                  List<String> list = new ArrayList<String>(Arrays.asList(data)); //Arrays.asList(data);

                    if(list.contains(interest.get(position).getId()))
                    {
                        list.remove(interest.get(position).getId());
                    }
                    interests = "";
                    for (int i = 0;i<list.size();i++)
                    {
                        if(i==0)
                            interests = list.get(i);
                        else
                            interests = interests+","+list.get(i);
                    }
                   /* selected = interests;
                    selectinterestcallback.onSelect(interests);
                    LogUtils.e(interests);*/
              //  Utils.getInstance(context).setString(INTEREST,interests);
                }
                selected = interests;
                selectinterestcallback.onSelect(interests);
                LogUtils.e(interests);
            }
        });
    }

    @Override
    public int getItemCount() {
        return interest.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListInterestBinding binding;

        public ViewHolder(ListInterestBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

    }

    public void setInterestCallBackListener(SelectedInterest mCallback) {
        this.selectinterestcallback = mCallback;
    }

    public interface SelectedInterest
    {
        public void onSelect(String interest);
    }
}
