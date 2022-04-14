package ru.kremlev.ncsrecognitonmanager.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

import ru.kremlev.ncsrecognitonmanager.databinding.FragmentManagerBinding
import ru.kremlev.ncsrecognitonmanager.manager.adapters.RecyclerViewManagerAdapter
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemData
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemType
import ru.kremlev.ncsrecognitonmanager.manager.viewmodels.Navigation
import ru.kremlev.ncsrecognitonmanager.manager.viewmodels.RecognitionSystemViewModel
import ru.kremlev.ncsrecognitonmanager.utils.LogManager
import ru.kremlev.ncsrecognitonmanager.utils.log

class ManagerFragment : Fragment() {
    private var _binding: FragmentManagerBinding? = null
    private val binding: FragmentManagerBinding
        get() = _binding!!

    private val model: RecognitionSystemViewModel by activityViewModels()

    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentManagerBinding.inflate(inflater, container, false)
        val view = binding.root
        try {
            binding.tvManagerLogin.text = FirebaseAuth.getInstance().currentUser!!.email?.substringBeforeLast("@")
        } catch (e: Exception) {
            LogManager.e("", e)
        }

        val adapter = RecyclerViewManagerAdapter(requireContext())
        layoutManager = LinearLayoutManager(requireContext())

        binding.apply {
            recyclerRaspberryListManager.layoutManager = layoutManager

            recyclerRaspberryListManager.adapter = adapter

            val dividerItemDecoration = DividerItemDecoration(
                recyclerRaspberryListManager.context,
                RecyclerView.VERTICAL
            )
            recyclerRaspberryListManager.addItemDecoration(dividerItemDecoration)
        }

        model.recognitionSystemData.observe(viewLifecycleOwner, Observer<ArrayList<RecognitionSystemData>> { list ->
            LogManager.d()
            list.log()
            adapter.setData(list)
            Navigation.selectedSystem.value = -1
        })

        model.recognitionSystemData.value = arrayListOf<RecognitionSystemData>(
            RecognitionSystemData("1", RecognitionSystemType.RASPBERRY),
            RecognitionSystemData("2", RecognitionSystemType.RASPBERRY),
            RecognitionSystemData("3", RecognitionSystemType.X86),
            RecognitionSystemData("44", RecognitionSystemType.RASPBERRY)
        )

        CoroutineScope(Dispatchers.Default).launch {
            delay(5000)
            withContext(Dispatchers.Main) {
                model.recognitionSystemData.value = arrayListOf<RecognitionSystemData>(
                    RecognitionSystemData("412", RecognitionSystemType.X86),
                    RecognitionSystemData("212", RecognitionSystemType.RASPBERRY),
                    RecognitionSystemData("-244", RecognitionSystemType.RASPBERRY)
                )
            }
            delay(5000)
            withContext(Dispatchers.Main) {
                model.recognitionSystemData.value = arrayListOf<RecognitionSystemData>(
                    RecognitionSystemData("274", RecognitionSystemType.X86),
                    RecognitionSystemData("333", RecognitionSystemType.RASPBERRY),
                    RecognitionSystemData("444", RecognitionSystemType.X86),
                    RecognitionSystemData("--arb", RecognitionSystemType.RASPBERRY),
                    RecognitionSystemData("--s2w", RecognitionSystemType.RASPBERRY)
                )
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}