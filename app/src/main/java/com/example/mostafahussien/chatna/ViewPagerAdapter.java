package com.example.mostafahussien.chatna;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


class ViewPagerAdapter extends FragmentPagerAdapter{
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
    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "REQUESTS";

            case 1:
                return "CHATS";

            case 2:
                return "FRIENDS";

            default:
                return null;
        }

    }
}
