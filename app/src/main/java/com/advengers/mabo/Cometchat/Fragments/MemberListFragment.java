package com.advengers.mabo.Cometchat.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;
import com.advengers.mabo.Cometchat.Activity.OneToOneChatActivity;
import com.advengers.mabo.Cometchat.Adapter.GroupMemberListAdapter;
import com.advengers.mabo.Cometchat.Contracts.MemberListFragmentContract;
import com.advengers.mabo.Cometchat.Contracts.StringContract;
import com.advengers.mabo.Cometchat.Helper.RecyclerTouchListener;
import com.advengers.mabo.Cometchat.Presenters.MemberListFragmentPresenter;
import com.advengers.mabo.R;
import com.advengers.mabo.Cometchat.Utils.CommonUtils;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberListFragment extends Fragment implements MemberListFragmentContract.MemberListFragmentView {

    private static final int LIMIT = 30;

    private RecyclerView rvMembers;

    private LinearLayoutManager linearLayoutManager;

    private User user;

    private MemberListFragmentContract.MemberListFragmentPresenter memberListFragmentPresenter;

    private String groupId;

    private String ownerUid;

    private GroupMemberListAdapter groupMemberListAdapter;

    private String scope;

    public MemberListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);

        memberListFragmentPresenter = new MemberListFragmentPresenter();
        memberListFragmentPresenter.attach(this);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvMembers = view.findViewById(R.id.rv_member);
        rvMembers.setLayoutManager(linearLayoutManager);



        if (getArguments().containsKey(StringContract.IntentStrings.INTENT_GROUP_ID)) {
            groupId = getArguments().getString(StringContract.IntentStrings.INTENT_GROUP_ID);
            memberListFragmentPresenter.initMemberList(groupId, LIMIT, getContext());
        }
        if (getArguments().containsKey(StringContract.IntentStrings.USER_ID))
        {
            ownerUid=getArguments().getString(StringContract.IntentStrings.USER_ID);
        }

        if (getArguments().containsKey(StringContract.IntentStrings.INTENT_SCOPE)) {
            scope = getArguments().getString(StringContract.IntentStrings.INTENT_SCOPE);
        }

        rvMembers.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvMembers, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View var1, int var2) {

                if (scope.equals(CometChatConstants.SCOPE_MODERATOR) || scope.equals(CometChatConstants.SCOPE_ADMIN)) {

                    registerForContextMenu(rvMembers);
                    user = (User) var1.getTag(R.string.user);
                    if (!ownerUid.equals(user.getUid())) {
                        getActivity().openContextMenu(var1);
                    }
                }

            }

            @Override
            public void onLongClick(View var1, int var2) {


            }
        }));


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        memberListFragmentPresenter.detach();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_group_action, menu);

        MenuItem menuItem=menu.findItem(R.id.menu_item_Reinstate);

        menuItem.setVisible(false);
        menu.setHeaderTitle(CommonUtils.setTitle("Select Action",getContext()));

    }

    @Override
    public void onResume() {
        super.onResume();
        if(groupMemberListAdapter!=null) {
            groupMemberListAdapter.resetAdapter();

        }

        memberListFragmentPresenter.refresh(groupId,LIMIT,getContext());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_outcast:
                showToast("banned");
                memberListFragmentPresenter.outCastUser(user.getUid(), groupId,groupMemberListAdapter);
                break;
            case R.id.menu_item_kick:
                memberListFragmentPresenter.kickUser(user.getUid(), groupId,groupMemberListAdapter);
                showToast("kick");
                break;
            case R.id.menu_view_profile:
                Intent intent = new Intent(getContext(), OneToOneChatActivity.class);
                intent.putExtra(StringContract.IntentStrings.USER_ID, user.getUid());
                intent.putExtra(StringContract.IntentStrings.USER_NAME, user.getName());
                startActivity(intent);
                break;

            case R.id.subMenu_admin:
                memberListFragmentPresenter.updateScope(user.getUid(),groupId,groupMemberListAdapter,CometChatConstants.SCOPE_ADMIN);
                break;

            case R.id.subMenu_moderator:
                memberListFragmentPresenter.updateScope(user.getUid(),groupId,groupMemberListAdapter,CometChatConstants.SCOPE_MODERATOR);
                break;

            case R.id.subMenu_participant:
                memberListFragmentPresenter.updateScope(user.getUid(),groupId,groupMemberListAdapter,CometChatConstants.SCOPE_PARTICIPANT);
                break;

        }
        return super.onContextItemSelected(item);
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void setAdapter(HashMap<String ,GroupMember> groupMemberList) {

        if (groupMemberListAdapter == null) {
            groupMemberListAdapter = new GroupMemberListAdapter(groupMemberList, getContext(),ownerUid);
            memberListFragmentPresenter.addGroupEventListener("MemberListFragment",groupId,groupMemberListAdapter);
            rvMembers.setAdapter(groupMemberListAdapter);
        } else {
            groupMemberListAdapter.refreshList(groupMemberList);
        }
    }

    @Override
    public void removeMember(String uid) {
        if (groupMemberListAdapter!=null){
            groupMemberListAdapter.removeMember(uid);
            try {
                getContext().sendBroadcast(new Intent().setAction("refresh"));
            }catch (NullPointerException e){
                e.printStackTrace();
            }


        }
    }


}