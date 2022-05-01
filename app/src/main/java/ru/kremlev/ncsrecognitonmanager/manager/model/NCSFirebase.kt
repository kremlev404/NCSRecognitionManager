package ru.kremlev.ncsrecognitonmanager.manager.model

import androidx.lifecycle.MutableLiveData

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

import ru.kremlev.ncsrecognitonmanager.manager.data.PersonData
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemData
import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemType
import ru.kremlev.ncsrecognitonmanager.manager.data.TimeStamp
import ru.kremlev.ncsrecognitonmanager.utils.LogManager

object NCSFirebase {
    private lateinit var database: DatabaseReference
    private val mutex = Mutex()
    private var systemIdLocalList: ArrayList<RecognitionSystemData> = arrayListOf()

    val currentUser: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val systemList: MutableLiveData<ArrayList<RecognitionSystemData>> by lazy {
        MutableLiveData<ArrayList<RecognitionSystemData>>()
    }

    private fun handleChanges(snapshot: DataSnapshot) {
        CoroutineScope(Dispatchers.Default).launch {
            mutex.withLock {
                treeDive(snapshot)
                updateData()
            }
        }
    }

    fun init() {
        database = Firebase.database.reference
        FirebaseAuth.getInstance().addAuthStateListener {
            currentUser.value = it.currentUser?.email.toString().substringBeforeLast("@")
        }
        database
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    LogManager.i()
                    handleChanges(snapshot)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    LogManager.i()
                    handleChanges(snapshot)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    LogManager.i()
                    handleChanges(snapshot)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    LogManager.i()
                    handleChanges(snapshot)
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
                    val listTS = it.value as ArrayList<Long>
                    listTS.forEach { lp ->
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

    fun deletePoint(mId: String, rsId: String, pId: String, index: Int) {
        LogManager.d("deletePoint $mId $rsId $pId $index")
        val personNode = database.child(mId).child(rsId).child(pId)
        val timestamp = personNode.child("timestamp")
        timestamp.get().addOnSuccessListener { tsNode ->
            val prob = database.child(mId).child(rsId).child(pId).child("prob")
            val timestamps = tsNode.value as ArrayList<Long>
            timestamps.removeAt(index)

            prob.get().addOnSuccessListener {
                val probs = it.value as ArrayList<Float>
                probs.removeAt(index)

                personNode.updateChildren(mapOf<String, ArrayList<Float>>(Pair(it.key!!, probs)))
                personNode.updateChildren(mapOf<String, ArrayList<Long>>(Pair(tsNode.key!!, timestamps)))
            }
        }
    }

    private fun treeDive(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            val key = dataSnapshot.key.toString()

            when {
                key.contains("mId:") -> {
                    if (currentUser.value != key.substringAfter("mId:"))
                        return
                }
                key.contains("rsId:") -> {
                    parseSystemId(dataSnapshot)
                }
                else -> {
                    return
                }
            }
            dataSnapshot.children.forEach { treeDive(it) }
        }
    }
}