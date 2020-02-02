package com.advengers.mabo.Cometchat.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Utils.LogUtils;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.User;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.advengers.mabo.Cometchat.Adapter.RecentsListAdapter;
import com.advengers.mabo.Cometchat.Contracts.RecentsContract;
import com.advengers.mabo.Cometchat.Helper.ScrollHelper;
import com.advengers.mabo.Cometchat.Presenters.RecentsListPresenter;
import com.advengers.mabo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class RecentsFragment extends Fragment implements RecentsContract.RecentsView{

    private RecentsListAdapter recentsListAdapter;

    private RecyclerView recentsRecyclerView;

    private ImageView ivNoRecents;

    private TextView tvNoRecents;

    private RecentsContract.RecentsPresenter recentPresenter;

    private ScrollHelper scrollHelper;

    private User user;

    private ShimmerFrameLayout recentShimmer;
    private Button btn_resume;
    private LinearLayoutManager linearLayoutManager;
    private List<Conversation> conversationList=new ArrayList<>();

    public RecentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_recents, container, false);
        btn_resume = (Button)view.findViewById(R.id.btn_resume);
        btn_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.e("I am coming");
                //   getActivity().startActivity(CometChat.getActiveCall());
            }
        });
        ivNoRecents = view.findViewById(R.id.ivNoRecents);
        tvNoRecents=view.findViewById(R.id.tvNoRecents);
        recentsRecyclerView = view.findViewById(R.id.recents_recycler_view);
        recentShimmer=view.findViewById(R.id.recent_shimmer);

        linearLayoutManager=new LinearLayoutManager(getContext());
        recentsRecyclerView.setLayoutManager(linearLayoutManager);
        recentsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        recentPresenter = new RecentsListPresenter();

        recentPresenter.attach(this);

        new Thread(() -> recentPresenter.fetchConversations(getContext())).start();

        recentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)) {
                    recentPresenter.fetchConversations(getContext());
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
              /*  if (dy < 0) {
                    scrollHelper.setFab(true);
                } else
                    scrollHelper.setFab(false);*/
            }
        });

        return view;
    }


    @Override
    public void setFilterList(List<Conversation> hashMap) {
        recentsListAdapter.setFilterList(hashMap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recentPresenter.removeMessageListener(getString(R.string.presenceListener));
    }

    @Override
    public void onStart() {
        super.onStart();
        recentPresenter.addMessageListener(getString(R.string.presenceListener));
    }

    @Override
    public void onResume() {
        super.onResume();
        recentPresenter.addMessageListener(getString(R.string.presenceListener));
        recentPresenter.fetchConversations(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
     //   scrollHelper = (ScrollHelper) context;

    }

    @Override
    public void refreshConversation(BaseMessage message) {
        Conversation newConversation= CometChatHelper.getConversationFromMessage(message);
         recentsListAdapter.updateConversation(newConversation);
    }



    @Override
    public void setRecentAdapter(List<Conversation> conversationList) {
        this.conversationList.addAll(conversationList);
        Log.e( "setRecentAdapter: ",conversationList.toString() );
        if (recentsListAdapter == null) {
            recentsListAdapter = new RecentsListAdapter(conversationList, getActivity(), R.layout.recent_list_item,false);
            recentsRecyclerView.setAdapter(recentsListAdapter);
            recentShimmer.stopShimmer();
            recentShimmer.setVisibility(View.GONE);

        } else {
            if (this.conversationList != null) {
                recentsListAdapter.refreshData(conversationList);
            }
        }

        if (this.conversationList != null && this.conversationList.size() == 0) {
            tvNoRecents.setVisibility(View.VISIBLE);
            ivNoRecents.setVisibility(View.VISIBLE);
        } else {
            tvNoRecents.setVisibility(View.GONE);
            ivNoRecents.setVisibility(View.GONE);
        }

    }

    @Override
    public void updateUnreadCount(Conversation conversation) {

    }

    @Override
    public void setLastMessage(Conversation conversation) {

    }


    public void search(String s) {
//        recentPresenter.searchConversation(s);
    }
}
