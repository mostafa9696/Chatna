package com.example.mostafahussien.chatna;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {
    List<users> usersList;
    List<String> usersIDs;
    Context context;
    OnPressFriendRequestUser friendRequestUser;
    public FriendRequestAdapter(List<users> usersList, Context context,List<String> usersIDs,OnPressFriendRequestUser friendRequestUser) {
        this.usersList=usersList;
        this.context=context;
        this.usersIDs=usersIDs;
        this.friendRequestUser=friendRequestUser;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.fridends_request_row,parent,false);
        return new FriendRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final users user=usersList.get(position);
        holder.userName.setText(user.getName());
        Picasso.with(context).load(user.getImage()).placeholder(R.drawable.profileimage).into(holder.userImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendRequestUser.pressRequestedUser(user,usersIDs.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView userImage;
        public ViewHolder(View itemView) {
            super(itemView);
            userName=(TextView) itemView.findViewById(R.id.request_userName);
            userImage=(CircleImageView)itemView.findViewById(R.id.request_friend_profile_layout);

        }
    }
}
