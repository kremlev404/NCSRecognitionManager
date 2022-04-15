package ru.kremlev.ncsrecognitonmanager.manager.model

import androidx.lifecycle.MutableLiveData

object Navigation {
    val selectedSystem: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(-1)
    }
}