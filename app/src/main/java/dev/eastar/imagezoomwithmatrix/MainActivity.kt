package dev.eastar.imagezoomwithmatrix

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dev.eastar.imagezoomwithmatrix.ZoomState.ZoomStyle

class MainActivity : AppCompatActivity() {

    private lateinit var mZoomView: ZoomView
//    private lateinit var mZoomStateView: ZoomStateView
    private lateinit var mBitmap: Bitmap

    private var mZoomState: ZoomState = ZoomState()
    private var mZoomGesture: GestureZoom = GestureZoom()
    private var mDrawGesture: GestureDraw = GestureDraw()

    private enum class MODE { ZOOM, DRAW }

    private var mode = MODE.ZOOM

    private var mContext: Context? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        setContentView(R.layout.main)
        mZoomState.deleteObservers()

        GestureBase.setZoomState(mZoomState)
        mZoomView = findViewById(R.id.zoomview)
        mZoomView.setZoomState(mZoomState)
        mZoomView.setOnTouchListener { _, event ->
            when (mode) {
                MODE.ZOOM -> mZoomGesture.onTouchEvent(event)
                MODE.DRAW -> mDrawGesture.onTouchEvent(event)
            }
            true
        }

        mZoomState.addObserver(mZoomView)
//        mZoomStateView = findViewById<View>(R.id.zoomstateview) as ZoomStateView
//        mZoomStateView.setZoomState(mZoomState)
//        mZoomGesture.setOnEventDebugListener(mZoomStateView)
//        mDrawGesture.setOnEventDebugListener(mZoomStateView)

        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.tuna)
        mZoomView.setImage(mBitmap)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBitmap.isRecycled) {
            mBitmap.recycle()
        }
        mZoomState.deleteObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.control, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        when (item.itemId) {
            R.id.reset -> mZoomState.reset()
            R.id.draw -> mode = MODE.DRAW
            R.id.rect -> {
                mZoomState.setZoomStyle(ZoomStyle.RECT)
                mode = MODE.ZOOM
            }
            R.id.poly -> {
                mZoomState.setZoomStyle(ZoomStyle.POLY)
                mode = MODE.ZOOM
            }
            else -> Unit
        }
        mZoomState.notifyObservers()
        return super.onOptionsItemSelected(item)
    }
}