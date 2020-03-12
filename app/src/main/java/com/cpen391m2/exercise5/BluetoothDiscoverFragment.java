package com.cpen391m2.exercise5;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDiscoverFragment extends Fragment {

    private Context context;
    private List<BluetoothCardInfo> discovered_devices;
    private List<BluetoothDevice> device_list;
    private RecyclerView recyclerView;
    private BluetoothListAdapter adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private FloatingActionButton actionButton;
    private BluetoothDevice socketDevice;
    private BluetoothConnectionService service;


    /**
     * Discover intent filters broadcast receiver.
     */
    private BroadcastReceiver mBroadCastReceiverDiscover = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() == null) {
                    return;
                }
                device_list.add(device);

                BluetoothCardInfo card = new BluetoothCardInfo(device.getName(), device.getAddress());
                discovered_devices.add(card);

                adapter.notifyDataSetChanged();
            }// more visual feedback for user (not essential but useful)
            else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Toast.makeText(context, "Discovery Started", Toast.LENGTH_LONG).show();
            }
            else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) ) {
                Toast.makeText(context, "Discovery Finished", Toast.LENGTH_LONG).show();
            }

        }
    };

    private final BroadcastReceiver mBroadcastReceiverBond = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    System.out.println("BroadcastReceiver: BOND_BONDED.");
                    Toast.makeText(getActivity(),
                            "Bonding Successful. Device Paired. Please refresh paired devices",
                            Toast.LENGTH_SHORT).show();
                    if (mDevice.getName().equals("raspberrypi")) {
                        MainActivity.rpiDevice = mDevice;
                    }
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    System.out.println("BroadcastReceiver: BOND_BONDING.");
                    Toast.makeText(getActivity(),
                            "Bonding in progress. Please wait",
                            Toast.LENGTH_SHORT).show();
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    System.out.println("BroadcastReceiver: BOND_NONE.");
                    Toast.makeText(getActivity(),
                            "Bonding Unsuccessful. Device may already be paired, or unavailable",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        device_list = new ArrayList<>();
        discovered_devices = new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiverBond, filter);

        mBluetoothAdapter = MainActivity.getBluetoothAdapterInstance();
        discovered_devices.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_discovered_devices, container, false);

        actionButton = view.findViewById(R.id.search_devices);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        recyclerView = view.findViewById(R.id.device_list_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new BluetoothListAdapter(context, discovered_devices, context.getString(R.string.status_disconnected), this);
        recyclerView.setAdapter(adapter);

        return view;
    }


    public void search() {
        device_list.clear();
        discovered_devices.clear();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();

            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

            discoverDevicesFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
            discoverDevicesFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            discoverDevicesFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            getActivity().registerReceiver(mBroadCastReceiverDiscover, discoverDevicesFilter);
        } else {
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

            discoverDevicesFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            discoverDevicesFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            discoverDevicesFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
            getActivity().registerReceiver(mBroadCastReceiverDiscover, discoverDevicesFilter);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

            if (permissionCheck != 0) {
                getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        }
    }


    public boolean pairDevice(int i) {
        mBluetoothAdapter.cancelDiscovery();

        String deviceToBond = discovered_devices.get(i).getDeviceName() + " " + discovered_devices.get(i).getAddr();
        System.out.println("Trying to bond with: " + deviceToBond);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothDevice device = device_list.get(i);

            boolean result = false;
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                result = device_list.get(i).createBond();
                if (result) {
                    Toast.makeText(getActivity(), "Device bonded, refreshing discovery", Toast.LENGTH_SHORT);
                    search();
                }
            }

            return result;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadCastReceiverDiscover);
        getActivity().unregisterReceiver(mBroadcastReceiverBond);
    }

}
