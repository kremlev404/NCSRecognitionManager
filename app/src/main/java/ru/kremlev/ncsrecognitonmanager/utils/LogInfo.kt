package ru.kremlev.ncsrecognitonmanager.utils

import ru.kremlev.ncsrecognitonmanager.resources.applicationName

abstract class LogInfo {

    private val methodIndex = 4
    protected val TAG = applicationName
    protected val isLoggingEnabled: Boolean = true

    protected val info: String
        get() {
            val stackTrace = Thread.currentThread().stackTrace
            val frame = stackTrace[methodIndex]

            return "${frame.className}::${frame.methodName}::line(${frame.lineNumber})"
        }
}