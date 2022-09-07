package com.dumper.android.ui.viewmodel

import android.os.Messenger
import androidx.lifecycle.ViewModel
import com.dumper.android.messager.MSGConnection

class MainViewModel : ViewModel() {

    var remoteMessenger: Messenger? = null
    lateinit var receiver : Messenger
    lateinit var dumperConnection : MSGConnection
}