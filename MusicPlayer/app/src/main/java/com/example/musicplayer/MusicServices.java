package com.example.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

public class MusicServices extends Service {
    //不同值表示不同状态0x11表示停止状态，0x12表示播放状态，0x13表示暂停状态
    int status=0x11;
    //current存放的是当前正在播放的歌曲对应的索引值
    int current=0;
    private MediaPlayer mediaPlayer=null;
    private AssetManager assetManager=null;
    private String []musics=new String []{"ordinary.mp3","star.mp3"};
    private ServiceReceiver serviceReceiver;
    public MusicServices() {
    }


    @Override
    public void onCreate() {
        mediaPlayer=new MediaPlayer();
        assetManager=getAssets();
        serviceReceiver=new ServiceReceiver();
        //动态注册serviceReceiver
        IntentFilter intentFilter=new IntentFilter(MainActivity.CONTROL);
        registerReceiver(serviceReceiver,intentFilter);

        super.onCreate();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                current++;
                if(current>=2){
                    current=0;
                }
                Intent intent=new Intent(MainActivity.UPDATE);
                intent.putExtra("current",current);
                sendBroadcast(intent);
                prepareAndPlay(musics[current]);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class ServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int control=intent.getIntExtra("control",-1);
            switch (control){
                case 1:
                    if (status==0x11){
                        prepareAndPlay(musics[current]);
                        status=0x12;
                    }else if(status==0x12){
                        mediaPlayer.pause();
                        status=0x13;
                    }else if(status==0x13){
                        mediaPlayer.start();
                        status=0x12;
                    }
                    break;
                case 2:
                    mediaPlayer.stop();
                    status=0x11;
                    break;
            }
            //发送普通广播
            Intent sendintent=new Intent(MainActivity.UPDATE);
            sendintent.putExtra("update",status);
            sendintent.putExtra("current",current);
            sendBroadcast(sendintent);
        }
    }

    private void prepareAndPlay(String music) {
        try {
            AssetFileDescriptor assetFileDescriptor=assetManager.openFd(music);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
