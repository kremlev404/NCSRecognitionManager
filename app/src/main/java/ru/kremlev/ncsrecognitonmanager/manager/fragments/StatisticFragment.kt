package ru.kremlev.ncsrecognitonmanager.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.kremlev.ncsrecognitonmanager.databinding.FragmentStatisticBinding

class StatisticFragment : Fragment() {
    private var _binding: FragmentStatisticBinding? = null
    private val binding: FragmentStatisticBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}