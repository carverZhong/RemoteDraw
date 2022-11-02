package com.github.carver.remotedraw.lib

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.github.carver.remotedraw.IRemoteDraw
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 继承了 SurfaceView，提供跨进程渲染的 Surface。
 */
class ProcessSurfaceView : SurfaceView, SurfaceHolder.Callback, ServiceConnection {
    private var surface: Surface? = null
    private var iRemoteDraw: IRemoteDraw? = null
    private val isSetSurface = AtomicBoolean(false)

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        holder.addCallback(this@ProcessSurfaceView)
        bindService()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        surface = p0.surface
        setSurfaceToRemote()
        Log.i(TAG, "surfaceCreated")
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        Log.i(TAG, "surfaceChanged")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        Log.i(TAG, "surfaceDestroyed")
        isSetSurface.set(false)
    }

    override fun onServiceConnected(p0: ComponentName?, iBinder: IBinder?) {
        Log.i(TAG, "onServiceConnected")
        if (iBinder == null) {
            Log.e(TAG, "onServiceDisconnected: iBinder is null.")
            return
        }
        iRemoteDraw = IRemoteDraw.Stub.asInterface(iBinder)
        setSurfaceToRemote()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Log.e(TAG, "onServiceDisconnected.")
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return iRemoteDraw?.dispatchTouchEvent(event) ?: false
    }

    private fun bindService() {
        val intent = Intent(context, RemoteDrawService::class.java)
        context.bindService(intent, this@ProcessSurfaceView, Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT)
    }

    private fun setSurfaceToRemote() {
        if (isSetSurface.get()) {
            Log.i(TAG, "setSurfaceToRemote: has set surface.")
            return
        }
        if (iRemoteDraw != null && surface != null) {
            iRemoteDraw!!.setSurface(surface!!)
            isSetSurface.set(true)
        }
    }

    companion object {
        private const val TAG = "ProcessSurfaceView"
    }

}