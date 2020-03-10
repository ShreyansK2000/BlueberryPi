package com.cpen391m2.exercise5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.BluetoothInfoHolder> {

    private String cardType;
    private Context context;
    private List<BluetoothCardInfo> device_list;

    public BluetoothListAdapter(Context context, List<BluetoothCardInfo> device_list, String type) {
        this.context = context;
        this.cardType = type;
        this.device_list = device_list;
    }


    @NonNull
    @Override
    public BluetoothInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.device_info_view, parent, false);
        return new BluetoothInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BluetoothInfoHolder holder, int position) {
        final BluetoothCardInfo device_info = device_list.get(position);

        String buttonText = cardType.equals(context.getString(R.string.status_disconnected)) ?
                            context.getString(R.string.action_connect) : context.getString(R.string.action_disconnect);

        holder.device_name_tv.setText(device_info.getDeviceName());
        holder.device_addr_tv.setText(device_info.getAddr());
        holder.connectButton.setText(buttonText);

    }

    @Override
    public int getItemCount() {
        return device_list.size();
    }

    public class BluetoothInfoHolder extends RecyclerView.ViewHolder {

        private TextView device_name_tv;
        private TextView device_addr_tv;
        private Button connectButton;
        private View holderView;

        public BluetoothInfoHolder(@NonNull View itemView) {
            super(itemView);
            device_name_tv = itemView.findViewById(R.id.device_name);
            device_addr_tv = itemView.findViewById(R.id.device_addr);
            connectButton = itemView.findViewById(R.id.connect_button);
            holderView = itemView;

            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cardType.equals(context.getString(R.string.status_disconnected))) {
                        // discover/paired device connect button
                        Toast.makeText(context, "Disconnected device, attempting to connect", Toast.LENGTH_LONG).show();

                    } else if (cardType.equals(context.getString(R.string.status_connected))) {
                        // connected device disconnect button
                        Toast.makeText(context, "Connected device, disconnecting", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(context, "Unknown String type, check code", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
