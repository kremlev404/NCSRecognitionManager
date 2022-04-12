package ru.kremlev.ncsrecognitonmanager.utils

import android.util.Log

internal object LogManager : LogInfo() {
    fun e() {
        if (isLoggingEnabled)
            Log.e(TAG, info)
    }

    fun e(arg: String = "", thr: Throwable? = null) {
        if (isLoggingEnabled)
            Log.e(TAG, info + arg, thr)
    }

    fun d() {
        if (isLoggingEnabled)
            Log.d(TAG, info)
    }

    fun d(arg: String) {
        if (isLoggingEnabled)
            Log.d(TAG, info + arg)
    }

    fun i() {
        if (isLoggingEnabled)
            Log.i(TAG, info)
    }

    fun i(arg: String) {
        if (isLoggingEnabled)
            Log.i(TAG, info + arg)
    }
}