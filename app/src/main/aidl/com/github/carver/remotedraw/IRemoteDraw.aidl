// IRemoteDraw.aidl
package com.github.carver.remotedraw;

import android.view.Surface;
import android.view.MotionEvent;

interface IRemoteDraw {
    void setSurface(in Surface surface);
    boolean dispatchTouchEvent(in MotionEvent event);
}