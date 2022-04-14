package ru.kremlev.ncsrecognitonmanager.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import ru.kremlev.ncsrecognitonmanager.databinding.FragmentStatisticBinding
import ru.kremlev.ncsrecognitonmanager.manager.viewmodels.RecognitionSystemViewModel
import ru.kremlev.ncsrecognitonmanager.utils.LogManager

class StatisticFragment : Fragment() {
    private var _binding: FragmentStatisticBinding? = null
    private val binding: FragmentStatisticBinding
        get() = _binding!!

    private val model: RecognitionSystemViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val view = binding.root

        model.getSelectedSystem().observe(viewLifecycleOwner) { it ->
            LogManager.d()
            binding.tvId.text = if (it > -1)
                (model.recognitionSystemData.value?.get(it)?.id ?: "No selected").toString()
            else
                "Please Select System At Manager Page"
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}