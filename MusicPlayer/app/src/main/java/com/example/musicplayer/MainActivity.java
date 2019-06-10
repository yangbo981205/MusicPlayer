package com.example.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageButton stopBtn=null;
    private ImageButton playBtn=null;
    private TextView songName=null;
    private TextView author=null;

    private MyListener myListener=null;

    public static final String CONTROL="com.example.musicplayer.MainActivity.control";
    public static final String UPDATE="com.example.musicplayer.MainActivity.update";

    //不同值表示不同状态0x11表示停止状态，0x12表示播放状态，0x13表示暂停状态
    private int status=0x11;
    private int current=0;

    String [] songNames=new String []{"平凡之路","夜空中最亮的星"};
    String [] authors=new String []{"朴树","逃跑计划"};

    private ActivityReceiver activityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopBtn=(ImageButton)findViewById(R.id.stopBtn);
        playBtn=(ImageButton)findViewById(R.id.playBtn);
        songName=(TextView)findViewById(R.id.songName);
        author=(TextView)findViewById(R.id.author);

        myListener=new MyListener();

        stopBtn.setOnClickListener(myListener);
        playBtn.setOnClickListener(myListener);

        activityReceiver=new ActivityReceiver();
        IntentFilter intentFilter=new IntentFilter(UPDATE);
        registerReceiver(activityReceiver,intentFilter);

        Intent intent=new Intent(MainActivity.this,MusicServices.class);
        startService(intent);
    }

    private class MyListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(CONTROL);
            //1表示播放或暂停，2表示停止
            switch(v.getId()){
                case R.id.playBtn:
                    intent.putExtra("control",1);
                    break;
                case R.id.stopBtn:
                    intent.putExtra("control",2);
                    break;
            }
            //发送普通广播
            sendBroadcast(intent);
        }
    }

    private class ActivityReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            status=intent.getIntExtra("update",-1);
            current=intent.getIntExtra("current",-1);
            if(current>=0){
                songName.setText(songNames[current]);
                author.setText(authors[current]);
            }
            switch (status){
                case 0x11:
                    playBtn.setImageResource(R.drawable.play);
                    status = 0x11;
                    break;
                case 0x12:
                    playBtn.setImageResource(R.drawable.pause);
                    status = 0x12;
                    break;
                case 0x13:
                    playBtn.setImageResource(R.drawable.play);
                    status = 0x13;
                    break;
            }
        }
    }
}
