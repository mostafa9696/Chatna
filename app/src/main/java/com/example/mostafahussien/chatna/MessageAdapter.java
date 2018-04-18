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

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Messages> messages;
    private FirebaseAuth auth;
    private String currentID,receiverImage;
    private boolean sender=false;
    public MessageAdapter(List<Messages> mMessageList,String receiverImageUri) {
        this.messages = mMessageList;
        auth=FirebaseAuth.getInstance();
        currentID=auth.getCurrentUser().getUid();
        receiverImage=receiverImageUri;
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        String userID=messages.get(position).getFrom().toString();
        if(userID.equals(currentID)){           // sender case
            return 1;
        } else {             // receiver case
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_layout_right, parent, false);
            return new RightViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_layout_left, parent, false);
            return new LeftViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder  holder, int position) {

        Messages message = messages.get(position);
        String message_type = message.getType();
        if(holder.getItemViewType()==1) {
            RightViewHolder rightHolder = (RightViewHolder) holder;
            if (message_type.equals("text")) {
                rightHolder.messageText.setText(message.getMessage());
                rightHolder.messageImage.setVisibility(View.INVISIBLE);
            } else {
                Picasso.with(rightHolder.messageImage.getContext()).load(message.getMessage())
                        .placeholder(R.drawable.default_avatar).into(rightHolder.messageImage);
                rightHolder.messageText.setVisibility(View.INVISIBLE);
            }
        } else {
            LeftViewHolder leftViewHolder = (LeftViewHolder) holder;
            if (message_type.equals("text")) {
                leftViewHolder.messageText.setText(message.getMessage());
                leftViewHolder.messageImage.setVisibility(View.INVISIBLE);
                Picasso.with(leftViewHolder.profileImage.getContext()).load(receiverImage)
                        .placeholder(R.drawable.default_avatar).into(leftViewHolder.profileImage);
            } else {
                Picasso.with(leftViewHolder.messageImage.getContext()).load(message.getMessage())
                        .placeholder(R.drawable.default_avatar).into(leftViewHolder.messageImage);
                leftViewHolder.messageText.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public CircleImageView profileImage;
        public ImageView messageImage;

        public LeftViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.left_message_text);
            profileImage = (CircleImageView) itemView.findViewById(R.id.left_message_profile_layout);
            messageImage = (ImageView) itemView.findViewById(R.id.left_message_image_layout);
        }
    }
    public class RightViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public ImageView messageImage;

        public RightViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.right_message_text);
            messageImage = (ImageView) itemView.findViewById(R.id.right_message_image_layout);
        }
    }
}
