package com.quanticheart.bluetooth.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quanticheart.bluetooth.base.dialog.BaseFragmentDialog
import com.quanticheart.bluetooth.test.mapper.model.Device
import com.quanticheart.core.databinding.DialogHardwaresBinding
import com.quanticheart.core.databinding.ItemHardwareBinding

//
// Created by Jonn Alves on 22/01/23.
//
class DialogHardwares(
    private val mContext: Context,
    private val itemList: List<Device>,
    private val callback: (Device) -> Unit,
    private val callbackDelete: ((Device) -> Unit)? = null
) : BaseFragmentDialog<DialogHardwaresBinding>(mContext, DialogHardwaresBinding::inflate) {

    override fun view(binding: DialogHardwaresBinding) {
        val del = if (callbackDelete != null)
            { d: Device ->
                callbackDelete.invoke(d)
                dismiss()
            }
        else null

        val viewAdapter = BluetoothListAdapter(itemList, {
            callback(it)
            dismiss()
        }, del)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = viewAdapter
        }
    }
}

class BluetoothListAdapter(
    private var item: List<Device>,
    private val callback: (Device) -> Unit,
    private val callbackDelete: ((Device) -> Unit)? = null
) :
    RecyclerView.Adapter<BluetoothListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHardwareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = item.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(item[position])

    inner class ViewHolder(private val view: ItemHardwareBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(item: Device) {
            view.deviceName.text = item.name
            view.macAddress.text = item.macAddress
            view.root.setOnClickListener {
                callback(item)
            }
            callbackDelete?.let {
                view.delete.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        it(item)
                    }
                }
            }
        }
    }
}