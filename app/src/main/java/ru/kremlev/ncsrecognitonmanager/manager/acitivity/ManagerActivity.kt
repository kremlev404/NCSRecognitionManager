package ru.kremlev.ncsrecognitonmanager.manager.acitivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.kremlev.ncsrecognitonmanager.databinding.ActivityManagerBinding
import ru.kremlev.ncsrecognitonmanager.utils.LogManager

class ManagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LogManager.d()
    }
}