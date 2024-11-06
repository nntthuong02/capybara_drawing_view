package com.app.recordandplayvideo.drawingview;

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.app.recordandplayvideo.R;

class BitmapMesh : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(SampleView(this))
    }

    private class SampleView(context: Context) : View(context) {
        companion object {
            private const val WIDTH = 20
            private const val HEIGHT = 20
            private const val COUNT = (WIDTH + 1) * (HEIGHT + 1)
            private fun setXY(array: FloatArray, index: Int, x: Float, y: Float) {
                array[index * 2] = x
                array[index * 2 + 1] = y
            }
        }

        private val mBitmap: Bitmap
        private val mVerts = FloatArray(COUNT * 2)
        private val mOrig = FloatArray(COUNT * 2)
        private val mMatrix = Matrix()
        private val mInverse = Matrix()
        private var globalScale = 0.1f
        private var mLastWarpX = -9999
        private var mLastWarpY = 0

        init {
            isFocusable = true

            // Load bitmap
            var bitmap = BitmapFactory.decodeResource(resources, R.drawable.beach)
            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true)
            mBitmap = bitmap
            val w = mBitmap.width.toFloat()
            val h = mBitmap.height.toFloat()

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

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            updateMatrix(w, h)
        }

        private fun updateMatrix(w: Int, h: Int) {
            val centerX = (w - mBitmap.width) / 2f
            val centerY = (h - mBitmap.height) / 2f
            mMatrix.setTranslate(centerX, centerY)
            mMatrix.invert(mInverse)
        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawColor(0xFFCCCCCC.toInt())
            canvas.concat(mMatrix)
            canvas.drawBitmapMesh(mBitmap, WIDTH, HEIGHT, mVerts, 0, null, 0, null)
        }

        private fun warp(cx: Float, cy: Float) {
            val centerX = mBitmap.width / 2f
            val centerY = mBitmap.height / 2f
            val maxDistance = kotlin.math.sqrt(centerX * centerX + centerY * centerY)
            val src = mOrig
            val dst = mVerts

            for (i in 0 until COUNT * 2 step 2) {
                val x = src[i]
                val y = src[i + 1]
                val dx = centerX - x
                val dy = centerY - y
                val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                val scale = 1 + (1 - (distance / maxDistance)) * globalScale * 1.2f

                val newX = centerX + (x - centerX) * scale
                val newY = centerY + (y - centerY) * scale

                dst[i] = newX
                dst[i + 1] = newY
            }
            invalidate()
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val pt = floatArrayOf(event.x, event.y)
            mInverse.mapPoints(pt)
            val x = pt[0].toInt()
            val y = pt[1].toInt()
            if (mLastWarpX != x || mLastWarpY != y) {
                mLastWarpX = x
                mLastWarpY = y
                if (globalScale < 1f) {
                    globalScale += 0.01f
                }
                warp(pt[0], pt[1])
                invalidate()
            }
            return true
        }
    }
}
