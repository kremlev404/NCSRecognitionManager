package ru.kremlev.ncsrecognitonmanager.manager.acitivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get

import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

import ru.kremlev.ncsrecognitonmanager.auth.activity.ManagerAuthActivity
import ru.kremlev.ncsrecognitonmanager.databinding.ActivityManagerBinding
import ru.kremlev.ncsrecognitonmanager.manager.AdapterTabPager
import ru.kremlev.ncsrecognitonmanager.manager.fragments.ManagerFragment
import ru.kremlev.ncsrecognitonmanager.manager.fragments.StatisticFragment
import ru.kremlev.ncsrecognitonmanager.utils.LogManager

class ManagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerBinding

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

        binding.navigationView.itemIconTintList = null
        binding.navigationView.menu[0].setOnMenuItemClickListener {
            LogManager.d("Logout btn pressed")
            FirebaseAuth.getInstance().signOut()
            true
        }
        FirebaseAuth.getInstance().addAuthStateListener {
            if (it.currentUser == null) {
                LogManager.d("User logged out")
                val managerIntent = Intent(this@ManagerActivity, ManagerAuthActivity::class.java)
                managerIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(managerIntent)
                finish()
            }
        }

        LogManager.d()
    }
}