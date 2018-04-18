package com.example.mostafahussien.chatna;

import android.media.Image;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActiity extends AppCompatActivity {
    private String receiverUserID, receiverName, senderUserID,receiverImageUri;
    private Toolbar chatToolbar;
    private TextView userName, lastSeen;
    private ImageView userImage;
    private DatabaseReference reference,messsageDB;
    private FirebaseAuth auth;
    private ImageButton chatAddBtn, chatSendBtn;
    private EditText chatMessage;
    private RecyclerView messagesList;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout refreshLayout;
    private List<Messages> messagess;
    private MessageAdapter adapter;
    private int currentMessagesPage=1;
    private int itemPosition=0;
    private String lastRefreshedKey="empty";
    private String prevRefreshedKey="empty";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_actiity);
        receiverUserID = getIntent().getStringExtra("userID");
        receiverName = getIntent().getStringExtra("userName");
        receiverImageUri=getIntent().getStringExtra("image");
        chatToolbar = (Toolbar) findViewById(R.id.chat_appBar);
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reference = FirebaseDatabase.getInstance().getReference();
        userName = (TextView) findViewById(R.id.toolbar_text);
        lastSeen = (TextView) findViewById(R.id.toolbar_lastSeen);
        lastSeen.setVisibility(View.VISIBLE);
        userName.setText(receiverName);
        userImage = (ImageView) findViewById(R.id.toolbar_icon);
        userImage.setImageResource(R.drawable.ic_chat_toolbar);
        auth = FirebaseAuth.getInstance();
        senderUserID = auth.getCurrentUser().getUid();
        chatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        chatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        chatMessage = (EditText) findViewById(R.id.chat_message_view);
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentMessagesPage++;
                itemPosition=0;
                loadRefreshedMessages();
            }
        });
        loadMessages();
        getUserData();
    }
    public void loadRefreshedMessages(){
        DatabaseReference messageRef=reference.child("Messages").child(senderUserID).child(receiverUserID);
        Query messageQuery=messageRef.orderByKey().endAt(lastRefreshedKey).limitToLast(10);  //endAt to load messages before endAt key
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message=dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();
                if(!prevRefreshedKey.equals(messageKey)){
                    messagess.add(itemPosition++,message);
                } else {
                    prevRefreshedKey=lastRefreshedKey;
                }
                if(itemPosition==1){    // first item load to list, so get its key
                    lastRefreshedKey=messageKey;
                }

                adapter.notifyDataSetChanged();
                //messagesList.scrollToPosition(messagess.size()-1);
                refreshLayout.setRefreshing(false);
                layoutManager.scrollToPositionWithOffset(10,0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void loadMessages() {
        DatabaseReference messageRef=reference.child("Messages").child(senderUserID).child(receiverUserID);
        Query messageQuery=messageRef.limitToLast(currentMessagesPage*10);  // get last 10 messages to show them in recyclerView
        messagess=new ArrayList<>();
        adapter=new MessageAdapter(messagess,receiverImageUri);
        layoutManager=new LinearLayoutManager(this);
        messagesList=(RecyclerView)findViewById(R.id.messages_list);
        messagesList.setLayoutManager(layoutManager);
        messagesList.setAdapter(adapter);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message=dataSnapshot.getValue(Messages.class);
                itemPosition++;
                if(itemPosition==1){    // first item load to list, so get its key
                    String messageKey = dataSnapshot.getKey();
                    lastRefreshedKey=messageKey;
                    prevRefreshedKey=messageKey;
                }
                messagess.add(message);
                adapter.notifyDataSetChanged();
                messagesList.scrollToPosition(messagess.size()-1);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserData() {
        reference.child("users").child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                if (online.equals("true")) {
                    lastSeen.setText("Online");
                } else {
                    TimeUtility timeUtility = new TimeUtility();
                    long lastTimeSeen = Long.parseLong(online);
                    String lastStringSeen = timeUtility.getTime(lastTimeSeen, getApplicationContext());
                    lastSeen.setText(lastStringSeen);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reference.child("Chat").child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(receiverUserID)) {      // check if sender and receiver not chating before
                    Map chatMap = new HashMap();
                    chatMap.put("seen", false);
                    chatMap.put("timestamp", ServerValue.TIMESTAMP);
                    Map chatUserMap = new HashMap();
                    chatUserMap.put("chat/" + senderUserID + "/" + receiverUserID, chatMap);
                    chatUserMap.put("chat/" + receiverUserID + "/" + senderUserID, chatMap);
                    reference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.e("Message_Log", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void clickAddBtn(View view) {
    }

    public void clickSendMessage(View view) {
        String message = chatMessage.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            DatabaseReference userMessagePush = reference.child("Messages").child(senderUserID)
                    .child(receiverUserID).push();
            String push_id = userMessagePush.getKey();
            String senderUserRef = "Messages/" + senderUserID + "/" + receiverUserID;
            String receiverUserRef = "Messages/" + receiverUserID + "/" + senderUserID;
            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", senderUserID);
            Map messageUserMap = new HashMap();
            messageUserMap.put(senderUserRef + "/" + push_id, messageMap);       // push message info under "messages" -> senderID -> receiverID
            messageUserMap.put(receiverUserRef + "/" + push_id, messageMap);     // push message info under "messages" -> receiverID -> senderID
            chatMessage.setText("");
            reference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e("Message_Log", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }
}
