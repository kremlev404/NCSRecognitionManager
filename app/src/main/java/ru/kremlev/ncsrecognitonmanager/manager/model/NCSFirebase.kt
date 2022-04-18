package ru.kremlev.ncsrecognitonmanager.manager.model

import androidx.lifecycle.MutableLiveData

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import ru.kremlev.ncsrecognitonmanager.manager.data.PersonData
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemData
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemType
import ru.kremlev.ncsrecognitonmanager.manager.data.TimeStamp
import ru.kremlev.ncsrecognitonmanager.utils.LogManager

object NCSFirebase {
    private lateinit var database: DatabaseReference
    private var systemIdLocalList: ArrayList<RecognitionSystemData> = arrayListOf()

    val currentUser: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val systemList: MutableLiveData<ArrayList<RecognitionSystemData>> by lazy {
        MutableLiveData<ArrayList<RecognitionSystemData>>()
    }

    fun init() {
        database = Firebase.database.reference
        FirebaseAuth.getInstance().addAuthStateListener {
            currentUser.value = it.currentUser.toString().substringBeforeLast("@")
        }
        database
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    CoroutineScope(Dispatchers.Default).launch {
                        treeDive(snapshot)
                        updateData()
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    CoroutineScope(Dispatchers.Default).launch {
                        treeDive(snapshot)
                        updateData()
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    CoroutineScope(Dispatchers.Default).launch {
                        treeDive(snapshot)
                        updateData()
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    CoroutineScope(Dispatchers.Default).launch {
                        treeDive(snapshot)
                        updateData()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    LogManager.e("", error.toException())
                }
            })
    }

    private fun updateData() {
        systemList.postValue(systemIdLocalList)
        systemIdLocalList = arrayListOf()
    }

    private fun parseSystemId(rsId: DataSnapshot) {
        LogManager.d("parseSystemId: $rsId ")
        val type = parseType(rsId.child("type").value.toString())
        val id = rsId.key.toString().substringAfterLast("rsId:")
        var probs = arrayListOf<Float>()
        val personsList = ArrayList<PersonData>()
        var ts = arrayListOf<TimeStamp>()
        var i = 0

        //system
        rsId.children.forEach { personId ->
            val personID = personId.key.toString().substringAfterLast("pId:")
            if (personID == "type")
                return@forEach
            personId.children.forEach {
                if (it.key?.contains("prob") == true) {
                    val listProb = it.value as ArrayList<Float>
                    probs = listProb
                } else if (it.key?.contains("timestamp") == true) {
                    val listProb = it.value as ArrayList<Long>
                    listProb.forEach { lp ->
                        ts.add(TimeStamp(lp))
                    }
                }
            }

            personsList.add(PersonData(personID, probs, ts))
            ts = arrayListOf()
            probs = arrayListOf()

        }
        systemIdLocalList.add(RecognitionSystemData(id, type, personsList))
    }

    private fun parseType(type: String): RecognitionSystemType {
        LogManager.d("parseType: $type ")
        return when (type) {
            "raspberry" -> RecognitionSystemType.RASPBERRY
            "x86" -> RecognitionSystemType.X86
            else -> RecognitionSystemType.X86
        }
    }

    private fun treeDive(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            val key = dataSnapshot.key.toString()

            if (key.contains("mId:")) {
                parseManagerId(key.substringAfter("mId:"))
            } else if (key.contains("rsId:")) {
                parseSystemId(dataSnapshot)
            } else {
                LogManager.d("${dataSnapshot.key.toString()}:  ${dataSnapshot.value} ")
            }
            dataSnapshot.children.forEach { treeDive(it) }
        }
    }

    private fun parseManagerId(mId: String) {
        LogManager.d("parseManagerId: $mId ")
    }

    private fun parsePersonId(pId: String) {
        LogManager.d("parsePersonId: $pId ")
    }

    private fun parseProb(probs: ArrayList<String>?) {
        LogManager.d("parseProb: ${probs?.javaClass} ")
    }

    private fun parseTimestamps(ts: ArrayList<String>?) {
        LogManager.d("parseTimestamps: ${ts?.javaClass} ")
    }
}