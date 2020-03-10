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

public class BluetoothPairedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private List<BluetoothCardInfo> paired_devices = null;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        paired_devices = new ArrayList<>();
        getPairedDevices();
        paired_devices.add(new BluetoothCardInfo("Trent's phone", "AA:BB:CC"));
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

        BluetoothListAdapter adapter = new BluetoothListAdapter(context, paired_devices, context.getString(R.string.status_disconnected));
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void getPairedDevices() {
        paired_devices.clear();
    }

    @Override
    public void onRefresh() {

    }
}
