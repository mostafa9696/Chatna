package com.example.mostafahussien.chatna.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mostafahussien.chatna.Fragment.ChatsFragment;
import com.example.mostafahussien.chatna.Fragment.FriendsFragment;
import com.example.mostafahussien.chatna.Fragment.RequestsFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter{
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            RequestsFragment requestsFragment=new RequestsFragment();
            return requestsFragment;
        } else if(position==1){
            ChatsFragment chatsFragment=new ChatsFragment();
            return chatsFragment;
        }else if(position==2){
            FriendsFragment friendsFragment=new FriendsFragment();
            return friendsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
