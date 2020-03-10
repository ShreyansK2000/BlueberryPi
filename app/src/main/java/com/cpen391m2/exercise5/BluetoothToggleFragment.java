package com.cpen391m2.exercise5;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BluetoothToggleFragment extends Fragment {

    private Context context;
    private TextView status_tv;
    private ImageView status_iv;
    private Button toggle_button;
    private String stateString;
    public int enabled;
    private BluetoothAdapter mBluetoothAdapter;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mBluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        enabled = 0;
                        stateString = "Bluetooth is off";
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                        enabled = 0;
                        stateString = "Bluetooth turning off";
                        break;

                    case BluetoothAdapter.STATE_ON:
                        enabled = 1;
                        stateString = "Bluetooth is on";
                        break;

                    case BluetoothAdapter.STATE_TURNING_ON:
                        enabled = 1;
                        stateString = "Bluetooth turning on";
                        break;

                    case BluetoothAdapter.STATE_CONNECTED:
                        enabled = 2;
                        stateString = "Connected to device";
                        break;

                    case BluetoothAdapter.STATE_CONNECTING:
                        enabled = 2;
                        stateString = "Connecting to device";
                        break;
                }
                setValues();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext().getApplicationContext();
        this.enabled = 0;
        this.mBluetoothAdapter = MainActivity.getBluetoothAdapterInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragement_bluetooth, container, false);

        status_tv = view.findViewById(R.id.bluetooth_status_text);
        status_tv.setTextColor(getResources().getColor(R.color.white));

        status_iv = view.findViewById(R.id.bluetooth_status_image);
        toggle_button = view.findViewById(R.id.bluetooth_toggle_button);

        toggle_button.setOnClickListener(toggleListener);

        setValues();

        return view;
    }


    private void setValues() {
        if (enabled == 0) {
            toggle_button.setText("ENABLE BLUETOOTH");
            status_iv.setImageDrawable(context.getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
        } else {
            toggle_button.setText("DISABLE BLUETOOTH");

            if (enabled == 1) {
                status_iv.setImageDrawable(context.getDrawable(R.drawable.ic_bluetooth_black_24dp));
            } else if (enabled == 2) {
                status_iv.setImageDrawable(context.getDrawable(R.drawable.ic_bluetooth_connected_black_24dp));
            }
        }

        status_tv.setText(stateString);
    }

    private Button.OnClickListener toggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (status_iv != null && status_tv != null) {
                if (mBluetoothAdapter == null) {
                    Toast.makeText(context, "Something went terribly wrong. Adapter should not be null", Toast.LENGTH_LONG).show();
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent BTEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(BTEnableIntent);

                    IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    getActivity().registerReceiver(mBroadcastReceiver, BTIntent);

                } else if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();

                    IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    getActivity().registerReceiver(mBroadcastReceiver, BTIntent);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
