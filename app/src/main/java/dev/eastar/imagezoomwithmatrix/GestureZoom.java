package dev.eastar.imagezoomwithmatrix;

import android.view.MotionEvent;

public class GestureZoom extends GestureBase {
    float r = 100;

    private float x1;
    private float y1;
    private float x2;
    private float y2;

    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX() - r;
                y1 = event.getY() - r;
                x2 = event.getX() + r;
                y2 = event.getY() + r;
                mZoomState.initPinch(x1, y1, x2, y2);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mZoomState.exitPinch(x1, y1, x2, y2);
                x1 = event.getX(0);
                y1 = event.getY(0);
                x2 = event.getX(1);
                y2 = event.getY(1);
                mZoomState.initPinch(x1, y1, x2, y2);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mZoomState.exitPinch(x1, y1, x2, y2);
                int index = event.getActionIndex();
                int pointcount = event.getPointerCount();
                if (pointcount == 2) {
                    x1 = event.getX(1 - index) - r;
                    y1 = event.getY(1 - index) - r;
                    x2 = event.getX(1 - index) + r;
                    y2 = event.getY(1 - index) + r;
                } else if (pointcount > 2 && index < 2) {
                    x1 = event.getX(1 - index);
                    y1 = event.getY(1 - index);
                    x2 = event.getX(2);
                    y2 = event.getY(2);
                } else {
                    x1 = event.getX(0);
                    y1 = event.getY(0);
                    x2 = event.getX(1);
                    y2 = event.getY(1);
                }
                mZoomState.initPinch(x1, y1, x2, y2);
                mZoomState.notifyObservers();
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() >= 2) {
                    x1 = event.getX(0);
                    y1 = event.getY(0);
                    x2 = event.getX(1);
                    y2 = event.getY(1);

                } else if (event.getPointerCount() == 1) {
                    x1 = event.getX(0) - r;
                    y1 = event.getY(0) - r;
                    x2 = event.getX(0) + r;
                    y2 = event.getY(0) + r;
                }
                mZoomState.zoom(x1, y1, x2, y2);
                mZoomState.notifyObservers();
                break;
            case MotionEvent.ACTION_UP:
                x1 = event.getX() - r;
                y1 = event.getY() - r;
                x2 = event.getX() + r;
                y2 = event.getY() + r;
                mZoomState.exitPinch(x1, y1, x2, y2);
                mZoomState.notifyObservers();
                break;
            default:
                break;
        }

        super.onTouchEvent(event);
        return true;
    }
}
