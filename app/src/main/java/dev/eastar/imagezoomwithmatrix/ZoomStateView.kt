package dev.eastar.imagezoomwithmatrix

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import dev.eastar.imagezoomwithmatrix.GestureBase.OnEventDebugListener
import java.util.*

class ZoomStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle), OnEventDebugListener {
    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
    }


    data class PointInfo(
        var srcX: Float, var srcY: Float, var id: Int
    )

    data class DrawPointInfo(
        var srcX: Float, var srcY: Float,
        var dstX: Float, var dstY: Float, var id: Int
    )

    private var mW = 0
    private var mH = 0

    private val radius = 100f
    private val mSrcRect = RectF()
    private val mDstRect = RectF()
    private val mOrgRect = RectF()
    private val mViewRect = RectF()
    private val mSrc = FloatArray(4)
    private val mDst = FloatArray(4)
    var eventInfo = ArrayList<PointInfo>()
    var eventDrawInfo = ArrayList<DrawPointInfo>()
    var dst = RectF()
    override fun onDraw(canvas: Canvas) {
        drawPointInfo(canvas, eventInfo)
        drawDrawPointInfo(canvas, eventDrawInfo)
        mZoomState._dump(mViewRect, mOrgRect, mSrcRect, mDstRect, mSrc, mDst)

        // 원본위치
        drawRect(canvas, mOrgRect, Color.BLUE, 5)

        // 변형위치
        mZoomState.getZoomRect(dst)
        drawRect(canvas, dst, Color.MAGENTA, 3)
    }

    private fun drawRect(canvas: Canvas, rect: RectF, color: Int, width: Int) {
        paintLine.strokeWidth = width.toFloat()
        paintLine.color = color
        canvas.drawRect(rect, paintLine)
        canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paintLine)
        canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, paintLine)
    }

    private fun drawPointInfo(canvas: Canvas, pointInfo: ArrayList<PointInfo>) {
        for (info in pointInfo) {
            val x = info.srcX
            val y = info.srcY
            drawLineX(canvas, Color.RED, y)
            drawLineY(canvas, Color.RED, x)
            paintLine.color = Color.LTGRAY
            paintLine.textSize = 40f
            paintLine.textAlign = Paint.Align.RIGHT
            canvas.drawCircle(x, y, radius, paintLine)
            canvas.drawText(info.id.toString(), x + radius, y - radius, paintLine)
        }
    }

    private fun drawDrawPointInfo(canvas: Canvas, drawPointInfo: ArrayList<DrawPointInfo>) {
        for (info in drawPointInfo) {
            run {
                val x = info.dstX
                val y = info.dstY
                drawLineX(canvas, Color.RED and 0x80ffffff.toInt(), y)
                drawLineY(canvas, Color.RED and 0x80ffffff.toInt(), x)
                paintLine.color = Color.LTGRAY
                paintLine.textSize = 40f
                paintLine.textAlign = Paint.Align.RIGHT
                canvas.drawCircle(x, y, radius, paintLine)
                canvas.drawText(info.id.toString(), x + radius, y - radius, paintLine)
            }
            run {
                val x = info.srcX
                val y = info.srcY
                paintLine.color = Color.DKGRAY
                paintLine.textSize = 40f
                paintLine.textAlign = Paint.Align.RIGHT
                canvas.drawCircle(x, y, radius, paintLine)
                canvas.drawText(info.id.toString(), x + radius, y - radius, paintLine)
            }
        }
    }

    private fun drawLineX(canvas: Canvas, color: Int, y: Float) {
        canvas.save()
        canvas.translate(0f, y)
        paintLine.color = color
        canvas.drawLine(0f, 0f, mW.toFloat(), 0f, paintLine)
        canvas.restore()
    }

    private fun drawLineY(canvas: Canvas, color: Int, x: Float) {
        canvas.save()
        canvas.translate(x, 0f)
        paintLine.color = color
        canvas.drawLine(0f, 0f, 0f, mH.toFloat(), paintLine)
        canvas.restore()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mW = w
        mH = h
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private lateinit var mZoomState: ZoomState
    fun setZoomState(zoomState: ZoomState) {
        mZoomState = zoomState
    }

    override fun onEventDebugListener(gesture: GestureBase, event: MotionEvent) {
        eventInfo.clear()
        val count = event.pointerCount
        for (i in 0 until count) {
            eventInfo.add(
                PointInfo(
                    event.getX(i),
                    event.getY(i),
                    event.getPointerId(i)
                )
            )
        }
        eventDrawInfo.clear()

        if (gesture is GestureDraw) {
            val matrix = Matrix()
            mZoomState.matrix.invert(matrix)
            for (i in 0 until count) {
                val src = floatArrayOf(event.getX(i), event.getY(i))
                val dst = FloatArray(2)
                matrix.mapPoints(dst, src)
                eventDrawInfo.add(
                    DrawPointInfo(
                        src[0],
                        src[1],
                        dst[0],
                        dst[1],
                        event.getPointerId(i)
                    )
                )
            }
        }
        invalidate()
    }


}