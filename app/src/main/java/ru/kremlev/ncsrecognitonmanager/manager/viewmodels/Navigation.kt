package ru.kremlev.ncsrecognitonmanager.manager.viewmodels

import androidx.lifecycle.MutableLiveData

object Navigation {
    val selectedSystem: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(-1)
    }
}