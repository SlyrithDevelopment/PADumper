package com.dumper.android.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dumper.android.dumper.process.ProcessData

class MemoryViewModel: ViewModel() {

    val allApps by lazy {
        MutableLiveData<List<ProcessData>>()
    }

    val selectedApps by lazy { MutableLiveData<String>() }
}