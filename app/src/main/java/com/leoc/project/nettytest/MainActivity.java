package com.leoc.project.nettytest;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button connect;
    private Button send;
    private EditText et;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connect=(Button)findViewById(R.id.connect);
        send=(Button)findViewById(R.id.send);
        et=(EditText)findViewById(R.id.et);
        initView();
        final int port=2222;
        Intent intent = new Intent(this, ServerService.class);
        startService(intent);
        connect.postDelayed(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        },1000);
    }

    private void initView() {
        connect.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connect:
                String host = "127.0.0.1";
                int port = 2222;
                connect();
                break;
            case R.id.send:
                if(!channel.isActive()){
                    connect();
                }
                if(!et.getText().toString().equals("")){
                    channel.writeAndFlush(et.getText().toString());
                }else{
                    Toast.makeText(this,"请输入要发送的内容",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void connect() {
        channel = new ImConnection().connect(new InetSocketAddress("127.0.0.1", 2222), new ImConnection.OnServerConnectListener() {
             @Override
             public void onConnectSuccess() {
                 Log.i("tag","success1");
             }

             @Override
             public void onConnectFailed() {
                 Log.i("tag","fail1");
             }
         });
    }

    @Override
    protected void onDestroy() {
        channel.disconnect();
        super.onDestroy();
    }
}
