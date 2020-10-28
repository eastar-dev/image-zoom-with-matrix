package dev.eastar.imagezoomwithmatrix

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

class ZoomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle), Observer {
    private val mPaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private lateinit var mZoomState: ZoomState
    fun setZoomState(zoomState: ZoomState) {
        mZoomState = zoomState
    }

    private lateinit var mBitmap: Bitmap
    fun setImage(bitmap: Bitmap) {
        mBitmap = bitmap
        mZoomState.setOriginalRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        mZoomState.reset()
    }

    override fun onDraw(canvas: Canvas) {
        if (mBitmap != null) {
            canvas.save()
            canvas.concat(mZoomState.matrix)
            canvas.drawBitmap(mBitmap, 0f, 0f, mPaint)
            canvas.restore()
        }
    }

    override fun update(observable: Observable?, data: Any?) {
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mZoomState.setFrameRect(0f, 0f, w.toFloat(), h.toFloat())
        super.onSizeChanged(w, h, oldw, oldh)
    }
}