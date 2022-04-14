package ru.kremlev.ncsrecognitonmanager.manager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemData

class RecognitionSystemViewModel : ViewModel() {
    private val selectedSystem: MutableLiveData<Int> by lazy {
        Navigation.selectedSystem
    }

    val recognitionSystemData: MutableLiveData<ArrayList<RecognitionSystemData>> by lazy {
        MutableLiveData<ArrayList<RecognitionSystemData>>()
    }

    fun getSelectedSystem(): LiveData<Int> {
        return selectedSystem
    }

}