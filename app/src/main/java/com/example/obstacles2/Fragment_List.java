package com.example.obstacles2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Fragment_List extends Fragment {


    private AppCompatActivity activity;

    private CallBack_List callBackList;

    private MaterialButton[] top_Ten;

    private MaterialButton list_BTN_Reset;

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setCallBackList(CallBack_List callBackList) {
        this.callBackList = callBackList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        findViews(view);
        initViews();

        clearButtonInit();



        return view;
    }


    public void clearButtonInit() {
        list_BTN_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDB myDB = new MyDB();

                String json = new Gson().toJson(myDB);
                MSPV3.getMe().putString("MY_DB", json);

                initViews();
                callBackList.clearListClicked();
            }
        });
    }


    private void initViews() {

        String js = MSPV3.getMe().getString("MY_DB", "");
        MyDB md = new Gson().fromJson(js, MyDB.class);

        ArrayList<Record> rec = md.getRecords();


        for (int i = 0; i < rec.size(); i++) {
            Record temp = rec.get(i);
            top_Ten[i].setText(i + 1 + ". " + temp.getName() + ": " + temp.getScore() + " Sec");
            top_Ten[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBackList.rowSelected(temp.getLon(), temp.getLat(), temp.getName());
                }
            });

        }
//        top_Ten[0].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callBackList.rowSelected(2.4, 3.5, top_Ten[0].getText().toString());
//            }
//        });
    }

    private void findViews(View view) {



        top_Ten = new MaterialButton[]{
                view.findViewById(R.id.high_scores_LBL_0),
                view.findViewById(R.id.high_scores_LBL_1),
                view.findViewById(R.id.high_scores_LBL_2),
                view.findViewById(R.id.high_scores_LBL_3),
                view.findViewById(R.id.high_scores_LBL_4),
                view.findViewById(R.id.high_scores_LBL_5),
                view.findViewById(R.id.high_scores_LBL_6),
                view.findViewById(R.id.high_scores_LBL_7),
                view.findViewById(R.id.high_scores_LBL_8),
                view.findViewById(R.id.high_scores_LBL_9),

        };

        list_BTN_Reset = view.findViewById(R.id.list_BTN_Reset);
    }
}
