package com.quanticheart.bluetooth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quanticheart.bluetooth.databinding.ActivityMainBinding
import com.quanticheart.bluetooth.dialog.DialogHardwares
import com.quanticheart.bluetooth.listeners.BluetoothListener
import com.quanticheart.bluetooth.mapper.model.Device

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val bluetooth: BluetoothManagement by lazy {
        BluetoothManagement(this, object : BluetoothListener {
            override fun onStartDiscovery() {
                binding.msg.text = "Start discovery"
            }

            override fun onFinishDiscovery() {
                binding.msg.text = "Stop discovery"
            }

            override fun onEnabledBluetooth() {
                binding.status.text = "ON"
                binding.on.isEnabled = false
                binding.off.isEnabled = true
            }

            override fun onDisabledBluetooth() {
                binding.status.text = "OFF"
                binding.on.isEnabled = true
                binding.off.isEnabled = false
            }

            override fun permissionsStatus(status: Boolean) {
                binding.statusP.text = if (status) "OK" else "REQUIRED"
            }

            @SuppressLint("MissingPermission")
            override fun getBluetoothDeviceDiscoveryList(devices: List<Device>) {
                DialogHardwares(this@MainActivity, devices, callback = {
                    it.log()
                }).show()
            }

            @SuppressLint("MissingPermission")
            override fun getBluetoothDeviceList(devices: List<Device>) {
                DialogHardwares(this@MainActivity, devices,
                    callback = {
                        it.log()
                    },
                    callbackDelete = {
                        bluetooth.unpairDevice(it.bluetoothDevice)
                    }
                ).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.off.setOnClickListener { bluetooth.disable() }
        binding.on.setOnClickListener { bluetooth.enable() }

        binding.picker.setOnClickListener { bluetooth.openPicker() }
        binding.settings.setOnClickListener { bluetooth.openSettings() }
        binding.show.setOnClickListener {
            bluetooth.getHardware()
        }
        binding.visible.setOnClickListener { bluetooth.visibleForDiscovery() }
        binding.search.setOnClickListener { bluetooth.startDiscovery() }
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        bluetooth.startActivityForResult(intent, requestCode, options)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        bluetooth.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        bluetooth.registerBluetoothStateChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetooth.unregisterBluetoothStateChanged()
    }
}