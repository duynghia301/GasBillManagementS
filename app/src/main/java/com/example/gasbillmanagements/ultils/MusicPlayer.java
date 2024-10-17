package com.example.gasbillmanagements.ultils;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.gasbillmanagements.R;

public class MusicPlayer extends Service {
    private final IBinder binder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private int currentPosition = 0; // Biến lưu vị trí phát hiện tại

    public class LocalBinder extends Binder {
        public MusicPlayer getService() {
            return MusicPlayer.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.faded);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void playMusic() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(currentPosition); // Thiết lập lại vị trí phát
                mediaPlayer.start();
            }
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition(); // Lưu vị trí phát hiện tại
            mediaPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
