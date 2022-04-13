package ru.kremlev.ncsrecognitonmanager.manager.acitivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import ru.kremlev.ncsrecognitonmanager.databinding.ActivityManagerBinding
import ru.kremlev.ncsrecognitonmanager.manager.adapters.RecyclerViewManagerAdapter
import ru.kremlev.ncsrecognitonmanager.utils.LogManager

class ManagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerBinding
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            binding.tvManagerLogin.text = FirebaseAuth.getInstance().currentUser!!.email?.substringBeforeLast("@")
        } catch (e: Exception) {
            LogManager.e("", e)
        }

        val adapter = RecyclerViewManagerAdapter(applicationContext)
        layoutManager = LinearLayoutManager(applicationContext)
        binding.apply {
            recyclerRaspberryListManager.layoutManager = layoutManager

            recyclerRaspberryListManager.adapter = adapter

            val dividerItemDecoration = DividerItemDecoration(
                recyclerRaspberryListManager.context,
                RecyclerView.VERTICAL
            )
            recyclerRaspberryListManager.addItemDecoration(dividerItemDecoration)
        }

        LogManager.d()
    }
}