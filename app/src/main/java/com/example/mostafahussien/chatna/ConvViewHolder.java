package com.example.mostafahussien.chatna;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mostafa Hussien on 20/04/2018.
 */

public class ConvViewHolder extends RecyclerView.ViewHolder {
    View view;
    CircleImageView imageView;
    TextView userName,userStatus;
    ImageView userOnlineIcon;
    public ConvViewHolder(View itemView) {
        super(itemView);
        view=itemView;
    }
    public void setConvInfo(String name, String image, String userOnline,String lastMsg, Context context){
        userName=(TextView)view.findViewById(R.id.row_user_name);
        userStatus=(TextView)view.findViewById(R.id.row_user_status);
        userStatus.setText(lastMsg);
        userName.setText(name);
        view.findViewById(R.id.view_profile_icon).setVisibility(View.GONE);
        view.findViewById(R.id.message_icon).setVisibility(View.GONE);
        imageView = (CircleImageView) view.findViewById(R.id.row_profile_image);
        Picasso.with(context).load(image).placeholder(R.drawable.profileimage).into(imageView);
        userOnlineIcon= (ImageView) view.findViewById(R.id.user_online);
        if(userOnline.equals("online")){
            userOnlineIcon.setVisibility(View.VISIBLE);
        } else {
            userOnlineIcon.setVisibility(View.INVISIBLE);
        }
    }
}
