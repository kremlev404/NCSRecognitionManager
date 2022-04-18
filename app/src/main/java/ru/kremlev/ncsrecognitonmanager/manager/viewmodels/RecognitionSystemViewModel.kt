package ru.kremlev.ncsrecognitonmanager.manager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemData
import ru.kremlev.ncsrecognitonmanager.manager.model.NCSFirebase
import ru.kremlev.ncsrecognitonmanager.manager.model.Navigation

class RecognitionSystemViewModel : ViewModel() {
    private val selectedSystem: MutableLiveData<Int> by lazy {
        Navigation.selectedSystem
    }

    val currentUser: MutableLiveData<String> by lazy {
        NCSFirebase.currentUser
    }

    val systemList: MutableLiveData<ArrayList<RecognitionSystemData>> by lazy {
        NCSFirebase.systemList
    }

    fun getSelectedSystem(): LiveData<Int> {
        return selectedSystem
    }
}