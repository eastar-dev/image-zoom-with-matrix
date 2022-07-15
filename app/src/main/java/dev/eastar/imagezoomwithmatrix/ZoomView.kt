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


    val paintOrg = Paint()
    val paintNew = Paint()

    init {
        paintOrg.strokeWidth = 50F
        paintNew.strokeWidth = 50F
        paintNew.color = 0xffff0000.toInt()
    }

    canvasMap

    override fun onDraw(canvas: Canvas) {
//        canvas.drawLine(500F, 500F, 1000F, 1000F, paintOrg)
//        canvas.rotate(30F, 500F,500F)
//        canvas.drawLine(500F, 500F, 1000F, 1000F, paintNew)

        repeat(12) {
            canvas.drawLine(500F, 0F, 500F, 500F, paintOrg)
            canvas.rotate(30F, 500F, 500F)
        }

//        repeat(6) {
//            canvas.drawLine(500F, 0F, 500F, 1000F, paintOrg)
//            canvas.rotate(30F, 500F, 500F)
//        }

        canvas.save()
        canvas.translate(500f, 500f)

        repeat(12) {
            canvas.drawLine(0F, 0F, 0F, -500F, paintOrg)
            canvas.rotate(30F)
        }

//        canvas.translate(-500f, -500f)
        canvas.restore()
        canvas.drawLine(500F, 500F, 1000F, 1000F, paintNew)

//        mPaint.alpha = 0x80
//        canvas.drawBitmap(mBitmap, 0f, 0f, mPaint)
//        mPaint.alpha = 0xff
//        canvas.save()
//        canvas.concat(mZoomState.matrix)
//        canvas.drawBitmap(mBitmap, 0f, 0f, mPaint)
//        canvas.restore()
    }

    override fun update(observable: Observable?, data: Any?) {
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mZoomState.setFrameRect(0f, 0f, w.toFloat(), h.toFloat())
        super.onSizeChanged(w, h, oldw, oldh)
    }
}