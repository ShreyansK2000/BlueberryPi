package com.blueberry.pi;

public class BluetoothCardInfo {

    private String device_name;
    private String addr;

    BluetoothCardInfo(String device_name, String addr) {
        this.device_name = (device_name == null) ? "NO_NAME" : device_name;
        this.addr = addr;
    }

    public String getAddr() {
        return addr;
    }

    public String getDeviceName() {
        return device_name;
    }
}
