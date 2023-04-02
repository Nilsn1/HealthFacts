package com.nilscreation.healthfacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    SwitchCompat switchMode;
    networkChangListener networkChangListener = new networkChangListener();
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottonNavigationView);
        switchMode = findViewById(R.id.switchMode);

        loadFragment(new MainFragment());

        nightMode();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.home) {
                    loadFragment(new MainFragment());
                } else if (id == R.id.settings) {
                    loadFragment(new SettingsFragment());
                } else if (id == R.id.favourite) {
                    loadFragment(new FavouriteFragment());
                } else {
                    loadFragment(new CategorySearchFragment.CategoryFragment());
                }
                return true;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainContainer, fragment);
        ft.commit();
    }

    @Override
    protected void onResume() {
        nightMode();
        super.onResume();
    }

    private void nightMode() {
        //NIGHT MODE
        // Saving state of our app using SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        // When user reopens the app after applying dark/light mode
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchMode.setChecked(true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            switchMode.setChecked(false);
        }

        switchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchMode.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    // it will set isDarkModeOn
                    // boolean to false
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragInstance = fm.findFragmentById(R.id.mainContainer);

        if (fragInstance instanceof CategorySearchFragment.CategoryFragment) {
            loadFragment(new MainFragment());
            bottomNavigation.setSelectedItemId(R.id.home);
        } else if (fragInstance instanceof FavouriteFragment) {
            loadFragment(new MainFragment());
            bottomNavigation.setSelectedItemId(R.id.home);
        } else if (fragInstance instanceof SettingsFragment) {
            loadFragment(new MainFragment());
            bottomNavigation.setSelectedItemId(R.id.home);
        } else if (fragInstance instanceof CategorySearchFragment) {
            loadFragment(new CategorySearchFragment.CategoryFragment());
        } else {
            callExitDialog();
        }
    }

    private void callExitDialog() {

        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.exit_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView btnCancel, btnYes, btnNO;
        btnCancel = dialog.findViewById(R.id.btn_Cancel);
        btnYes = dialog.findViewById(R.id.btn_Yes);
        btnNO = dialog.findViewById(R.id.btn_No);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialog.show();

    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangListener);
        super.onStop();
    }

    private boolean isConnected(HomeActivity homeActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) homeActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    public class networkChangListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //Internet connection
            if (!isConnected(HomeActivity.this)) {

                Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.internet_dialog);
                dialog.setCancelable(false);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                Button btnOk = dialog.findViewById(R.id.btn_retry);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        onReceive(HomeActivity.this, intent);
                        loadFragment(new MainFragment());
                    }
                });
                dialog.show();
            }

        }
    }
}