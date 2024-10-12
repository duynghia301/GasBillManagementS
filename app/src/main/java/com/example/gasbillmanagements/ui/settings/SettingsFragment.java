package com.example.gasbillmanagements.ui.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.ultils.MusicPlayer;

public class SettingsFragment extends Fragment {
    private Switch switchPlayMusic;
    private Switch switchShowAddress;
    private Switch switchShowUsedNumGas;
    private Switch switchShowGasLevel;
    private Switch switchShowPrice;

    private MusicPlayer musicPlayerService;
    private boolean isBound = false;

    private static final String PREFS_NAME = "MusicSettings";
    private static final String PREF_MUSIC_ON = "music_on";

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Xử lý các tham số nếu cần
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        switchPlayMusic = view.findViewById(R.id.switch_play_music);

        // Lấy cài đặt âm nhạc và cập nhật trạng thái của switch
        switchPlayMusic.setChecked(getMusicSetting());

        switchPlayMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (musicPlayerService != null) {
                    musicPlayerService.playMusic(); // Bắt đầu phát nhạc
                }
            } else {
                if (musicPlayerService != null) {
                    musicPlayerService.stopMusic();
                }
            }
            saveMusicSetting(isChecked); // Lưu cài đặt
        });

        return view;
    }

    private void saveMusicSetting(boolean isOn) {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_MUSIC_ON, isOn);
        editor.apply();
    }

    private boolean getMusicSetting() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_MUSIC_ON, true); // Mặc định bật nhạc
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(getActivity(), MusicPlayer.class);
        requireActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            requireActivity().unbindService(connection);
            isBound = false;
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayer.LocalBinder binder = (MusicPlayer.LocalBinder) service;
            musicPlayerService = binder.getService();
            isBound = true;

            // Tiếp tục phát nhạc nếu cài đặt đang bật
            if (getMusicSetting()) {
                musicPlayerService.playMusic();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicPlayerService = null;
            isBound = false;
        }
    };
}
