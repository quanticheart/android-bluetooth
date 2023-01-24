package com.quanticheart.bluetooth.connecting

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

class ServerConnectThread : Thread() {
    private var bTSocket: BluetoothSocket? = null

    @SuppressLint("MissingPermission")
    fun acceptConnect(bTAdapter: BluetoothAdapter, mUUID: UUID?) {
        var temp: BluetoothServerSocket? = null
        try {
            temp = bTAdapter.listenUsingRfcommWithServiceRecord("Service_Name", mUUID)
        } catch (e: IOException) {
            Log.d("SERVERCONNECT", "Could not get a BluetoothServerSocket:$e")
        }
        while (true) {
            bTSocket = try {
                temp!!.accept()
            } catch (e: IOException) {
                Log.d("SERVERCONNECT", "Could not accept an incoming connection.")
                break
            }
            if (bTSocket != null) {
                try {
                    temp.close()
                } catch (e: IOException) {
                    Log.d("SERVERCONNECT", "Could not close ServerSocket:$e")
                }
                break
            }
        }
    }

    fun closeConnect() {
        try {
            bTSocket!!.close()
        } catch (e: IOException) {
            Log.d("SERVERCONNECT", "Could not close connection:$e")
        }
    }
}