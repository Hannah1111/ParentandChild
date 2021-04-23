package com.hannah.parentandchild

import android.app.Application

class PApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ALog.e("--- PAppliation ")
        Utils.createChannel(this)
    }
}