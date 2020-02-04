package com.example.mesajlasmauygulamasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String username, othername;
    TextView chatUsername;
    EditText chatEditText;
    ImageView sendImage,backImage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    RecyclerView chatRecyView;
    MesajAdapter mesajAdapter;
    List<MesajModel> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tanimla();
        loadMesaj();
    }

    public void tanimla(){
        list = new ArrayList<>();

        username = getIntent().getExtras().getString("username");
        othername = getIntent().getExtras().getString("othername");

        Log.i("alınandegerler : ",username+"--"+othername);

        chatUsername = (TextView)findViewById(R.id.chatUsername);
        chatEditText = (EditText)findViewById(R.id.chatEditText);
        sendImage = (ImageView)findViewById(R.id.sendImage);
        backImage = (ImageView)findViewById(R.id.backImage);
        chatUsername.setText(othername);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                intent.putExtra("kadi",username);
                startActivity(intent);
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mesaj = chatEditText.getText().toString();
                chatEditText.setText("");
                mesajGonder(mesaj);
            }
        });

        chatRecyView = (RecyclerView)findViewById(R.id.chatRecyView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatActivity.this,1);
        chatRecyView.setLayoutManager(layoutManager);

        mesajAdapter = new MesajAdapter(ChatActivity.this,list,ChatActivity.this,username);

        chatRecyView.setAdapter(mesajAdapter);

    }

    public void mesajGonder(String text){
        final String key = reference.child("Mesajlar").child(username).child(othername).push().getKey();
        final Map messageMap = new HashMap();
        messageMap.put("text",text);
        messageMap.put("from",username);
        reference.child("Mesajlar").child(username).child(othername).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()){
                    reference.child("Mesajlar").child(othername).child(username).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {

                        }
                    });
                }
            }
        });
    }

    public void loadMesaj(){
        reference.child("Mesajlar").child(username).child(othername).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MesajModel mesajModel = dataSnapshot.getValue(MesajModel.class);
                list.add(mesajModel);
                mesajAdapter.notifyDataSetChanged();
                chatRecyView.scrollToPosition(list.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
