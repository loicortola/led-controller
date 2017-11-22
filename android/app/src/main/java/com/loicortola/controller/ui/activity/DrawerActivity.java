package com.loicortola.controller.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.loicortola.controller.ui.fragment.RemoteControlFragment;
import com.loicortola.controller.R;
import com.loicortola.controller.ui.fragment.DeviceFragment;

public class DrawerActivity extends AppCompatActivity implements DeviceFragment.NavigationListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set main fragment
        getSupportFragmentManager().beginTransaction().add(R.id.content, new DeviceFragment()).commit();
    }

    @Override
    public void onDeviceSelected(String deviceId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, RemoteControlFragment.newInstance(deviceId));
        ft.addToBackStack(null);
        ft.commit();
    }
}
