package com.example.homemade_guardian_beta.Main.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.homemade_guardian_beta.Main.bottombar.ChatroomListFragment;
import com.example.homemade_guardian_beta.Main.bottombar.HomeFragment;
import com.example.homemade_guardian_beta.Main.bottombar.MyInfoFragment;
import com.example.homemade_guardian_beta.Main.bottombar.WritePostFragment;
import com.example.homemade_guardian_beta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //안드로이드 생명주기중 하나
    @Override
    protected void onResume() {
        super.onResume();
    }

    //안드로이드 생명주기중 하나
    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                init();
                break;
        }
    }

    private void init() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();                                // part5 : 로그인 시        // part22 : 운래는 옮길때 homeFragment로 옮겨졌으나 매번 불러오는것이 비효율적이라 여기로 옮김
        if (firebaseUser == null) {
            myStartActivity(LoginActivity.class);                                                              // part5 : 로그인 정보 없으면 회원가입 화면으로
        } else {
            //myStartActivity(CameraActivity.class);                                                            // part5 : test
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {                                                            // part5 : 로그인 되있었어도 정보가 있으면 불러오고
                            } else {                                                                            // part5 : 아니면 입력받는다.   (18')
                                myStartActivity(MemberInitActivity.class);
                            }
                        }
                    } else {
                        //Log.d("태그", "get failed with ", task.getException());
                    }
                }
            });
        }
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);            // part22 : 바텀 네비게이션바  설정 (47'20")
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        HomeFragment homeFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, homeFragment)
                                .commit();
                        return true;
                    case R.id.writepost:
                        WritePostFragment writepostFragment = new WritePostFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, writepostFragment)
                                .commit();
                        return true;
                    case R.id.chatroomlist:
                        ChatroomListFragment chatroomFragment = new ChatroomListFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, chatroomFragment)
                                .commit();
                        return true;
                    case R.id.myinfo:
                        MyInfoFragment myinfoFragment = new MyInfoFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, myinfoFragment)
                                .commit();
                        return true;
                }
                return false;
            }
        });
        //여기에 들어가는지 확실x
        //sendRegistrationToServer();
    }

    private void myStartActivity(Class c) {                                                             // part22 : c에다가 이동하려는 클래스를 받고 requestcode는 둘다 1로 준다.
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }

}