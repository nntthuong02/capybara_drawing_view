package com.app.recordandplayvideo.drawingview


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RectF
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }
    var layoutMode: LayoutMode = LayoutMode.PACKED

    private var move0x = 0f
    private var move0x1 = 0f
    private var move0x2 = 0f
    private var scaleFactor = 1f
    private var scaleWidthFactor = 1f

    private var xMin = Float.MAX_VALUE
    private var xMax = Float.MIN_VALUE
    private val flippedPathHorizontal = Path()

    private var yMin = Float.MAX_VALUE
    private var yMax = Float.MIN_VALUE
    private val flippedPathVertical = Path()
    private var isMirrored1 = false

    private val path = Path()
    private var path1 = Path()
    private var path2 = Path()
    private var path3 = Path()
    private var imgPath4 = Path()
    private var path4 = Path()
    private var path5 = Path()
    private var imgPath5 = Path()
    private var path6 = Path()
    private var path7 = Path()
    private var path8 = Path()
    private var imgPath8 = Path()
    private var path9 = Path()
    private var imgLate = Path()

    // Hàm để thêm điểm vào path khi người dùng vẽ

//    private val allowedRegion = RectF(width.toFloat() / 4f, 0.6f * height.toFloat() , width.toFloat() * 3f / 4f, 0.8f * height.toFloat())

    private var isDrawing = false
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val allowedRegion = RectF(
            width.toFloat() / 3f,
            0.6f * height.toFloat(),
            width.toFloat() * 2f / 3f,
            0.6f * height.toFloat() + width.toFloat() / 3f
        )
        val x = event.x
        val y = event.y

        Log.d("Size.View Size", "Width: $width, Height: $height")
        Log.d("Size.left", (width.toFloat() / 3f).toString())
        Log.d("Size.right", (width.toFloat() * 2f / 3f).toString())
        Log.d("Size.top", (0.8f * height.toFloat() - width.toFloat() / 3f).toString())
        Log.d("Size.bottom", (0.8f * height.toFloat()).toString())

        // Kiểm tra xem tọa độ có nằm trong vùng cho phép không

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (allowedRegion.contains(x, y)) {
                    isDrawing = true
//                    path.reset()
                    path.moveTo(x, y)
                    xMin = event.x
                    xMax = event.x
                    yMin = event.y
                    yMax = event.y
                } else {
                    // Nếu chạm ngoài, không bắt đầu vẽ
                    isDrawing = false
                }


            }

            MotionEvent.ACTION_MOVE -> {
                if (allowedRegion.contains(x, y)) {
                    if (!isDrawing && allowedRegion.contains(x, y)) {
                        // Bắt đầu vẽ nếu chuyển từ ngoài vào trong
                        isDrawing = true
                        path.moveTo(x, y)
                    }
                    path.lineTo(event.x, event.y)
                    xMin = minOf(xMin, event.x)
                    xMax = maxOf(xMax, event.x)
                    yMin = minOf(yMin, event.y)
                    yMax = maxOf(yMax, event.y)
                }

            }

            MotionEvent.ACTION_UP -> {
                isDrawing = false
            }
        }
        invalidate()
        return true
    }
    //


    fun setPackedHorizontal(scale: Float = scaleFactor, default: Float = 0.75f) {
        if (scale >= 1) {
            //hinh be
            move0x = -xMin + (width * 1f - (xMax - xMin) * 4) / 2
            move0x1 = move0x + 2 * (xMax - xMin) - width / 3f / scale + 13 * scale
            move0x2 = move0x1 + 2 * (xMax - xMin) - 1 * width / 3f / scale + 13f
        } else {
            move0x = -xMin + (width * 1f - (xMax - xMin) * 4 * scale) / 2 / scale
            move0x1 = move0x + 2 * (xMax - xMin) - width / 3f / scale + 13 * scale
            move0x2 = move0x1 + 2 * (xMax - xMin) - 1 * width / 3f / scale + 13f
        }
    }


    fun setSpaceHorizontal(default: Float = 0.5f, scale: Float = scaleFactor) {
        move0x = -(xMin) + default * width / 3f / scale
        move0x1 = -(xMin) + default * width / 3f / scale
        move0x2 = -(xMin) + default * width / 3f / scale
    }

    fun setDistanceHorizontal(scale: Float = scaleFactor, default: Float = 0.75f) {
        if (scale >= 1) {
            //hinh be
            move0x = -xMin + (width * 1f - (xMax - xMin) * 4) / 2
            move0x1 = move0x + 2 * (xMax - xMin) - width / 3f / scale + 13 * scale
            move0x2 = move0x1 + 2 * (xMax - xMin) - 1 * width / 3f / scale + 13f
        } else {
            move0x = -xMin + (width * 1f - (xMax - xMin) * 4 * scale) / 2 / scale
            move0x1 = move0x + 2 * (xMax - xMin) - width / 3f / scale + 13 * scale
            move0x2 = move0x1 + 2 * (xMax - xMin) - 1 * width / 3f / scale + 13f
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(path, paint) // Vẽ nét gốc

        // Tạo các đường đi mới mà không tích lũy
//        path1.reset()
//        path1.addPath(path)

        path2.reset()
        path2.addPath(path)
        path2.addPath(path3)

        path4.reset()
        path4.addPath(path)
        path4.addPath(imgPath4)

        path5.reset()
        path5.addPath(path4)
        path5.addPath(imgPath5)

        path8.reset()
        path8.addPath(imgPath8)
        path8.addPath(imgPath4)

        val cellWidth = width / 3f
        val cellHeight = height * 0.5f / 3f

        val scaleWidth = width / 8f
        val scaleHeight = height * 0.7f / 8f

        val verticalMove = yMax - yMin // Bạn cần xác định yMax và yMin
        val horizontalMove = xMax - xMin

        // Tính toán a và b
        val a = scaleWidth / horizontalMove
        val b = scaleHeight / verticalMove
//        scaleWidthFactor = b

        // Lấy giá trị lớn hơn giữa a và b
        val maxScale = minOf(a, b)
        scaleFactor = if (maxScale < 1) maxScale else 1f

        when {
            isMirrored1 -> {
                val move0y = -(yMin) + scaleHeight / scaleFactor
                when (layoutMode) {
                    LayoutMode.PACKED -> setPackedHorizontal()
                    LayoutMode.SPACED -> setSpaceHorizontal()
                    LayoutMode.DISTANCE_HORIZONTAL -> setDistanceHorizontal()
                }

                // Lưu log xMin và xMax
                Log.d("xMin2", xMin.toString())
                Log.d("xMax2", xMax.toString())


                // Vẽ các ô
                // Vẽ ô (0, 0)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x, move0y)
                canvas.drawPath(path1, paint)
                canvas.restore()

                // Vẽ ô (1, 0)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x1 + 1 * cellWidth / scaleFactor, move0y)
                canvas.drawPath(path2, paint)
                canvas.restore()

                // Vẽ ô (2, 0)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x2 + 2 * cellWidth / scaleFactor, move0y)
                canvas.drawPath(path3, paint)
                canvas.restore()

                // Vẽ ô (0, 1)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x, move0y + 1 * cellHeight / scaleFactor)
                canvas.drawPath(path4, paint)
                canvas.restore()

                // Vẽ ô (1, 1)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(
                    move0x1 + 1 * cellWidth / scaleFactor,
                    move0y + 1 * cellHeight / scaleFactor
                )
                Log.d("height1", height.toString())
                Log.d("height2", (move0y * scaleFactor + 1 * cellWidth).toString())
                Log.d("height3", (height / (scaleHeight + 1 * cellWidth)).toString())
                Log.d("scaleFactorOnDraw", scaleFactor.toString())

                canvas.drawPath(path5, paint)
                canvas.restore()

                // Vẽ ô (2, 1)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(
                    move0x2 + 2 * cellWidth / scaleFactor,
                    move0y + 1 * cellHeight / scaleFactor
                )
                canvas.drawPath(path6, paint)
                canvas.restore()

                // Vẽ ô (0, 2)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x, move0y + 2 * cellHeight / scaleFactor)
                canvas.drawPath(imgPath4, paint)
                canvas.restore()

                // Vẽ ô (1, 2)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(
                    move0x1 + 1 * cellWidth / scaleFactor,
                    move0y + 2 * cellHeight / scaleFactor
                )
                canvas.drawPath(path8, paint)
                canvas.restore()

                // Vẽ ô (2, 2)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(
                    move0x2 + 2 * cellWidth / scaleFactor,
                    move0y + 2 * cellHeight / scaleFactor
                )
                canvas.drawPath(path9, paint)
                canvas.restore()

            }
        }

    }


    fun clear() {
        path.reset()
        flippedPathHorizontal.reset()
        flippedPathVertical.reset()
        path1.reset()
        path2.reset()
        path3.reset()
        imgPath4.reset()
        path4.reset()
        path5.reset()
        imgPath5.reset()
        path6.reset()
        path7.reset()
        path8.reset()
        imgPath8.reset()
        path9.reset()
        isMirrored1 = false
        invalidate()
    }

    private fun createMirroredHorizontalPathLeft(sourcePath: Path, targetPath: Path) {
        val width = -(xMax - xMin + 13f)
        val mirrorMatrix = android.graphics.Matrix().apply {
            preScale(-1f, 1f, (xMin + xMax) / 2, 0f)
            postTranslate(width, 0f)
        }
        val mirroredPath = Path(sourcePath)
        mirroredPath.transform(mirrorMatrix)
        targetPath.addPath(mirroredPath)
        invalidate() // Cập nhật lại view
    }

    private fun createMirrored(sourcePath: Path, targetPath: Path) {
        // Tạo một bản sao của sourcePath và thêm vào targetPath
        val mirroredPath = Path(sourcePath) // Tạo bản sao mà không thay đổi
        targetPath.addPath(mirroredPath) // Thêm bản sao vào targetPath

        // Cập nhật lại view
        invalidate()
    }

    private fun createMirroredVerticalPathTop(sourcePath: Path, targetPath: Path) {
        val height = -yMax + yMin - 13f
        val mirrorMatrix = android.graphics.Matrix().apply {
            preScale(1f, -1f, 0f, (yMin + yMax) / 2)
            postTranslate(0f, height)
        }
        val mirroredPath = Path(sourcePath)
        mirroredPath.transform(mirrorMatrix)
        targetPath.addPath(mirroredPath)
        invalidate() // Cập nhật lại view
    }

    // Hàm gọi từ nút Create
    fun duplicateAndFlip() {
        isMirrored1 = true
        createMirrored(path, path1)
        createMirroredHorizontalPathLeft(path1, path3)
        createMirroredVerticalPathTop(path1, imgPath4)
        createMirroredVerticalPathTop(path1, path4)
        createMirroredHorizontalPathLeft(path4, imgPath5)
        createMirroredHorizontalPathLeft(path4, path6)
        createMirroredHorizontalPathLeft(imgPath4, imgPath8)
        createMirroredHorizontalPathLeft(imgPath4, path9)
    }

    fun setLayoutModeCanvas(mode: LayoutMode) {
        layoutMode = mode
        invalidate() // Yêu cầu cập nhật lại view
    }

    fun saveCircularDrawingToBitmap(
        radiusPx: Float = width.toFloat() / 2 - width.toFloat() / 6,
        defaultY: Float = 0.7f
    ): Bitmap {
        // Tính toán các giá trị trung tâm
        val centerX = width.toFloat() / 2
        val centerY = height.toFloat() / 3.9617975f

        // Tạo bitmap ban đầu để lưu hình tròn
        val originalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val originalCanvas = Canvas(originalBitmap)

        // Vẽ hình trong suốt trên canvas
        originalCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // Cắt vùng hình tròn
        val pathCircle = Path().apply {
            addCircle(centerX, centerY, radiusPx, Path.Direction.CW)
        }
        originalCanvas.clipPath(pathCircle)

        // Vẽ đường dẫn lên canvas từ DrawingView
        draw(originalCanvas)

        val squareSize = (radiusPx * 2).toInt()
        val left = (centerX - radiusPx).toInt()
        val top = (centerY - radiusPx).toInt()

        return Bitmap.createBitmap(originalBitmap, left, top, squareSize, squareSize)
    }


}
