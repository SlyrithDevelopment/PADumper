package com.dumper.android.core

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.*
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.dumper.android.R
import com.dumper.android.core.RootServices.Companion.IS_FIX_NAME
import com.dumper.android.core.RootServices.Companion.IS_FLAG_CHECK
import com.dumper.android.core.RootServices.Companion.LIBRARY_DIR_NAME
import com.dumper.android.core.RootServices.Companion.LIST_FILE
import com.dumper.android.core.RootServices.Companion.MSG_DUMP_PROCESS
import com.dumper.android.core.RootServices.Companion.MSG_GET_PROCESS_LIST
import com.dumper.android.core.RootServices.Companion.PROCESS_NAME
import com.dumper.android.databinding.ActivityMainBinding
import com.dumper.android.dumper.Fixer
import com.dumper.android.messager.MSGConnection
import com.dumper.android.messager.MSGReceiver
import com.dumper.android.ui.ConsoleFragment
import com.dumper.android.ui.MemoryFragment
import com.dumper.android.ui.viewmodel.ConsoleViewModel
import com.dumper.android.ui.viewmodel.MainViewModel
import com.topjohnwu.superuser.ipc.RootService

class MainActivity : AppCompatActivity() {
    private lateinit var mainBind: ActivityMainBinding
    val mainVm: MainViewModel by viewModels()
    val console: ConsoleViewModel by viewModels()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBind = ActivityMainBinding.inflate(layoutInflater)
        initService()

        with(mainBind) {
            setContentView(root)
            setSupportActionBar(toolbar)

            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.contentContainer, MemoryFragment.instance)
                    .commit()
            }


            bottomBar.setOnItemSelectedListener {
                supportFragmentManager.commit {
                    setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
                    replace(
                        R.id.contentContainer,
                        when (it.itemId) {
                            R.id.action_memory -> MemoryFragment.instance
                            R.id.action_console -> ConsoleFragment.instance
                            else -> throw IllegalArgumentException("Unknown item selected")
                        }, null
                    )
                }
                true
            }

            toolbar.setOnMenuItemClickListener {
                if (it.itemId == R.id.github) {
                    startActivity(
                        Intent(
                            ACTION_VIEW,
                            Uri.parse("https://github.com/BryanGIG/PADumper")
                        )
                    )
                }
                true
            }
        }
    }

    private fun initService() {
        Fixer.extractLibs(this)
        if (mainVm.remoteMessenger == null) {
            mainVm.dumperConnection = MSGConnection(this)
            val intent = Intent(this, RootServices::class.java)
            RootService.bind(intent, mainVm.dumperConnection)
            mainVm.receiver = Messenger(Looper.myLooper()?.let { Handler(it, MSGReceiver(this)) })
        }
    }

    fun sendRequestAllProcess() {
        val message = Message.obtain(null, MSG_GET_PROCESS_LIST)
        message.replyTo = mainVm.receiver
        mainVm.remoteMessenger?.send(message)
    }

    fun sendRequestDump(process: String, dump_file: Array<String>, autoFix: Boolean, flagCheck: Boolean) {
        val message = Message.obtain(null, MSG_DUMP_PROCESS)

        message.data.apply {
            putString(PROCESS_NAME, process)
            putStringArray(LIST_FILE, dump_file)
            putBoolean(IS_FLAG_CHECK, flagCheck)
            if (autoFix) {
                putBoolean(IS_FIX_NAME, true)
                putString(LIBRARY_DIR_NAME, "${filesDir.path}/SoFixer")
            }
        }

        message.replyTo = mainVm.receiver
        mainVm.remoteMessenger?.send(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        RootService.unbind(mainVm.dumperConnection)
    }
}