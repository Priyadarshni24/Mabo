package com.advengers.mabo.Adapter;

import android.content.Context;
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
import com.advengers.mabo.databinding.ListPostBinding;
import com.advengers.mabo.databinding.ListTagpeopleBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TagpeopleAdapter  extends RecyclerView.Adapter<TagpeopleAdapter.ViewHolder> {
    private Context context;
    private PosrSliderAdapter sliderAdapter;
    SimpleDateFormat formatter,formatter1;
    ArrayList<User> people = new ArrayList<>();
    TagPeople tagPeoplecallback;
    public  TagpeopleAdapter(Context context,ArrayList<User> data)
    {
        this.context = context;
        this.people = data;

    }
    @NonNull
    @Override
    public TagpeopleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListTagpeopleBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_tagpeople, parent, false);

        return new TagpeopleAdapter.ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull TagpeopleAdapter.ViewHolder holder, final int position) {
        // holder.bindData(dataModelList.get(position), mContext);
        holder.binding.txtpeoplename.setText(people.get(position).getUsername());
        holder.binding.btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tagPeoplecallback.onTag(people.get(position),position);
            }
        });
    }



    @Override
    public int getItemCount() {
        return people.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListTagpeopleBinding binding;

        public ViewHolder(ListTagpeopleBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

    }

    public void tagPeopleCallBackListener(TagpeopleAdapter.TagPeople mCallback) {
        this.tagPeoplecallback = mCallback;
    }

    public interface TagPeople
    {
        public void onTag(User userdetail,int position);
    }
}
