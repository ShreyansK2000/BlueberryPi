package com.blueberry.pi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private BluetoothToggleFragment toggleFragment;
    private DevicesFragment devicesFragment;
    private RPiControlFragment rPiControlFragment;
    private Fragment current;

    protected static BluetoothAdapter staticBluetoothAdapter;
    private BluetoothAdapter mBlueToothAdapter;

    public static BluetoothConnectionService connectionService;
    public static BluetoothDevice rpiDevice;

    public static BluetoothAdapter getBluetoothAdapterInstance() {
        if (staticBluetoothAdapter == null) {
            staticBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            return staticBluetoothAdapter;
        }
        else return staticBluetoothAdapter;
    }

    public static BluetoothConnectionService getConnectionService(Context context) {
        if (connectionService == null) {
            connectionService = new BluetoothConnectionService(context.getApplicationContext());
            return connectionService;
        }
        else return connectionService;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleFragment = new BluetoothToggleFragment();
        devicesFragment = new DevicesFragment();
        rPiControlFragment = new RPiControlFragment();

        connectionService = getConnectionService(this);
        if (connectionService.getState() == BluetoothConnectionService.STATE_NONE) {
            connectionService.start();
        }

        FragmentManager fm = getSupportFragmentManager();

        rpiDevice = null;

        // set the default symbol
        mBlueToothAdapter = getBluetoothAdapterInstance();
        if (!mBlueToothAdapter.isEnabled()) {
            toggleFragment.enabled = 0;
        } else {
            toggleFragment.enabled = 1;
        }

        connectionService = getConnectionService(this);
        if (connectionService.getState() == BluetoothConnectionService.STATE_NONE) {
            connectionService.start();
        }

        fm.beginTransaction().add(R.id.fragment_container, devicesFragment, "devices").hide(devicesFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, rPiControlFragment, "rpi").hide(rPiControlFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, toggleFragment, "toggle").commit();

        current = toggleFragment;

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(listener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected;

                if (current == null) {
                    toggleFragment = new BluetoothToggleFragment();
                    current = toggleFragment;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            toggleFragment);
                }

                switch (item.getItemId()) {
                    case R.id.bluetooth_tab:
                        if (toggleFragment == null) selected = new BluetoothToggleFragment();
                        else selected = toggleFragment;

                        getSupportFragmentManager().beginTransaction().hide(current).show(selected).commit();
                        current = selected;
                        break;

                    case R.id.devices_tab:
                        if (devicesFragment == null) selected = new DevicesFragment();
                        else selected = devicesFragment;

                        getSupportFragmentManager().beginTransaction().hide(current).show(selected).commit();
                        current = selected;
                        break;

                    case R.id.rpi_tab:
                        if (rPiControlFragment == null) selected = new RPiControlFragment();
                        else selected = rPiControlFragment;

                        getSupportFragmentManager().beginTransaction().hide(current).show(selected).commit();
                        current = selected;
                        break;

                    /*
                     * By default, i.e. at the start of the activity, select the complex logic/
                     * pick event fragment
                     */
                    default:
                        if (toggleFragment == null) selected = new BluetoothToggleFragment();
                        else selected = toggleFragment;

                        getSupportFragmentManager().beginTransaction().hide(current).show(selected).commit();
                        current = selected;
                        break;
                }

                return true;
        }
    };
}
