package com.example.chatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ChatAdapter extends BaseAdapter {
    private ArrayList<DTO_chat> chatList = new ArrayList<>();

    public void add(DTO_chat chatItem){
        chatList.add(chatItem);
    }

    public void remove(int count) {
            chatList.remove(count);
    }
    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int i) {
        return chatList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_adapter,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.idTextView = convertView.findViewById(R.id.idText);
            viewHolder.contentTextView = convertView.findViewById(R.id.contentText);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.idTextView.setText(chatList.get(position).getId());
        viewHolder.contentTextView.setText(chatList.get(position).getContent());
        return convertView;
    }

    private class ViewHolder{
        TextView idTextView;
        TextView contentTextView;


    }
}