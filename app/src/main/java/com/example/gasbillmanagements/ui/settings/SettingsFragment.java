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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.ultils.MusicPlayer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SettingsFragment extends Fragment {
    private Switch switchPlayMusic;
    private OnSettingsChangeListener listener;
    private Switch switchAddress, switchUsedNumGas, switchGasLevel, switchPrice;

    private MusicPlayer musicPlayerService;
    private boolean isBound = false;

    private static final String PREFS_NAME = "MusicSettings";
    private static final String PREF_MUSIC_ON = "music_on";
    private static final String PREF_SWITCH_ADDRESS = "switch_address";
    private static final String PREF_SWITCH_USED_NUM_GAS = "switch_used_num_gas";
    private static final String PREF_SWITCH_GAS_LEVEL = "switch_gas_level";
    private static final String PREF_SWITCH_PRICE = "switch_price";

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsChangeListener) {
            listener = (OnSettingsChangeListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        switchPlayMusic = view.findViewById(R.id.switch_play_music);
        switchAddress = view.findViewById(R.id.switch_show_address);
        switchUsedNumGas = view.findViewById(R.id.switch_show_used_num_gas);
        switchGasLevel = view.findViewById(R.id.switch_show_gas_level);
        switchPrice = view.findViewById(R.id.switch_show_price);

        // Khôi phục trạng thái của các switch từ SharedPreferences
        switchAddress.setChecked(getSwitchState(PREF_SWITCH_ADDRESS, true));
        switchUsedNumGas.setChecked(getSwitchState(PREF_SWITCH_USED_NUM_GAS, true));
        switchGasLevel.setChecked(getSwitchState(PREF_SWITCH_GAS_LEVEL, true));
        switchPrice.setChecked(getSwitchState(PREF_SWITCH_PRICE, true));

        // Thiết lập listener cho các switch
        setupSwitchListeners();

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
            saveMusicSetting(isChecked); // Lưu cài đặt âm nhạc
        });

        return view;
    }

    private void setupSwitchListeners() {
        switchAddress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(PREF_SWITCH_ADDRESS, isChecked);
            notifySettingsChanged();
        });

        switchUsedNumGas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(PREF_SWITCH_USED_NUM_GAS, isChecked);
            notifySettingsChanged();
        });

        switchGasLevel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(PREF_SWITCH_GAS_LEVEL, isChecked);
            notifySettingsChanged();
        });

        switchPrice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(PREF_SWITCH_PRICE, isChecked);
            notifySettingsChanged();
        });
    }

    private void notifySettingsChanged() {
        if (listener != null) {
            listener.onSettingsChanged(
                    switchAddress.isChecked(),
                    switchUsedNumGas.isChecked(),
                    switchGasLevel.isChecked(),
                    switchPrice.isChecked()
            );
        }
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

    private void saveSwitchState(String key, boolean isChecked) {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    private boolean getSwitchState(String key, boolean defaultValue) {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
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

    @Override
    public void onPause() {
        super.onPause();
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
    }
}
