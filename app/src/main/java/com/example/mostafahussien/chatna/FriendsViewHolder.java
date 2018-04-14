package com.example.mostafahussien.chatna;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mostafa Hussien on 14/04/2018.
 */

public class FriendsViewHolder extends RecyclerView.ViewHolder {
    View view;
    public FriendsViewHolder(View itemView) {
        super(itemView);
        view=itemView;
    }
    public void setFriendInfo(String name, String date, String image, Context context){
        TextView userName=(TextView)view.findViewById(R.id.row_user_name);
        userName.setText(name);
        TextView friendDate=(TextView)view.findViewById(R.id.row_user_status);
        friendDate.setText(date);
        CircleImageView imageView=(CircleImageView)view.findViewById(R.id.row_profile_image);
        Picasso.with(context).load(image).placeholder(R.drawable.profileimage).into(imageView);
    }
}
