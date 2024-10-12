package com.example.gasbillmanagements;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.gasbillmanagements.database.DatabaseHelper;
import com.example.gasbillmanagements.ultils.MusicPlayer;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.gasbillmanagements.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper myDB;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;


    //Music
    private MusicPlayer musicPlayerService;
    private boolean isBound = false;
    private static final String PREFS_NAME = "MusicSettings";
    private static final String PREF_MUSIC_ON = "music_on";
    //music




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

//// Thêm dữ liệu vào bảng gas_level_type
//        databaseHelper.insertGasLevelType("Level1", 1000, 50, 1.5f);
//        databaseHelper.insertGasLevelType("Level2", 2000, 100, 1.8f);
//
//// Thêm dữ liệu vào bảng customer
//        databaseHelper.insertCustomer("Ramesh", "202401", "Ahmedabad", 2000, 1);
//        databaseHelper.insertCustomer("Khilan", "202401", "Delhi", 1500, 2);
//        databaseHelper.insertCustomer("Kaushik", "202402", "Kota", 2000, 1);
//        databaseHelper.insertCustomer("Chaitali", "202402", "Mumbai", 6500, 2);
//        databaseHelper.insertCustomer("Hardik", "202403", "Bhopal", 8500, 2);
//        databaseHelper.insertCustomer("Komal", "202403", "MP", 4500, 1);


        Intent intent = new Intent(this, MusicPlayer.class);
        startService(intent);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.nav_search);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Cấu hình thanh điều hướng
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_task, R.id.nav_ListCustomer)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // Kiểm tra xem fragment hiện tại có phải là SettingsFragment không
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            if (navController.getCurrentDestination().getId() != R.id.nav_settings) {
                // Nếu không phải, điều hướng tới SettingsFragment
                try {
                    navController.navigate(R.id.nav_settings);
                } catch (Exception e) {
                    e.printStackTrace(); // Log lỗi nếu có vấn đề xảy ra
                }
            } else {
                // Nếu đã ở SettingsFragment, không làm gì hoặc thông báo
                Snackbar.make(binding.getRoot(), "You are already here", Snackbar.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    //Music
    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, MusicPlayer.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicPlayerService != null && getMusicSetting()) {
            musicPlayerService.playMusic(); // Tiếp tục phát nhạc nếu cài đặt đang bật
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (musicPlayerService != null) {
            musicPlayerService.stopMusic(); // Tạm dừng nhạc khi thoát ứng dụng
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, android.os.IBinder service) {
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

    private boolean getMusicSetting() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getBoolean(PREF_MUSIC_ON, true); // Mặc định bật nhạc
    }

    //Music






}
