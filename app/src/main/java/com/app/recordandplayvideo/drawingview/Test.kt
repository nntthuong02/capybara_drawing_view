package com.app.recordandplayvideo.drawingview

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class Test(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }

    private var move0x = 0f
    private var move0x1 = 0f
    private var move0x2 = 0f
    private var scaleFactor = 1f
    private val path = Path()
    private var xMin = Float.MAX_VALUE
    private var xMax = Float.MIN_VALUE
    private val flippedPathHorizontal = Path()

    private var yMin = Float.MAX_VALUE
    private var yMax = Float.MIN_VALUE
    private val flippedPathVertical = Path()
    private var isMirrored1 = false

    private var path2 = Path()
    private var path3 = Path()
    private var path4 = Path()
    private var path5 = Path()
    private var imgPath5 = Path()

    private var path6 = Path()
    private var path8 = Path()
    private var imgPath8 = Path()

    private var path9 = Path()

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



    fun setPackedHorizontal(scale: Float = scaleFactor, default: Float = 0.8f) {
        move0x = -xMin + default * width / 3f / scale
        move0x1 = move0x + 2 * (xMax-xMin) - width / 3f / scale + 13 * scale
        move0x2 = move0x1 + 2 * (xMax-xMin) - 1 * width / 3f/scale + 13f
    }


    fun setSpaceBetweenHorizontal(scale: Float = scaleFactor, default: Float = 0.5f){
        move0x = -(xMin) + default * width / 3f /scaleFactor
        move0x1 = -(xMin) + default * width / 3f /scaleFactor
        move0x2 = -(xMin) + default * width / 3f /scaleFactor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint) // Vẽ nét gốc

        val pathCopy = Path().apply {
            addPath(path)
        }
        path2 = Path().apply {
            addPath(path)
            addPath(flippedPathHorizontal)
        }

        path4 = Path().apply {
            addPath(path)
            addPath(flippedPathVertical)
        }

        imgPath5 = Path().apply {
            addPath(path4)
            addPath(path5)
        }

        imgPath5 = Path().apply {
            addPath(path4)
            addPath(path5)
        }

        imgPath8 = Path().apply {
            addPath(path8)
            addPath(flippedPathVertical)
        }
        val cellWidth = width / 3f
        val cellHeight = height * 0.7f / 3f

        val scaleWidth = width / 8f
        val scaleHeight = height * 0.7f / 8f

        val verticalMove = yMax - yMin // Bạn cần xác định yMax và yMin
        val horizontalMove = xMax - xMin

        // Tính toán a và b
        val a = scaleWidth / horizontalMove
        val b = scaleHeight / verticalMove

        // Lấy giá trị lớn hơn giữa a và b
        val maxScale = minOf(a, b)
        if (maxScale < 1) {
            // Nếu maxScale nhỏ hơn 1, tính tỷ lệ giảm
            scaleFactor = maxScale
        } else {
            // Nếu maxScale lớn hơn hoặc bằng 1, giữ nguyên tỷ lệ
            scaleFactor = 1f
        }


        when {
            isMirrored1 ->  {
                val move0y = -(yMin) + scaleHeight/scaleFactor
                //Các cột cách đều nhau
//                val move0x = -(xMin) + 0.5f * cellWidth /scaleFactor
//                val move0x1 = -(xMin) + 0.5f * cellWidth /scaleFactor
//                val move0x2 = -(xMin) + 0.5f * cellWidth /scaleFactor
//
//                move0x = -(xMin) + 0.8f * cellWidth / scaleFactor
//                move0x1 = move0x + 2*(xMax-xMin) - cellWidth/scaleFactor + 13*scaleFactor
//                move0x2 = move0x1 + 2*(xMax-xMin) - 1 * cellWidth/scaleFactor + 13f
                // Dịch chuyển canvas để căn hình đầu tiên về góc trên trái
//                canvas.translate(-xOffset, -yOffset)

//                setPackedHorizontal(scaleFactor)
                setSpaceBetweenHorizontal()
                Log.d("xMin2", xMin.toString())
                Log.d("xMax2", xMax.toString())
                path.reset()
//                val scaleFactor = 0.5f

// Vẽ ô (0, 0)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x , move0y)
                canvas.drawPath(pathCopy, paint)
                canvas.restore()

// Vẽ ô (1, 0)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x1 + 1 * cellWidth/scaleFactor, move0y)
                canvas.drawPath(path2, paint)
                canvas.restore()

// Vẽ ô (2, 0)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x2 + 2 * cellWidth/scaleFactor, move0y)
                canvas.drawPath(path3, paint)
                canvas.restore()

// Vẽ ô (0, 1)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x, move0y + 1 * cellHeight/scaleFactor)
                canvas.drawPath(path4, paint)
                canvas.restore()

// Vẽ ô (1, 1)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x1 + 1 * cellWidth/scaleFactor, move0y + 1 * cellHeight/scaleFactor)
                canvas.drawPath(imgPath5, paint)
                canvas.restore()

// Vẽ ô (2, 1)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x2 + 2 * cellWidth/scaleFactor, move0y + 1 * cellHeight/scaleFactor)
                canvas.drawPath(path6, paint)
                canvas.restore()

// Vẽ ô (0, 2)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x, move0y + 2 * cellHeight/scaleFactor)
                canvas.drawPath(flippedPathVertical, paint)
                canvas.restore()

// Vẽ ô (1, 2)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x1 + 1 * cellWidth/scaleFactor, move0y + 2 * cellHeight/scaleFactor)
                canvas.drawPath(imgPath8, paint)
                canvas.restore()

// Vẽ ô (2, 2)
                canvas.save()
                canvas.scale(scaleFactor, scaleFactor)
                canvas.translate(move0x2 + 2 * cellWidth/scaleFactor, move0y + 2 * cellHeight/scaleFactor)
                canvas.drawPath(path9, paint)
                canvas.restore()


            }
        }
    }

    fun clear() {
        path.reset()
        flippedPathHorizontal.reset()
        flippedPathVertical.reset()
        path2.reset()
        path3.reset()
        path4.reset()
        path5.reset()
        imgPath5.reset()
        path6.reset()
        path8.reset()
        imgPath8.reset()
        path9.reset()
        isMirrored1 = false
        invalidate()
    }

    fun saveDrawingToBitmap(context: Context): Boolean {
        // Tạo bitmap để lưu hình vẽ
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas) // Vẽ lên canvas từ DrawingView

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
        // Tạo nét vẽ lật và dịch chuyển khi nhấn "Create"
        createMirroredHorizontalPathLeft(path, flippedPathHorizontal)
        //->path2
        createMirroredHorizontalPathLeft(path, path3)
        createMirroredVerticalPathTop(path, flippedPathVertical)
        createMirroredVerticalPathTop(path,path4)
        //->path4
        createMirroredHorizontalPathLeft(path4, path5)
        //->imgPath5
        createMirroredHorizontalPathLeft(path4, path6)
        //ve path6
        //ve flippedPathVertical
        createMirroredHorizontalPathLeft(flippedPathVertical, path8)
        //->imtPath8
        //ve path8
        createMirroredHorizontalPathLeft(flippedPathVertical, path9)
    }

}
