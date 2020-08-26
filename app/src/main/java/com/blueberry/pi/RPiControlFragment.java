package com.blueberry.pi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.nio.charset.Charset;
import java.util.UUID;

public class RPiControlFragment extends Fragment {

    private BluetoothAdapter mBluetoothAdapter;
    private Button connect_button;
    private Button send_button;
    private EditText pi_input_field;
    private BluetoothConnectionService connectionService;

    public static BluetoothDevice mmdevice;

    public static void setDevice(BluetoothDevice device) {
        if (mmdevice == null) {
            mmdevice = null;
        }
    }

    private static final String appName = "MYAPP";

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    BluetoothDevice mBluetoothDevice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionService = MainActivity.getConnectionService(getActivity());
        if (connectionService.getState() == BluetoothConnectionService.STATE_NONE) {
            connectionService.start();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragement_rpi, container, false);

        mBluetoothAdapter = MainActivity.getBluetoothAdapterInstance();

        pi_input_field = view.findViewById(R.id.pi_input_field);
        send_button = view.findViewById(R.id.send_input_button);
        connect_button = view.findViewById(R.id.start_connection_button);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes = pi_input_field.getText().toString().getBytes(Charset.defaultCharset());
                connectionService.write(bytes);
            }
        });

        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.rpiDevice == null) {
                    Toast.makeText(getActivity(), "Not bonded to rpi\nTry again after bonding", Toast.LENGTH_SHORT).show();
                } else {
                    connectionService.connect(MainActivity.rpiDevice);
                }

                long time = System.currentTimeMillis();
                while (connectionService.getState() == BluetoothConnectionService.STATE_CONNECTING) {
                    if (System.currentTimeMillis() - time >= 2000) {
                        Toast.makeText(getActivity(), "Connection timed out, try again", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                if (connectionService.getState() == BluetoothConnectionService.STATE_CONNECTED) {
                    Toast.makeText(getActivity(), "Connection successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

//    public void startConnection() {
//        startBTConnection(mmdevice, MY_UUID_INSECURE);
//    }
//
//    public void startBTConnection(BluetoothDevice device, UUID uuid) {
//        connectionService.startClient(device, uuid);
//    }


}
