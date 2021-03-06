package com.example.mostafahussien.chatna.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafahussien.chatna.R;
import com.example.mostafahussien.chatna.Model.users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    users user;
    CircleImageView profileImage;
    TextView userName, status, userEmail, friends, mutualFriends;
    Button infoBTN, aboutBTN, friendRequest, requestDecline;
    CardView infoCard, aboutCard;
    DatabaseReference friendRequestDB;
    private DatabaseReference rootRef;
    DatabaseReference notificationDB;
    DatabaseReference friendDatabase;
    FirebaseUser currentID;
    String visitedUserID,current_state,frame;
    boolean fromNotification;
    List<String> currentUserFriedns, visistedUserFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = getIntent().getParcelableExtra("user");
        visitedUserID = getIntent().getExtras().getString("userID");
        fromNotification = getIntent().getExtras().getBoolean("notfication", false);
        profileImage = (CircleImageView) findViewById(R.id.user_profile_image);
        userName = (TextView) findViewById(R.id.profile_user_name);
        status = (TextView) findViewById(R.id.profile_user_status);
        userEmail = (TextView) findViewById(R.id.profile_user_email);
        friends = (TextView) findViewById(R.id.friends_num);
        mutualFriends = (TextView) findViewById(R.id.mutual_friends_num);
        infoBTN = (Button) findViewById(R.id.info_btn);
        aboutBTN = (Button) findViewById(R.id.about_btn);
        requestDecline = (Button) findViewById(R.id.decline_friend);
        requestDecline.setVisibility(View.GONE);
        friendRequest = (Button) findViewById(R.id.add_friend);
        frame = "info";
        current_state = "not_friend";
        infoCard = (CardView) findViewById(R.id.profile_user_info);
        aboutCard = (CardView) findViewById(R.id.profile_user_about);

        friendRequestDB = FirebaseDatabase.getInstance().getReference().child("friends_req");
        friendDatabase = FirebaseDatabase.getInstance().getReference().child("friends");

        notificationDB = FirebaseDatabase.getInstance().getReference().child("notifications");
        currentID = FirebaseAuth.getInstance().getCurrentUser();
        fillViews();
        checRequestkState();
    }
    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        rootRef=FirebaseDatabase.getInstance().getReference().child("users").child(currentID.getUid());
        rootRef.child("online").setValue(false);

    }*/

    public void fillViews() {
        if (fromNotification) {
            DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(visitedUserID);
            mUsersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String display_name = dataSnapshot.child("name").getValue().toString();
                    String Ustatus = dataSnapshot.child("status").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    userName.setText(display_name);
                    status.setText(Ustatus);
                    userEmail.setText(email);
                    if (!image.equals("default")) {
                        Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.profileimage).into(profileImage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            userName.setText(user.getName());
            status.setText(user.getStatus());
            userEmail.setText(user.getEmail());
            if (!user.getImage().equals("default")) {
                Picasso.with(ProfileActivity.this).load(user.getImage()).placeholder(R.drawable.profileimage).into(profileImage);
            }
        }

        currentUserFriedns = new ArrayList<>();
        visistedUserFriends = new ArrayList<>();
        friendDatabase.child(visitedUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String friendsNum = String.valueOf(dataSnapshot.getChildrenCount());
                friends.setText(friendsNum);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    visistedUserFriends.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (currentID.getUid().equals(visitedUserID)) {
            requestDecline.setVisibility(View.GONE);
            friendRequest.setVisibility(View.GONE);
        } else {                                                                                                  // get mutual friends
            final List<String> commonFriends = new ArrayList<String>(currentUserFriedns);
            friendDatabase.child(currentID.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        currentUserFriedns.add(snapshot.getKey());
                    }
                    int comFriends = 0;
                    for (String id1 : visistedUserFriends) {
                        for (String id2 : currentUserFriedns) {
                            if (id1.equals(id2)) {
                                comFriends++;
                                break;
                            }
                        }
                    }
                    mutualFriends.setText(String.valueOf(comFriends));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void getInfo(View view) {
        if (frame.equals("about")) {
            aboutCard.setVisibility(View.GONE);
            infoCard.setVisibility(View.VISIBLE);
            infoBTN.setBackgroundColor(getResources().getColor(R.color.buttonPressed));
            aboutBTN.setBackgroundColor(getResources().getColor(R.color.buttonRelease));
            frame = "info";
        }
    }

    public void getAbout(View view) {
        if (frame.equals("info")) {
            aboutCard.setVisibility(View.VISIBLE);
            infoCard.setVisibility(View.GONE);
            aboutBTN.setBackgroundColor(getResources().getColor(R.color.buttonPressed));
            infoBTN.setBackgroundColor(getResources().getColor(R.color.buttonRelease));
            frame = "about";
        }
    }

    public void checRequestkState() {
        String currentUserid = currentID.getUid();
        friendRequestDB.child(currentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // check if current object child(currentID) has visited profile ID child
                if (dataSnapshot.hasChild(visitedUserID)) {
                    String req_type = dataSnapshot.child(visitedUserID).child("request_type").getValue().toString();
                    if (req_type.equals("received")) {
                        current_state = "req_received";
                        friendRequest.setText("Accept Friend Request");
                        requestDecline.setVisibility(View.VISIBLE);
                    } else if (req_type.equals("send")) {
                        current_state = "req_sent";
                        friendRequest.setText("Cancel Friend Request");
                        requestDecline.setVisibility(View.GONE);
                    }
                } else {        // check currentID and visited profile ID are friends or not ...
                    friendDatabase.child(currentID.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(visitedUserID)) {
                                current_state = "friends";
                                friendRequest.setText("UnFriend");
                                requestDecline.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void addFriend(View view) {
        friendRequest.setEnabled(false);
        // Not friend case ...
        if (current_state.equals("not_friend")) {
            friendRequestDB.child(currentID.getUid())
                    .child(visitedUserID).child("request_type").setValue("send").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()) {
                        friendRequestDB.child(visitedUserID).child(currentID.getUid())
                                .child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {  // onSuccess listen only if above task is success but on Complete listen even success or failed
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendNotification();
                                friendRequest.setText("Cancel Friend Request");
                                requestDecline.setVisibility(View.GONE);
                                current_state = "req_sent";
                                Toast.makeText(ProfileActivity.this, "Request send successfully ", Toast.LENGTH_LONG).show();

                            }
                        });
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to send request !", Toast.LENGTH_LONG).show();
                    }
                    friendRequest.setEnabled(true);
                }
            });
        }

        // cancel request case
        if (current_state.equals("req_sent")) {
            removeFriendRequest("not_friend", "Add Friend");
        }

        // accept request case
        if (current_state.equals("req_received")) {
            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
            friendDatabase.child(currentID.getUid()).child(visitedUserID).child("date")
                    .setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    friendDatabase.child(visitedUserID).child(currentID.getUid()).child("date")
                            .setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            removeFriendRequest("friends", "UnFriend");
                        }
                    });
                }
            });
        }
        // press unFriend
        if (current_state.equals("friends")) {
            friendDatabase.child(currentID.getUid()).child(visitedUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    friendDatabase.child(visitedUserID).child(currentID.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            current_state = "not_friend";
                            friendRequest.setText("Add Friend");
                            requestDecline.setVisibility(View.GONE);
                            friendRequest.setEnabled(true);
                        }
                    });
                }
            });
        }
    }

    public void sendNotification() {
        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("from", currentID.getUid());
        notificationData.put("type", "request");
        // push() to create random id to store new notification
        notificationDB.child(visitedUserID).push().setValue(notificationData);
    }

    public void removeFriendRequest(final String currState, final String buttonText) {
        friendRequestDB.child(currentID.getUid()).child(visitedUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                friendRequestDB.child(visitedUserID).child(currentID.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendRequest.setEnabled(true);
                        friendRequest.setText(buttonText);
                        current_state = currState;
                        requestDecline.setVisibility(View.GONE);
                    }
                });
                friendRequest.setEnabled(false);
            }
        });

    }

    public void declineRequest(View view) {
        removeFriendRequest("not_friend", "Add Friend");
    }
}
