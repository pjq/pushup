package me.pjq.pushup;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * Created by kicoolzhang on 11/12/13.
 */

public class WifiNetworkHelper {
    private static final String TAG = "WifiNetworkHelper";

    WifiManager mWifiManager;
    WifiManager.WifiLock mWifiLock;

    WifiNetworkHelper(Context ctx) {
        mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        mWifiLock = mWifiManager.createWifiLock(
                android.os.Build.VERSION.SDK_INT >= 12
                        ? WifiManager.WIFI_MODE_FULL_HIGH_PERF
                        : WifiManager.WIFI_MODE_FULL, getClass().getName());

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        Log.i(TAG, "Own IP Address: " + wifiIpAddress(wifiInfo.getIpAddress()) + "Network SSID: " + wifiInfo.getSSID() + "Netword ID: " + wifiInfo.getNetworkId());
    }

    public WifiNetworkInfo getWifiInfo() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return new WifiNetworkInfo(wifiInfo);
    }

    protected static String wifiIpAddress(int ipAddress) {
        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e(TAG, "Unable to get host address.");
            ipAddressString = null;
        }

        Log.i(TAG, "Wifi IP:" + ipAddressString);
        return ipAddressString;
    }

    public void lock() {
        mWifiLock.acquire();
    }

    public void unlock() {
        mWifiLock.release();
    }

    public static class WifiNetworkInfo {
        WifiInfo wifiInfo;
        String wifiIpAddress;

        public WifiNetworkInfo(WifiInfo wifiInfo) {
            this.wifiInfo = wifiInfo;

            wifiIpAddress = wifiIpAddress(wifiInfo.getIpAddress());
        }

        public String getWifiIpAddress() {
            return wifiIpAddress;
        }

        public String getSSID() {
            return wifiInfo.getSSID();
        }

        public String getNetwordId() {
            return Integer.toString(wifiInfo.getNetworkId());
        }
    }
}
