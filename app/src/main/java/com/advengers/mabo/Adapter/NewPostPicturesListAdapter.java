package com.advengers.mabo.Adapter;

import android.content.Context;
import android.view.ViewGroup;


import com.advengers.mabo.Commons.RecyclerViewAdapterBase;
import com.advengers.mabo.Commons.ViewWrapper;
import com.advengers.mabo.Tools.NewPostPicture;
import com.advengers.mabo.Tools.NewPostPictureView;

import java.util.ArrayList;

/**
 * Created by priyadarshniau on 10/6/2017.
 */
public class NewPostPicturesListAdapter extends RecyclerViewAdapterBase<NewPostPicture, NewPostPictureView> {
    Context context;

    public NewPostPicturesListAdapter(Context context)
    {
        this.context =context;
    }

    public void update() {
        notifyDataSetChanged();
    }
    public void setItems(ArrayList<NewPostPicture> array) {
        items = array;
    }

    @Override
    public void onBindViewHolder(ViewWrapper<NewPostPictureView> holder, int position) {
        NewPostPictureView view = holder.getView();
        NewPostPicture newPostPicture = items.get(position);
        view.bind(newPostPicture);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    protected NewPostPictureView onCreateItemView(ViewGroup parent, int viewType) {
        NewPostPictureView v = new NewPostPictureView(parent.getContext());
        return v;
    }
}
