package com.blueberry.pi;

import android.bluetooth.BluetoothAdapter;
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

import java.util.Objects;

public class BluetoothToggleFragment extends Fragment {

    private Context context;
    private ImageView status_iv;
    private TextView bluetooth_status_tv;
    private Button toggle_button;

    private TextView discoverable_status_tv;
    private Button discoverable_button;

    private String stateString;
    private String modeString;

    public int enabled;
    private BluetoothAdapter mBluetoothAdapter;

    private final BroadcastReceiver mBroadcastReceiverState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

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
                        stateString = "Connecting to device...";
                        break;
                }
                setValues();
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiverMode = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        modeString = "Device is not discoverable, can receive connections";
                        break;

                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        modeString = "Device is discoverable, can receive connections";
                        break;

                    case BluetoothAdapter.SCAN_MODE_NONE:
                        modeString = "Not discoverable, cannot receive connection";
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
        this.mBluetoothAdapter = MainActivity.getBluetoothAdapterInstance();
        this.stateString = "";
        this.modeString = "";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragement_bluetooth, container, false);

        bluetooth_status_tv = view.findViewById(R.id.bluetooth_status_text);
        bluetooth_status_tv.setTextColor(getResources().getColor(R.color.white));

        status_iv = view.findViewById(R.id.bluetooth_status_image);
        toggle_button = view.findViewById(R.id.bluetooth_toggle_button);
        discoverable_status_tv = view.findViewById(R.id.discoverable_status_text);
        discoverable_button = view.findViewById(R.id.discoverable_button);

        toggle_button.setOnClickListener(toggleListener);
        discoverable_button.setOnClickListener(discoverableListener);

        setValues();

        return view;
    }


    void setValues() {
        if (enabled == 0) {
            toggle_button.setText("ENABLE BLUETOOTH");
            status_iv.setImageDrawable(context.getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
            discoverable_status_tv.setVisibility(View.GONE);
            discoverable_button.setVisibility(View.GONE);

            System.out.println(bluetooth_status_tv.getText());

            if (stateString.equals("")){
                bluetooth_status_tv.setText("Bluetooth is off");
            } else {
                bluetooth_status_tv.setText(stateString);
            }

        } else {
            toggle_button.setText("DISABLE BLUETOOTH");

            discoverable_status_tv.setVisibility(View.VISIBLE);
            discoverable_status_tv.setText(modeString);
            discoverable_button.setVisibility(View.VISIBLE);
            discoverable_button.setText("MAKE DISCOVERABLE");

            if (enabled == 1) {
                status_iv.setImageDrawable(context.getDrawable(R.drawable.ic_bluetooth_black_24dp));
            } else if (enabled == 2) {
                status_iv.setImageDrawable(context.getDrawable(R.drawable.ic_bluetooth_connected_black_24dp));
            }

            if (stateString.equals("")){
                bluetooth_status_tv.setText("Bluetooth is on");
            } else {
                bluetooth_status_tv.setText(stateString);
            }
        }


    }

    private Button.OnClickListener toggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (status_iv != null && bluetooth_status_tv != null) {
                if (mBluetoothAdapter == null) {
                    Toast.makeText(context, "Something went terribly wrong. Adapter should not be null", Toast.LENGTH_LONG).show();
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent BTEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(BTEnableIntent);

                    IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    Objects.requireNonNull(getActivity()).registerReceiver(mBroadcastReceiverState, BTIntent);

                } else {
                    mBluetoothAdapter.disable();

                    IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    Objects.requireNonNull(getActivity()).registerReceiver(mBroadcastReceiverState, BTIntent);
                }
            }
        }
    };

    private Button.OnClickListener discoverableListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            IntentFilter modeFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            Objects.requireNonNull(getActivity()).registerReceiver(mBroadcastReceiverMode, modeFilter);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Objects.requireNonNull(getActivity()).unregisterReceiver(mBroadcastReceiverState);
        getActivity().unregisterReceiver(mBroadcastReceiverMode);
    }
}
