package com.example.homemade_guardian_beta.Main.bottombar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.homemade_guardian_beta.Main.activity.Loding_Dialog;
import com.example.homemade_guardian_beta.Main.activity.MainActivity;
import com.example.homemade_guardian_beta.Main.activity.MyInfoPostActivity;
import com.example.homemade_guardian_beta.Main.activity.UpdateInfoActivity;
import com.example.homemade_guardian_beta.R;
import com.example.homemade_guardian_beta.model.user.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyInfoFragment extends Fragment {
    ImageView Myinfo_profileImage;
    TextView Myinfo_profileNickName, Myinfo_profileUniversity;
    LinearLayout Proceeding_Post,Deal_Complete_Post,My_Writen_Post;
    TextView Kind_Count,Complete_Count,Correct_Count,Bad_Count;
    LinearLayout LogOut_Button,To_Reviews_written;
    Button Update_InfoBtn;
    private String CurrentUid;
    private FirebaseUser CurrentUser;
    UserModel userModel = new UserModel();
    UpdateInfoActivity updateInfoActivity;


    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater,@Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myinfo, container, false);
        Toolbar myToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("내 정보");
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            userModel = new UserModel();
            userModel = (UserModel) bundle.getSerializable("userModel");
        }

//        updateInfoActivity = new UpdateInfoActivity();
//        Intent intent = getActivity().getIntent();
//        UserModel userModel = (UserModel)intent.getSerializableExtra("userModel");
//        Bundle Tobundle = new Bundle();
//        Tobundle.putSerializable("userModel",userModel);
//        updateInfoActivity.setArguments(Tobundle);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        CurrentUid =CurrentUser.getUid();

        //Myinfo_profileImage = (ImageView) view.findViewById(R.id.Myinfo_profileImage);
        Myinfo_profileNickName = (TextView) view.findViewById(R.id.Myinfo_profileNickName);
        Myinfo_profileUniversity = (TextView) view.findViewById(R.id.Myinfo_profileUniversity);

        Proceeding_Post = view.findViewById(R.id.Proceeding_Post);
        Proceeding_Post.setOnClickListener(onClickListener);
        Deal_Complete_Post = view.findViewById(R.id.Deal_Complete_Post);
        Deal_Complete_Post.setOnClickListener(onClickListener);
        My_Writen_Post = view.findViewById(R.id.My_Writen_Post);
        My_Writen_Post.setOnClickListener(onClickListener);

        Kind_Count = (TextView) view.findViewById(R.id.Kind_Count);
        Complete_Count = (TextView) view.findViewById(R.id.Complete_Count);
        Correct_Count = (TextView) view.findViewById(R.id.Correct_Count);
        Bad_Count = (TextView) view.findViewById(R.id.Bad_Count);

        LogOut_Button = view.findViewById(R.id.LogOut_Button);
        LogOut_Button.setOnClickListener(onClickListener);
        To_Reviews_written = view.findViewById(R.id.To_Reviews_written);
        To_Reviews_written.setOnClickListener(onClickListener);

        Update_InfoBtn = view.findViewById(R.id.Update_Info_Buutton);
        Update_InfoBtn.setOnClickListener(onClickListener);

        Profile_Info(userModel);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Proceeding_Post:
                    myStartActivity(MyInfoPostActivity.class,"0");
                    break;
                case R.id.Deal_Complete_Post:
                    myStartActivity(MyInfoPostActivity.class,"1");
                    break;
                case R.id.My_Writen_Post:
                    myStartActivity(MyInfoPostActivity.class,"2");
                    break;
                case R.id.To_Reviews_written:
                    myStartActivity(MyInfoPostActivity.class,"3");
                    break;
                case R.id.Update_Info_Buutton:
                    myStartActivity(UpdateInfoActivity.class,"4");
                    break;
                case R.id.LogOut_Button:
                    ((MainActivity)getActivity()).Logout_dialog();
                    break;
            }
        }
    };

    public void setUsermodel(Intent data){
        userModel = new UserModel();
        userModel = (UserModel) data.getExtras().getSerializable("userModel");
        Profile_Info(userModel);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("dataa", "data: " + data);
                //setUsermodel(data);
                Log.d("dataa", "userModel2: " + userModel.getUserModel_NickName());
            }
        }
    }

    public void Profile_Info(UserModel Usermodel){
        Myinfo_profileNickName.setText(Usermodel.getUserModel_NickName());
        if(Usermodel.getUserModel_University() == 0){
            Myinfo_profileUniversity.setText("홍익대학교");
        }
        else {
            Myinfo_profileUniversity.setText("고려대학교");
        }
        //post의 이미지 섬네일 띄우기
        if(Usermodel.getUserModel_ProfileImage() != null){
            //Glide.with(getContext()).load(Usermodel.getUserModel_ProfileImage()).centerCrop().into(Myinfo_profileImage);
        }
        else{
            //Glide.with(getContext()).load(R.drawable.none_profile_user).centerCrop().into(Myinfo_profileImage);
        }
        Kind_Count.setText("X"+Usermodel.getUserModel_kindReviewList().size());
        Complete_Count.setText("X"+Usermodel.getUserModel_completeReviewList().size());
        Correct_Count.setText("X"+Usermodel.getUserModel_correctReviewList().size());
        Bad_Count.setText("X"+Usermodel.getUserModel_badReviewList().size());

    }

    private void myStartActivity(Class c,String Info) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("Info",Info);
        intent.putExtra("CurrentUid",CurrentUid);
        intent.putExtra("userModel",userModel);
        startActivityForResult(intent, 0);
    }


}