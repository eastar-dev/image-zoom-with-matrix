package dev.eastar.imagezoomwithmatrix

import android.content.Context
import android.graphics.*
import android.log.Log
import android.log.Log.e
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


    class EventInfo(var x: Float, var y: Float, var id: Int) {
        var color = Color.LTGRAY
    }

    private var mW = 0
    private var mH = 0

    private val radius = 100f
    private val mSrcRect = RectF()
    private val mDstRect = RectF()
    private val mOrgRect = RectF()
    private val mViewRect = RectF()
    private val mSrc = FloatArray(4)
    private val mDst = FloatArray(4)
    var eventInfo = ArrayList<EventInfo>()
    var eventDrawInfo = ArrayList<EventInfo>()
    var mCenterPoint = PointF()
    var dst = RectF()
    override fun onDraw(canvas: Canvas) {
        drawPointReleave(canvas, eventInfo)
        drawPointEffects(canvas, eventInfo)
        drawPointEffects(canvas, eventDrawInfo)
        mZoomState._dump(mViewRect, mOrgRect, mSrcRect, mDstRect, mSrc, mDst)

        // 원본위치
        drawRect(canvas, mOrgRect, Color.BLUE, 5)

        // 변형위치
        //mZoomState.getZoomRect(dst)
        //drawRect(canvas, dst, Color.MAGENTA, 3)

        Log.draw
        mZoomState.zoomPoints
        drawRect(canvas, dst, Color.MAGENTA, 3)
    }

    private fun drawRect(canvas: Canvas, rect: RectF, color: Int, width: Int) {
        paintLine.strokeWidth = width.toFloat()
        paintLine.color = color
        canvas.drawRect(rect, paintLine)
        canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paintLine)
        canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, paintLine)
    }

    private fun drawPointEffects(canvas: Canvas, eventInfos: ArrayList<EventInfo>) {
        for (eventInfo in eventInfos) {
            drawPointEffect(canvas, eventInfo)
        }
    }

    private fun drawPointEffect(canvas: Canvas, eventInfo: EventInfo) {
        val x = eventInfo.x
        val y = eventInfo.y
        val color = eventInfo.color
        drawLineX(canvas, Color.RED, y)
        drawLineY(canvas, Color.RED, x)
        paintLine.color = color
        paintLine.textSize = 40f
        paintLine.textAlign = Paint.Align.RIGHT
        canvas.drawCircle(x, y, radius, paintLine)
        canvas.drawText(eventInfo.id.toString(), x + radius, y - radius, paintLine)
    }

    private fun drawPointReleave(canvas: Canvas, eventInfos: ArrayList<EventInfo>) {
        if (eventInfos.size >= 2)
            drawPointReleave(canvas, PointF(eventInfos[0].x, eventInfos[0].y), PointF(eventInfos[1].x, eventInfos[1].y))
    }

    private fun drawPointReleave(canvas: Canvas, p1: PointF, p2: PointF) {
        paintLine.strokeWidth = 1f
        paintLine.color = Color.BLUE
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paintLine)
        canvas.drawLine(p1.x, p2.y, p2.x, p1.y, paintLine)
        centerPoint(mCenterPoint, p1, p2)
        val r = distance(p1, p2) / 2f
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, distance(p1, p2) / 2, paintLine)
        canvas.drawRect(
            mCenterPoint.x - r,
            mCenterPoint.y - r,
            mCenterPoint.x + r,
            mCenterPoint.y + r,
            paintLine
        )
    }

    fun drawLineX(canvas: Canvas, color: Int, y: Float) {
        canvas.save()
        canvas.translate(0f, y)
        paintLine.color = color
        canvas.drawLine(0f, 0f, mW.toFloat(), 0f, paintLine)
        canvas.restore()
    }

    fun drawLineY(canvas: Canvas, color: Int, x: Float) {
        canvas.save()
        canvas.translate(x, 0f)
        paintLine.color = color
        canvas.drawLine(0f, 0f, 0f, mH.toFloat(), paintLine)
        canvas.restore()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        e()
        mW = w
        mH = h
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun distance(p1: PointF, p2: PointF): Float {
        val x = p1.x - p2.x
        val y = p1.y - p2.y
        return Math.sqrt(x * x + y * y.toDouble()).toFloat()
    }

    private fun centerPoint(p: PointF, p1: PointF, p2: PointF) {
        val x = p1.x + p2.x
        val y = p1.y + p2.y
        p[x / 2] = y / 2
    }

    private lateinit var mZoomState: ZoomState
    fun setZoomState(zoomState: ZoomState) {
        mZoomState = zoomState
    }

    override fun onEventDebugListener(gesture: GestureBase, event: MotionEvent) {
        eventInfo.clear()
        val count = event.pointerCount
        for (i in 0 until count) {
            eventInfo.add(EventInfo(event.getX(i), event.getY(i), event.getPointerId(i)))
        }
        if (eventInfo.size > 0) eventInfo[0].color = Color.LTGRAY
        if (eventInfo.size > 1) eventInfo[1].color = Color.DKGRAY
        eventDrawInfo.clear()

        if (gesture is GestureDraw) {
            val matrix = Matrix()
            mZoomState.matrix.invert(matrix)
            for (i in 0 until count) {
                val src = floatArrayOf(event.getX(i), event.getY(i))
                val dst = FloatArray(2)
                matrix.mapPoints(dst, src)
                eventDrawInfo.add(EventInfo(dst[0], dst[1], event.getPointerId(i)))
            }
        }
        invalidate()
    }


}