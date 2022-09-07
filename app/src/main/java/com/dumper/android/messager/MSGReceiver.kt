package com.dumper.android.messager


import android.os.*
import android.widget.Toast
import com.dumper.android.core.MainActivity
import com.dumper.android.core.RootServices
import com.dumper.android.dumper.process.ProcessData
import com.dumper.android.ui.MemoryFragment

class MSGReceiver(private val activity: MainActivity) : Handler.Callback {
    override fun handleMessage(message: Message): Boolean {
        message.data.classLoader = activity.classLoader

        when (message.what) {
            RootServices.MSG_GET_PROCESS_LIST -> {
                message.data.getParcelableArrayList<ProcessData>(RootServices.LIST_ALL_PROCESS)
                    ?.let {
                        MemoryFragment.instance.showProcess(it)
                    }
            }
            RootServices.MSG_DUMP_PROCESS -> {
                message.data.getString(RootServices.DUMP_LOG)?.let {
                    activity.console.append(it)
                    activity.console.appendLine("==========================")
                    Toast.makeText(activity, "Dump Complete!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return false
    }
}