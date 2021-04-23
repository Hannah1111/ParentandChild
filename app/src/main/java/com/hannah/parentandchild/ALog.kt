package com.hannah.parentandchild

import android.content.Context
import android.util.Log
import android.widget.Toast

object ALog {
    private val isLogView: Boolean = true

    fun d(tag: String?, message: String) {
        if (isLogView) Log.d(tag, setMessage(message)!!)
    }

    fun d(message: String) {
        if (isLogView) Log.d(setTag(), setMessage(message)!!)
    }

    public fun e(tag: String?, message: String) {
        if (isLogView) Log.e(tag, setMessage(message)!!)
    }

    public fun e(message: String) {
        if (isLogView) Log.e(setTag(), setMessage(message)!!)
    }

    fun i(tag: String?, message: String) {
        if (isLogView) Log.i(tag, setMessage(message)!!)
    }

    public fun i(message: String) {
        if (isLogView) Log.i(setTag(), setMessage(message)!!)
    }

    public fun w(tag: String?, message: String) {
        if (isLogView) Log.w(tag, setMessage(message)!!)
    }

    public fun w(message: String) {
        if (isLogView) Log.w(setTag(), setMessage(message)!!)
    }

    private fun setMessage(msg: String): String? {
        val msgBuilder = StringBuilder()
        msgBuilder.append(" (")
            .append(Thread.currentThread().stackTrace[4].fileName)
            .append(":")
            .append(Thread.currentThread().stackTrace[4].lineNumber)
            .append(")     ").append(msg)
        return msgBuilder.toString()
    }

    private fun setTag(): String? {
        return Thread.currentThread().stackTrace[4].className.substring(
            Thread.currentThread().stackTrace[4].className
                .lastIndexOf(".") + 1
        )
    }

    fun withToast(context: Context?, message: String) {
        if (isLogView) {
            try {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
            }
            Log.w(setTag(), setMessage(message)!!)
        }
    }
}