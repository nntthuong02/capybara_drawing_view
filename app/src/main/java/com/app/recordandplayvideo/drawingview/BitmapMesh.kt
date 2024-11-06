package com.app.recordandplayvideo.drawingview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.app.recordandplayvideo.R


class BitmapMesh : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(SampleView(this))
    }

    private class SampleView(context: Context) : View(context) {
        private val WIDTH = 20
        private val HEIGHT = 20
        private val COUNT = (WIDTH + 1) * (HEIGHT + 1)
        private var mBitmap: Bitmap? = null
        private val mVerts = FloatArray(COUNT * 2)
        private val mOrig = FloatArray(COUNT * 2)
        private val mMatrix = Matrix()
        private val mInverse = Matrix()
        private var globalScale = 0.1f // hệ số zoom tổng thể

        init {
            setFocusable(true)

            // Load bitmap
            mBitmap = BitmapFactory.decodeResource(resources, R.drawable.beach)
            mBitmap = Bitmap.createScaledBitmap(mBitmap!!, 400, 400, true)
            val w = mBitmap!!.width.toFloat()
            val h = mBitmap!!.height.toFloat()

            // Construct the mesh
            var index = 0
            for (y in 0..HEIGHT) {
                val fy = h * y / HEIGHT
                for (x in 0..WIDTH) {
                    val fx = w * x / WIDTH
                    setXY(mVerts, index, fx, fy)
                    setXY(mOrig, index, fx, fy)
                    index++
                }
            }
            mMatrix.setTranslate(10f, 10f)
            mMatrix.invert(mInverse)

            // Apply initial warp centered on the bitmap
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
            // Calculate position to center the bitmap
            val centerX = (w - mBitmap!!.width) / 2f
            val centerY = (h - mBitmap!!.height) / 2f
            mMatrix.setTranslate(centerX, centerY)
            mMatrix.invert(mInverse)
        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawColor(0xFFCCCCCC.toInt())
            canvas.concat(mMatrix)
            canvas.drawBitmapMesh(mBitmap!!, WIDTH, HEIGHT, mVerts, 0, null, 0, null)
        }

        private fun warp(cx: Float, cy: Float) {
            val centerX = mBitmap!!.width / 2f
            val centerY = mBitmap!!.height / 2f
            val maxDistance =
                Math.sqrt((centerX * centerX + centerY * centerY).toDouble()).toFloat()
            val src = mOrig
            val dst = mVerts

            for (i in 0 until COUNT * 2 step 2) {
                val x = src[i]
                val y = src[i + 1]
                val dx = centerX - x
                val dy = centerY - y
                val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                // Tính tỷ lệ giãn giảm dần dựa trên khoảng cách
                val scale =
                    1 + (1 - (distance / maxDistance)) * globalScale * 1.2f // 0.5f là độ giãn tối đa tại tâm

                // Áp dụng giãn
                val newX = centerX + (x - centerX) * scale
                val newY = centerY + (y - centerY) * scale

                dst[i] = newX
                dst[i + 1] = newY
            }
            invalidate()
        }

        private var mLastWarpX = -9999 // don't match a touch coordinate
        private var mLastWarpY: Int = 0

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(event: MotionEvent): Boolean {
            val pt = floatArrayOf(event.x, event.y)
            mInverse.mapPoints(pt)
            val x = pt[0].toInt()
            val y = pt[1].toInt()
            if (mLastWarpX != x || mLastWarpY != y) {
                mLastWarpX = x
                mLastWarpY = y
                if (globalScale < 1f) { // Giới hạn độ giãn tối đa
                    globalScale += 0.01f
                }
                warp(pt[0], pt[1])
                invalidate()
            }
            return true
        }
    }
}
