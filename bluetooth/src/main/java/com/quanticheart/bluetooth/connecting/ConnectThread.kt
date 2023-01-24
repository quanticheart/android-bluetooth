package com.quanticheart.bluetooth.connecting

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothDevice
import android.util.Log
import java.io.IOException
import java.util.*

class ConnectThread : Thread() {
    private val bTSocket: BluetoothSocket? = null

    @SuppressLint("MissingPermission")
    fun connect(bTDevice: BluetoothDevice, mUUID: UUID?): Boolean {
        var temp: BluetoothSocket? = null
        temp = try {
            bTDevice.createRfcommSocketToServiceRecord(mUUID)
        } catch (e: IOException) {
            Log.d("CONNECTTHREAD", "Could not create RFCOMM socket:$e")
            return false
        }
        try {
            bTSocket?.connect()
        } catch (e: IOException) {
            Log.d("CONNECTTHREAD", "Could not connect: $e")
            try {
                bTSocket?.close()
            } catch (close: IOException) {
                Log.d("CONNECTTHREAD", "Could not close connection:$e")
                return false
            }
        }
        return true
    }

    fun cancel(): Boolean {
        try {
            bTSocket?.close()
        } catch (e: IOException) {
            Log.d("CONNECTTHREAD", "Could not close connection:$e")
            return false
        }
        return true
    }
}

@SuppressLint("MissingPermission")
class ConnectThread2(bTDevice: BluetoothDevice, UUID: UUID?) : Thread() {
    private val bTDevice: BluetoothDevice
    private val bTSocket: BluetoothSocket?

    init {
        var tmp: BluetoothSocket? = null
        this.bTDevice = bTDevice
        try {
            tmp = this.bTDevice.createRfcommSocketToServiceRecord(UUID)
        } catch (e: IOException) {
            Log.d("CONNECTTHREAD", "Could not start listening for RFCOMM")
        }
        bTSocket = tmp
    }

    fun connect(): Boolean {
        try {
            bTSocket?.connect()
        } catch (e: IOException) {
            Log.d("CONNECTTHREAD", "Could not connect: $e")
            try {
                bTSocket?.close()
            } catch (close: IOException) {
                Log.d("CONNECTTHREAD", "Could not close connection:$e")
                return false
            }
        }
        return true
    }

    fun cancel(): Boolean {
        try {
            bTSocket?.close()
        } catch (e: IOException) {
            return false
        }
        return true
    }
}