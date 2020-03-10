package com.cpen391m2.exercise5;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDiscoverFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private List<BluetoothCardInfo> discovered_devices = null;
    private RecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        discovered_devices = new ArrayList<>();
        getDiscoveredDevices();
        discovered_devices.add(new BluetoothCardInfo("Shreyans' phone", "AA:BB:CC"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_discovered_devices, container, false);

        SwipeRefreshLayout swipeView = view.findViewById(R.id.devices_list);
        swipeView.setOnRefreshListener(this);

        recyclerView = view.findViewById(R.id.device_list_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        BluetoothListAdapter adapter = new BluetoothListAdapter(context, discovered_devices, context.getString(R.string.status_disconnected));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onRefresh() {

    }

    public void getDiscoveredDevices() {
        discovered_devices.clear();
        // Do stuff to get discovered devices
    }
}
