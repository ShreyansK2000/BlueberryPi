package com.cpen391m2.exercise5;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BluetoothPairedFragment extends Fragment {// implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private List<BluetoothCardInfo> paired_devices;
    private List<BluetoothDevice> devices_list;
    private RecyclerView recyclerView;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothListAdapter adapter;
    private FloatingActionButton refreshButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = MainActivity.getBluetoothAdapterInstance();
        context = getActivity();
        paired_devices = new ArrayList<>();
        devices_list = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_paired_devices, container, false);

//        SwipeRefreshLayout swipeView = view.findViewById(R.id.devices_list);
//        swipeView.setOnRefreshListener(this);

        refreshButton = view.findViewById(R.id.refresh_paired_devices);
        recyclerView = view.findViewById(R.id.device_list_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new BluetoothListAdapter(context, paired_devices, context.getString(R.string.status_disconnected), this);
        recyclerView.setAdapter(adapter);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPairedDevicesList();
                for (BluetoothDevice bluetoothDevice : devices_list) {
                    if (bluetoothDevice.getName().equals("raspberrypi")) {
                        MainActivity.rpiDevice = bluetoothDevice;
                        break;
                    }
                }
            }
        });

        return view;
    }

    public boolean unPairDevice(int i) {
        mBluetoothAdapter.cancelDiscovery();

        BluetoothDevice deviceToBreakBond = devices_list.get(i);

        try {
            Method m = deviceToBreakBond.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(deviceToBreakBond, (Object[]) null);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public boolean connectDevice(int i) {
        mBluetoothAdapter.cancelDiscovery();

        String deviceToBond = paired_devices.get(i).getDeviceName() + " " + paired_devices.get(i).getAddr();
        System.out.println("Trying to bond with: " + deviceToBond);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return devices_list.get(i).createBond();
        }

        return false;
    }

    private void getPairedDevicesList() {
        paired_devices.clear();
        devices_list.clear();
        Set<BluetoothDevice> paired_set = mBluetoothAdapter.getBondedDevices();

        if (paired_set.size() > 0) {
            Iterator<BluetoothDevice> iter = paired_set.iterator() ;
            BluetoothDevice aNewdevice ;
            while (iter.hasNext()) {		// while at least one more device
                aNewdevice = iter.next(); 	         // get next element in set
                // Add the name and address to an array adapter to show in a ListView

                //add the new device details to the array
                devices_list.add(aNewdevice);
                paired_devices.add(new BluetoothCardInfo(aNewdevice.getName(), aNewdevice.getAddress()));
                adapter.notifyDataSetChanged ();	// update list on screen
            }
        }
    }

}
