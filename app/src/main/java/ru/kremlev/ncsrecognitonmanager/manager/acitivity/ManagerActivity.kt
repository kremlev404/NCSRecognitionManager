package ru.kremlev.ncsrecognitonmanager.manager.acitivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator

import com.google.firebase.auth.FirebaseAuth

import ru.kremlev.ncsrecognitonmanager.databinding.ActivityManagerBinding
import ru.kremlev.ncsrecognitonmanager.manager.AdapterTabPager
import ru.kremlev.ncsrecognitonmanager.manager.adapters.RecyclerViewManagerAdapter
import ru.kremlev.ncsrecognitonmanager.manager.fragments.ManagerFragment
import ru.kremlev.ncsrecognitonmanager.manager.fragments.StatisticFragment
import ru.kremlev.ncsrecognitonmanager.utils.LogManager

class ManagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerBinding
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = AdapterTabPager(this)
        adapter.addFragment(ManagerFragment(), "Manager")
        adapter.addFragment(StatisticFragment(), "Statistic")

        binding.pager.adapter = adapter
        binding.pager.currentItem = 0
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        LogManager.d()
    }
}