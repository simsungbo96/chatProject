package com.example.chatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoRoom extends AppCompatActivity {
    @BindView(R.id.et_roomNumber) EditText roomNumber_et;
    @BindView(R.id.btn_goRoom) Button goRoom_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_room2);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.btn_goRoom)
    void onFinish(){
        String safeNumber = roomNumber_et.getText().toString()+"sbversionsimplemqtt";
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("Topic",safeNumber);
        startActivity(intent);
        finish();
    }

}