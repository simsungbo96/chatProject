package com.example.chatproject;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity{
    private ChatAdapter chatAdapter;
    private MqttClient mqttClient;
    String text;
    EditText textEt;
    int timeValue = 10000;

    private InputMethodManager imm;
    Timer timer = new Timer();
    TimerTask clearTask = new TimerTask() {
        @Override
        public void run() {
                try {
                    if (chatAdapter.getCount() > 0) {
                        chatAdapter.remove(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatAdapter.notifyDataSetInvalidated();
                            }
                        });
                    }
                }catch(IndexOutOfBoundsException indexOutOfBoundsException){
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return true;
    }
    public void tempTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                try {
                    if (chatAdapter.getCount() > 0) {
                        chatAdapter.remove(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatAdapter.notifyDataSetInvalidated();
                            }
                        });
                    }
                }catch(IndexOutOfBoundsException indexOutOfBoundsException){
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, 10000);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.timer_option_1:
                timer.cancel();
                break;
            case R.id.timer_option_5:
                tempTask();
                Log.e("timevalue",timeValue+"");
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
protected void onCreate(Bundle savedInstanceState) {
    Intent intent = getIntent(); /*데이터 수신*/
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    textEt = findViewById(R.id.et_text);
    imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    ListView chatListView;
    chatListView = findViewById(R.id.lv);
    chatAdapter = new ChatAdapter();
    chatListView.setAdapter(chatAdapter);
    String clientID = MqttClient.generateClientId();
    try{connectMqtt();}catch(Exception ignored){

    }
    textEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        // TODO : keypad 에서 enter 실행시 Listen하고 동작할 액션을 작성
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            text = textEt.getText().toString() + "";
            if (textEt.toString().equals("")) {
            } else {
                try {

                    JSONObject json = new JSONObject();
                    String id;
                    id = clientID.replaceFirst("paho", "");
                    json.put("id", id);
                    json.put("content", text);
                    mqttClient.publish(intent.getStringExtra("Topic"), new MqttMessage(json.toString().getBytes()));
                    textEt.setText("");
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(textEt, InputMethodManager.SHOW_IMPLICIT);
                } catch (MqttException | JSONException e) {

                    e.printStackTrace();

                }
            }
            imm.hideSoftInputFromWindow(textEt.getWindowToken(), 0);
            return true;

        }

    });

}
    private void connectMqtt() throws Exception{
        mqttClient = new MqttClient("tcp://broker.hivemq.com:1883", MqttClient.generateClientId(), null);
        mqttClient.connect();
        Intent intent =getIntent();
        mqttClient.subscribe(intent.getStringExtra("Topic"));
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                try{connectMqtt();timer.schedule(clearTask,0,timeValue);}catch(Exception e){Log.d("Error","MqttReConnect Error");}
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                JSONObject json = new JSONObject(new String(message.getPayload(), "UTF-8"));
                chatAdapter.add(new DTO_chat(json.getString("id"), json.getString("content")));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
