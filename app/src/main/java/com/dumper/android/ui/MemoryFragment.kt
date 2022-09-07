package com.dumper.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.dumper.android.core.MainActivity
import com.dumper.android.databinding.FragmentMemoryBinding
import com.dumper.android.dumper.process.ProcessData
import com.dumper.android.ui.viewmodel.ConsoleViewModel
import com.dumper.android.ui.viewmodel.MemoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MemoryFragment : Fragment() {
    companion object {
        val instance by lazy { MemoryFragment() }
    }

    private val vm: MemoryViewModel by viewModels()
    private val consoles: ConsoleViewModel by activityViewModels()

    private lateinit var memBinding: FragmentMemoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        memBinding = FragmentMemoryBinding.inflate(inflater, container, false)
        return memBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        memBinding.apply {
            vm.selectedApps.observe(viewLifecycleOwner) {
                processText.editText?.setText(it)
            }

            dumpButton.setOnClickListener {
                val process = processText.editText!!.text.toString()
                if (process.isNotBlank()) {
                    consoles.appendLine("==========================\nProcess : $process")

                    val listDump = mutableListOf(libName.editText!!.text.toString())
                    if (metadata.isChecked)
                        listDump.add("global-metadata.dat")

                    getMainActivity().sendRequestDump(
                        process,
                        listDump.toTypedArray(),
                        autoFix.isChecked,
                        flagCheck.isChecked
                    )
                } else {
                    consoles.appendError("Process name is empty")
                }
            }

            selectApps.setOnClickListener {
                getMainActivity().sendRequestAllProcess()
            }
        }
    }

    fun showProcess(list: ArrayList<ProcessData>) {
        list.sortBy { lists -> lists.appName }

        val appNames = list.map { processData ->
            val processName = processData.processName
            if (processName.contains(":"))
                "${processData.appName} (${processName.substringAfter(":")})"
            else
                processData.appName
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select process")
            .setSingleChoiceItems(appNames.toTypedArray(), -1) { dialog, which ->
                vm.selectedApps.value = list[which].processName
                dialog.dismiss()
            }
            .show()

    }

    private fun getMainActivity() = requireActivity() as MainActivity
}