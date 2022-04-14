package ru.kremlev.ncsrecognitonmanager.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import ru.kremlev.ncsrecognitonmanager.databinding.FragmentManagerBinding
import ru.kremlev.ncsrecognitonmanager.manager.adapters.RecyclerViewManagerAdapter
import ru.kremlev.ncsrecognitonmanager.utils.LogManager

class ManagerFragment : Fragment() {
    private var _binding: FragmentManagerBinding? = null
    private val binding: FragmentManagerBinding
        get() = _binding!!

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
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}