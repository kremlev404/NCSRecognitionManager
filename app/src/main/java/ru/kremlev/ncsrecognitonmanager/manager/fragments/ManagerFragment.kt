package ru.kremlev.ncsrecognitonmanager.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseAuth
import ru.kremlev.ncsrecognitonmanager.R

import ru.kremlev.ncsrecognitonmanager.databinding.FragmentManagerBinding
import ru.kremlev.ncsrecognitonmanager.manager.adapters.RecyclerViewManagerAdapter
import ru.kremlev.ncsrecognitonmanager.manager.model.Navigation
import ru.kremlev.ncsrecognitonmanager.manager.viewmodels.RecognitionSystemViewModel
import ru.kremlev.ncsrecognitonmanager.utils.LogManager
import ru.kremlev.ncsrecognitonmanager.utils.log

class ManagerFragment : Fragment() {
    private var _binding: FragmentManagerBinding? = null
    private val binding: FragmentManagerBinding
        get() = _binding!!

    private val model: RecognitionSystemViewModel by activityViewModels()

    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagerBinding.inflate(inflater, container, false)
        val view = binding.root
        try {
            binding.tvManagerLogin.text =
                FirebaseAuth.getInstance().currentUser!!.email?.substringBeforeLast("@")
        } catch (e: Exception) {
            LogManager.e("", e)
        }

        val adapter = RecyclerViewManagerAdapter(requireContext())
        layoutManager = LinearLayoutManager(requireContext())

        binding.apply {
            recyclerSystemsIdListManager.layoutManager = layoutManager

            recyclerSystemsIdListManager.adapter = adapter

            val dividerItemDecoration = DividerItemDecoration(
                recyclerSystemsIdListManager.context,
                RecyclerView.VERTICAL
            )
            recyclerSystemsIdListManager.addItemDecoration(dividerItemDecoration)
        }

        var lastSize = -1
        model.systemList.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty())
                binding.horizontalProgressbar.visibility = View.VISIBLE
            else
                binding.horizontalProgressbar.visibility = View.INVISIBLE
            LogManager.d()
            list.log()
            adapter.setData(list)
            if (list.size < lastSize) {
                Navigation.selectedSystem.value = -1
                Toast.makeText(
                    requireContext(),
                    getString(R.string.recognition_system_removed),
                    Toast.LENGTH_SHORT
                ).show()
                lastSize = list.size
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}