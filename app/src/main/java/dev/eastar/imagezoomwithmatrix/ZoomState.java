package dev.eastar.imagezoomwithmatrix;

import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.log.Log;

import java.util.Observable;

public class ZoomState extends Observable {
    private final Matrix mZoomMatrix = new Matrix();
    private final Matrix mCalcTempMatrix = new Matrix();


    private final float[] mSrc = new float[4];
    private final float[] mDst = new float[4];

    private final RectF mSrcRect = new RectF();
    private final RectF mDstRect = new RectF();
    private final RectF mOriginalRect = new RectF();
    private final RectF mViewRect = new RectF();

    // 줌 타입
    public enum ZoomStyle {POLY, RECT}

    private ZoomStyle flagZoomType = ZoomStyle.POLY;

    public synchronized Matrix getMatrix() {
        return mZoomMatrix;
    }

    public void setOriginalRect(float l, float t, float r, float b) {
        mOriginalRect.set(l, t, r, b);
    }

    public void setFrameRect(float l, float t, float r, float b) {
        mViewRect.set(l, t, r, b);
    }

    public void reset() {
        mZoomMatrix.reset();
        setChanged();
    }

    public void setZoomStyle(ZoomStyle zoomStyle) {
        flagZoomType = zoomStyle;
        setChanged();
    }

    public void initPinch(float x1, float y1, float x2, float y2) {
        if (flagZoomType == ZoomStyle.POLY) {
            mSrc[0] = x1;
            mSrc[1] = y1;
            mSrc[2] = x2;
            mSrc[3] = y2;
            mDst[0] = x1;
            mDst[1] = y1;
            mDst[2] = x2;
            mDst[3] = y2;
        } else if (flagZoomType == ZoomStyle.RECT) {
            outRect_of_outCircle(mSrcRect, x1, y1, x2, y2);
            mDstRect.set(mSrcRect);
        }
    }

    public void zoom(float x1, float y1, float x2, float y2) {
        calcZoomMatrix(x1, y1, x2, y2);
        setChanged();
    }


    public synchronized void exitPinch(float x1, float y1, float x2, float y2) {
        calcZoomMatrix(x1, y1, x2, y2);
        setChanged();
    }

    private void calcZoomMatrix(float x1, float y1, float x2, float y2) {
        if (flagZoomType == ZoomStyle.POLY) {
            mDst[0] = x1;
            mDst[1] = y1;
            mDst[2] = x2;
            mDst[3] = y2;
            mCalcTempMatrix.setPolyToPoly(mSrc, 0, mDst, 0, mSrc.length >> 1);
            mSrc[0] = x1;
            mSrc[1] = y1;
            mSrc[2] = x2;
            mSrc[3] = y2;
        } else if (flagZoomType == ZoomStyle.RECT) {
            outRect_of_outCircle(mDstRect, x1, y1, x2, y2);
            mCalcTempMatrix.setRectToRect(mSrcRect, mDstRect, ScaleToFit.CENTER);
            outRect_of_outCircle(mSrcRect, x1, y1, x2, y2);
        }
        mZoomMatrix.postConcat(mCalcTempMatrix);
    }

    private void outRect_of_outCircle(final RectF rc, float x1, float y1, float x2, float y2) {
        float cx = (x1 + x2) / 2f;
        float cy = (y1 + y2) / 2f;

        float x = x1 - x2;
        float y = y1 - y2;
        float r = (float) (Math.sqrt(x * x + y * y) / 2f);

        rc.set(cx - r, cy - r, cx + r, cy + r);
    }

    // debug block
    public OnDebugListener onDebugListener;

    public interface OnDebugListener {
        void onDebugData(ZoomState data);
    }

    public void _dump(final RectF viewRc, final RectF orgRc, final RectF srcRc, final RectF dstRc, float[] src, float[] dst) {
        viewRc.set(mViewRect);
        orgRc.set(mOriginalRect);
        srcRc.set(mSrcRect);
        dstRc.set(mDstRect);
        src[0] = mSrc[0];
        src[1] = mSrc[1];
        src[2] = mSrc[2];
        src[3] = mSrc[3];
        dst[0] = mDst[0];
        dst[1] = mDst[1];
        dst[2] = mDst[2];
        dst[3] = mDst[3];
    }

    public void getZoomRect(RectF dst) {
        dst.set(mOriginalRect);
        Log.e(dst);
        mZoomMatrix.mapRect(dst);
        Log.w(dst);
    }

    public float[] getZoomPoints() {
        RectF dst = mOriginalRect;
//        2*4*2
        float[] points = new float[]{
                dst.left, dst.top,
                dst.right, dst.top,
                dst.right, dst.bottom,
                dst.left, dst.bottom,
                0, 0,
                0, 0,
                0, 0,
                0, 0
        };
        mZoomMatrix.mapPoints(points, 8, points, 0, 4);
        return points;
    }

    @Override
    protected void setChanged() {
        super.setChanged();
        if (onDebugListener != null) {
            onDebugListener.onDebugData(this);
        }
    }
}
