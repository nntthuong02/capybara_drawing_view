package com.app.recordandplayvideo.drawingview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.app.recordandplayvideo.R


@SuppressLint("ViewConstructor")
class ZoomHelper(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val WIDTH = 20
    private val HEIGHT = 20
    private val COUNT = (WIDTH + 1) * (HEIGHT + 1)
    private var mBitmap: Bitmap? = null
    private val mVerts = FloatArray(COUNT * 2)
    private val mOrig = FloatArray(COUNT * 2)
    private val mMatrix = Matrix()
    private val mInverse = Matrix()
    private var globalScale = 0.1f // hệ số zoom tổng thể

    // Paint object for drawing the circle
    private val paint = Paint().apply {
        color = Color.RED // Set the circle color to red
        style = Paint.Style.FILL // Fill the circle
    }

    fun setGlobalScale(default: Float){
        globalScale = default
    }

    fun zoom(bitmap: Bitmap) {
        setFocusable(true)
        if (bitmap == null) {
            mBitmap = BitmapFactory.decodeResource(resources, R.drawable.beach)
        } else {
            mBitmap = bitmap
        }
        mBitmap = Bitmap.createScaledBitmap(mBitmap!!, 400, 400, true)
        val w = mBitmap!!.width.toFloat()
        val h = mBitmap!!.height.toFloat()

        var index = 0
        for (y in 0..HEIGHT) {
            val fy = h * y / HEIGHT
            for (x in 0..WIDTH) {
                setXY(mVerts, index, w * x / WIDTH, fy)
                setXY(mOrig, index, w * x / WIDTH, fy)
                index++
            }
        }
        mMatrix.setTranslate(10f, 10f)
        mMatrix.invert(mInverse)

        warp(width.toFloat(), height.toFloat())
    }

    private fun setXY(array: FloatArray, index: Int, x: Float, y: Float) {
        array[index * 2] = x
        array[index * 2 + 1] = y
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateMatrix(w, h)
    }

    private fun updateMatrix(w: Int, h: Int) {
        val centerX = (w - mBitmap!!.width) / 2f
        val centerY = (h - mBitmap!!.height) / 2f
        mMatrix.setTranslate(centerX, centerY)
        mMatrix.invert(mInverse)
    }

    override fun onDraw(canvas: Canvas) {
        // Tính toán bán kính dựa trên kích thước của Bitmap
        val bitmapSize = Math.max(mBitmap!!.width, mBitmap!!.height) // Lấy kích thước lớn nhất giữa chiều rộng và chiều cao của bitmap
        val radius = bitmapSize / 2f // Bán kính sẽ bằng một nửa chiều dài cạnh

        // Vẽ nền là một hình tròn màu đỏ
        val centerX = width / 2f
        val centerY = height / 2f

        // Vẽ hình tròn nền màu đỏ với đường kính bằng kích thước của bitmap
        canvas.drawCircle(centerX, centerY, radius, paint)

        // Áp dụng ma trận và vẽ bitmap mesh
        canvas.concat(mMatrix)
        canvas.drawBitmapMesh(mBitmap!!, WIDTH, HEIGHT, mVerts, 0, null, 0, null)
    }

    fun warp(cx: Float, cy: Float) {
        val centerX = mBitmap!!.width / 2f
        val centerY = mBitmap!!.height / 2f
        val maxDistance = Math.sqrt((centerX * centerX + centerY * centerY).toDouble()).toFloat()
        val src = mOrig
        val dst = mVerts

        for (i in 0 until COUNT * 2 step 2) {
            val x = src[i]
            val y = src[i + 1]
            val dx = centerX - x
            val dy = centerY - y
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            val scale = 1 + (1 - (distance / maxDistance)) * globalScale * 1.2f
            val newX = centerX + (x - centerX) * scale
            val newY = centerY + (y - centerY) * scale

            dst[i] = newX
            dst[i + 1] = newY
        }
        invalidate()
    }

    private var mLastWarpX = -9999
    private var mLastWarpY: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }

    fun startAutoZoom2(){

    }
    fun startAutoZoom() {
        val zoomDuration = 2000L // Thời gian zoom (2 giây)
        val targetScale = 1.0f // Đích của scale
        val steps = zoomDuration / 30 // Số lần cập nhật
        val zoomStep = (targetScale - globalScale) / steps // Tính toán độ thay đổi mỗi lần

        val handler = Handler()
        var currentScale = globalScale

        val runnable = object : Runnable {
            override fun run() {
                if (currentScale < targetScale) {
                    currentScale += zoomStep
                    if (currentScale > targetScale) { // Đảm bảo không vượt quá targetScale
                        currentScale = targetScale
                    }
                    globalScale = currentScale
                    warp(width.toFloat(), height.toFloat()) // Áp dụng zoom
                    invalidate() // Cập nhật giao diện

                    handler.postDelayed(this, 20) // Tiếp tục sau 30ms
                }
            }
        }
        handler.post(runnable) // Bắt đầu thực hiện zoom
    }

}
