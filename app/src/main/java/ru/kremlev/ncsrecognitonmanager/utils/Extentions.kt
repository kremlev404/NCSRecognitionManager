package ru.kremlev.ncsrecognitonmanager.utils

import ru.kremlev.ncsrecognitonmanager.manager.data.RecognitionSystemData

fun ArrayList<RecognitionSystemData>.log() {
    this.forEach {
        LogManager.i("id: ${it.id}, type: ${it.type}")
    }
}