package com.quanticheart.bluetooth.connecting

import kotlin.Throws
import android.bluetooth.BluetoothSocket
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class ManageConnectThread : Thread() {
    @Throws(IOException::class)
    fun sendData(socket: BluetoothSocket, data: Int) {
        val output = ByteArrayOutputStream(4)
        output.write(data)
        val outputStream = socket.outputStream
        outputStream.write(output.toByteArray())
    }

    @Throws(IOException::class)
    fun receiveData(socket: BluetoothSocket): Int {
        val buffer = ByteArray(4)
        val input = ByteArrayInputStream(buffer)
        val inputStream = socket.inputStream
        inputStream.read(buffer)
        return input.read()
    }
}