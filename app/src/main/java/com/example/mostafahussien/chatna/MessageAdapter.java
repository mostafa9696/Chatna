package com.example.mostafahussien.chatna;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mostafa Hussien on 17/04/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Messages> messages;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    public MessageAdapter(List<Messages> mMessageList) {
        this.messages = mMessageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.e("ww7", String.valueOf(messages.size()));
        Messages message = messages.get(position);
        String from_user = message.getFrom();
        String message_type = message.getType();
        auth=FirebaseAuth.getInstance();
        String currentID=auth.getCurrentUser().getUid();
        if(from_user.equals(currentID)){
            holder.messageText.setTextColor(Color.BLUE);
        }else {
            holder.messageText.setTextColor(Color.CYAN);
        }
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(from_user);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();
                //holder.displayName.setText(name);
                Picasso.with(holder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.profileimage).into(holder.profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (message_type.equals("text")) {
            holder.messageText.setText(message.getMessage());
            holder.messageImage.setVisibility(View.INVISIBLE);
        } else {
            Picasso.with(holder.profileImage.getContext()).load(message.getMessage())
                    .placeholder(R.drawable.default_avatar).into(holder.messageImage);
            holder.messageText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public CircleImageView profileImage;
        //public TextView displayName;
        public ImageView messageImage;

        public ViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_text);
            profileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_layout);
            // displayName = (TextView) itemView.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) itemView.findViewById(R.id.message_image_layout);
        }
    }
}
