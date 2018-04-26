package com.example.mostafahussien.chatna.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mostafahussien.chatna.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public UsersViewHolder(View itemView) {
        super(itemView);
        view=itemView;
    }
    public void setUserInfo(String name, String status, String image, Context context){

        TextView userName=(TextView)view.findViewById(R.id.row_user_name);
        userName.setText(name);
        TextView userStatus=(TextView)view.findViewById(R.id.row_user_status);
        userStatus.setText(status);
        CircleImageView imageView=(CircleImageView)view.findViewById(R.id.row_profile_image);
        Picasso.with(context).load(image).placeholder(R.drawable.profileimage).into(imageView);
    }

}
