package com.example.mostafahussien.chatna.ViewHolder;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mostafahussien.chatna.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mostafa Hussien on 14/04/2018.
 */

public class FriendsViewHolder extends RecyclerView.ViewHolder {
    View view;
    public ImageView viewProfile,userChat;
    public FriendsViewHolder(View itemView) {
        super(itemView);
        view=itemView;
        viewProfile=(ImageView)view.findViewById(R.id.view_profile_icon);
        userChat=(ImageView)view.findViewById(R.id.message_icon);
        viewProfile.setVisibility(View.VISIBLE);
        userChat.setVisibility(View.VISIBLE);
    }
    public void setFriendInfo(String name, String date, String image, Context context){
        TextView userName=(TextView)view.findViewById(R.id.row_user_name);
        userName.setText(name);
        TextView friendDate=(TextView)view.findViewById(R.id.row_user_status);
        friendDate.setText(date);
        CircleImageView imageView=(CircleImageView)view.findViewById(R.id.row_profile_image);
        Picasso.with(context).load(image).fit().placeholder(R.drawable.profileimage).into(imageView);
    }
    public void setUserOnline(String isOnline){
        ImageView onlineView=(ImageView)view.findViewById(R.id.user_online);
        if(isOnline.equals("true")){
            onlineView.setVisibility(View.VISIBLE);
        }else {
            onlineView.setVisibility(View.GONE);
        }
    }
}
