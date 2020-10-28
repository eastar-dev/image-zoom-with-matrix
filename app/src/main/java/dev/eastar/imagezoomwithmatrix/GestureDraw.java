package dev.eastar.imagezoomwithmatrix;

import android.graphics.Matrix;
import android.log.Log;
import android.view.MotionEvent;

public class GestureDraw  extends GestureBase {
	public boolean onTouchEvent(MotionEvent event) {
		final Matrix matrix = mZoomState.getMatrix();
		float[] pts = new float[2];
		pts[0] = event.getX();
		pts[1] = event.getY();
		matrix.mapPoints(pts);

		super.onTouchEvent(event);
		return true;
	}
}
