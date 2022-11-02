package com.github.carver.remotedraw.lib

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RemoteDrawService : Service() {

    private val remoteDrawImpl = RemoteDrawImpl(this)

    override fun onBind(p0: Intent?): IBinder {
        return remoteDrawImpl
    }
}