package dev.eastar.imagezoomwithmatrix;

import android.view.MotionEvent;

public class GestureBase {
    // debug listener
    public OnEventDebugListener onEventDebugListener;

    public interface OnEventDebugListener {
        void onEventDebugListener(GestureBase gestureBase, MotionEvent event);
    }

    public void setOnEventDebugListener(OnEventDebugListener onEventDebugListener) {
        this.onEventDebugListener = onEventDebugListener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (onEventDebugListener != null)
            onEventDebugListener.onEventDebugListener(this, event);
        return false;
    }


    protected static ZoomState mZoomState;

    public static void setZoomState(ZoomState zoomState) {
        mZoomState = zoomState;
    }
}
