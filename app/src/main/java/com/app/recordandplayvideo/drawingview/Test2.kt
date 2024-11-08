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
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class Test2(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }
    var layoutMode: LayoutMode = LayoutMode.PACKED

    private var move0x = 0f
    private var move0x1 = 0f
    private var move0x2 = 0f
    private var scaleFactor = 1f
    private var scaleWidthFactor = 1f
    private val path = Path()
    private var xMin = Float.MAX_VALUE
    private var xMax = Float.MIN_VALUE
    private val flippedPathHorizontal = Path()

    private var yMin = Float.MAX_VALUE
    private var yMax = Float.MIN_VALUE
    private val flippedPathVertical = Path()
    private var isMirrored1 = false

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
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                xMin = event.x
                xMax = event.x
                yMin = event.y
                yMax = event.y
                invalidate()
                Log.d("xMin", xMin.toString())
                Log.d("xMax", xMax.toString())
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                xMin = minOf(xMin, event.x)
                xMax = maxOf(xMax, event.x)
                yMin = minOf(yMin, event.y)
                yMax = maxOf(yMax, event.y)
                invalidate()
            }
        }
        return true
    }

    //



    fun setPackedHorizontal(scale: Float = scaleFactor, default: Float = 0.75f) {
        if(scale >= 1){
            //hinh be
            move0x = -xMin + (width * 1f - (xMax - xMin) * 4) / 2
            move0x1 = move0x + 2 * (xMax-xMin) - width / 3f / scale + 13 * scale
            move0x2 = move0x1 + 2 * (xMax-xMin) - 1 * width / 3f/scale + 13f
        } else {
            move0x = -xMin+ (width * 1f - (xMax - xMin) * 4 * scale) / 2 / scale
            move0x1 = move0x + 2 * (xMax-xMin) - width / 3f / scale + 13 * scale
            move0x2 = move0x1 + 2 * (xMax-xMin) - 1 * width / 3f/scale + 13f
        }
    }


    fun setSpaceHorizontal(default: Float = 0.5f, scale: Float = scaleFactor){
        move0x = -(xMin) + default * width / 3f /scale
        move0x1 = -(xMin) + default * width / 3f /scale
        move0x2 = -(xMin) + default * width / 3f /scale
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
        val cellHeight = height * 0.7f / 3f

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
                    LayoutMode.DISTANCE_HORIZONTAL -> setPackedHorizontal()
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
                canvas.translate(move0x, move0y + 1 * cellWidth / scaleFactor)
                canvas.drawPath(path4, paint)
                canvas.restore()

                // Vẽ ô (1, 1)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x1 + 1 * cellWidth / scaleFactor, move0y + 1 * cellWidth / scaleFactor)
                Log.d("height1", height.toString())
                Log.d("height2", (move0y * scaleFactor + 1 * cellWidth).toString())
                Log.d("height3", (height / (scaleHeight + 1 * cellWidth)).toString())
                Log.d("scaleFactorOnDraw", scaleFactor.toString())

                canvas.drawPath(path5, paint)
                canvas.restore()

                // Vẽ ô (2, 1)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x2 + 2 * cellWidth / scaleFactor, move0y + 1 * cellWidth / scaleFactor)
                canvas.drawPath(path6, paint)
                canvas.restore()

                // Vẽ ô (0, 2)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x, move0y + 2 * cellWidth / scaleFactor)
                canvas.drawPath(imgPath4, paint)
                canvas.restore()

                // Vẽ ô (1, 2)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x1 + 1 * cellWidth / scaleFactor, move0y + 2 * cellWidth / scaleFactor)
                canvas.drawPath(path8, paint)
                canvas.restore()

                // Vẽ ô (2, 2)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x2 + 2 * cellWidth / scaleFactor, move0y + 2 * cellWidth / scaleFactor)
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

    fun saveDrawingToBitmap(context: Context): Boolean {
        // Tạo bitmap để lưu hình vẽ với độ trong suốt
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Đặt màu nền của canvas là trong suốt
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // Vẽ lên canvas từ DrawingView
        draw(canvas) // Vẽ các đường dẫn lên canvas

        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "drawing_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                val outputStream = context.contentResolver.openOutputStream(it)
                outputStream?.let { stream ->
                    // Lưu bitmap với định dạng PNG
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.flush()
                    Toast.makeText(context, "Drawing saved to gallery", Toast.LENGTH_SHORT).show()
                }
                outputStream?.close()
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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
        createMirroredVerticalPathTop(path1,path4)
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
        context: Context,
        radiusPx: Float = width.toFloat() / 2 - width.toFloat() / 6,
        defaultY: Float = 0.7f
    ): Uri? {
        // Tính toán các giá trị trung tâm
        val centerX = width.toFloat() / 2
        val centerY = height.toFloat() / 3.5646877f

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

        // Tính toán kích thước và vị trí hình vuông bao quanh hình tròn
        val squareSize = (radiusPx * 2).toInt()
        val left = (centerX - radiusPx).toInt()
        val top = (centerY - radiusPx).toInt()

        // Tạo bitmap mới để chứa hình vuông cắt ra từ hình tròn
        val squareBitmap = Bitmap.createBitmap(originalBitmap, left, top, squareSize, squareSize)

        // Lưu bitmap hình vuông vào thư viện
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "square_circular_drawing_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                val outputStream = context.contentResolver.openOutputStream(it)
                outputStream?.let { stream ->
                    squareBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.flush()
                    Toast.makeText(context, "Square circular drawing saved to gallery", Toast.LENGTH_SHORT).show()
                }
                outputStream?.close()
                true
            }
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}
