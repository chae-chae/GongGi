package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myapplication.HomeAdapter;
import com.example.myapplication.PostInfo;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActiviy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
//            startJoinActivity();
            myStartActivity(JoinActivity.class);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
//            Log.d(TAG, "check docRef" + docRef.toString());
            docRef.get().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });

            db.collection("posts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<PostInfo> postList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    postList.add(new PostInfo(
                                            document.getData().get("title").toString(),
                                            document.getData().get("contents").toString(),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime())));
                                }
                                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                                RecyclerView.Adapter mAdapter = new HomeAdapter(MainActivity.this, postList);
                                recyclerView.setAdapter(mAdapter);
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
//
//            ArrayList<String> arrayList = new ArrayList<>();
//            arrayList.add("test1");
//            arrayList.add("test2");
//            arrayList.add("test3");
//
//            RecyclerView recyclerView = findViewById(R.id.recyclerView);
//            recyclerView.setHasFixedSize(true);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//            RecyclerView.Adapter mAdapter = new HomeAdapter(this, arrayList);
//            recyclerView.setAdapter(mAdapter);
        }

        //findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.floatingActionButton2).setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.logoutButton: 추후 사용
//                    FirebaseAuth.getInstance().signOut();
//                    startJoinActivity();
//                    break;
                case R.id.floatingActionButton2:
                    startWritePostActivity();
                    break;
            }
        }
    };

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }



//    private void startJoinActivity() {
//        Intent intent = new Intent(this, JoinActivity.class);
//        startActivity(intent);
//    }
//
    private void startWritePostActivity() {
        Intent intent = new Intent(this, WritePostActivity.class);
        startActivity(intent);
    }

}



