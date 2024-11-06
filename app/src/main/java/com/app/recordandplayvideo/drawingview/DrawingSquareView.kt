package com.app.recordandplayvideo.drawingview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class DrawingSquareView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 15f
    }
    var layoutMode: LayoutMode = LayoutMode.PACKED

    private var bitmap1: Bitmap? = null

    private var bitmap2: Bitmap? = null

    private var bitmap3: Bitmap? = null

    private var bitmap4: Bitmap? = null

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
    private var isMirroredBitmap = false


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
        val allowedRegion = RectF(width.toFloat() / 3f, 0.6f * height.toFloat() , width.toFloat() * 2f / 3f, 0.6f * height.toFloat() + width.toFloat() / 3f)
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
                }else {
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

    fun setDistanceHorizontal(scale: Float = scaleFactor, default: Float = 0.75f) {
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
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint) // Vẽ nét gốc

        // Tạo các đường đi mới mà không tích lũy
//        path1.reset()
//        path1.addPath(path)

//        path2.reset()
//        path2.addPath(path)
//        path2.addPath(path3)
//
//        path4.reset()
//        path4.addPath(path)
//        path4.addPath(imgPath4)
//
//        path5.reset()
//        path5.addPath(path4)
//        path5.addPath(imgPath5)
//
//        path8.reset()
//        path8.addPath(imgPath8)
//        path8.addPath(imgPath4)

        val cellWidth = width / 3f
        val cellHeight = height * 0.5f / 3f

        val scaleWidth = width / 8f
        val scaleHeight = height * 0.5f / 8f

        val verticalMove = yMax - yMin // Bạn cần xác định yMax và yMin
        val horizontalMove = xMax - xMin

        // Tính toán a và b
        val a = scaleWidth / horizontalMove
        val b = scaleHeight / verticalMove
//        scaleWidthFactor = b

        // Lấy giá trị lớn hơn giữa a và b
        val maxScale = minOf(a, b)
        scaleFactor = if (maxScale < 1) maxScale else 1f

        //
        val singleBitmapSize = width / 6

        val marginHorizontal = width / 6
        val marginTop = width / 6
       if(isMirroredBitmap){
           if(bitmap1 != null && bitmap2 != null && bitmap3 != null && bitmap4 != null ){
               //1,1
               val scaledBitmap1 = Bitmap.createScaledBitmap(bitmap1!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap1, marginHorizontal.toFloat(), marginTop.toFloat(), null)

               //1,2
               val scaledBitmap2 = Bitmap.createScaledBitmap(bitmap2!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap2, (marginHorizontal + singleBitmapSize).toFloat(), marginTop.toFloat(), null)

               //1,3
               val scaledBitmap3 = Bitmap.createScaledBitmap(bitmap1!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap3, (marginHorizontal + 2 * singleBitmapSize).toFloat(), marginTop.toFloat(), null)

               //1,4
               val scaledBitmap4 = Bitmap.createScaledBitmap(bitmap2!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap4, (marginHorizontal + 3 * singleBitmapSize).toFloat(), marginTop.toFloat(), null)

               //2, 1
               val scaledBitmap5 = Bitmap.createScaledBitmap(bitmap3!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap5, marginHorizontal.toFloat(), (marginTop + singleBitmapSize).toFloat(), null)

               //2, 2
               val scaledBitmap6 = Bitmap.createScaledBitmap(bitmap4!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap6, (marginHorizontal + singleBitmapSize).toFloat(), (marginTop + singleBitmapSize).toFloat(), null)

               //2, 3
               val scaledBitmap7 = Bitmap.createScaledBitmap(bitmap3!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap7, (marginHorizontal + 2 * singleBitmapSize).toFloat(), (marginTop + singleBitmapSize).toFloat(), null)

               //2, 4
               val scaledBitmap8 = Bitmap.createScaledBitmap(bitmap4!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap8, (marginHorizontal + 3 * singleBitmapSize).toFloat(), (marginTop + singleBitmapSize).toFloat(), null)

               //3, 1
               val scaledBitmap9 = Bitmap.createScaledBitmap(bitmap1!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap9, marginHorizontal.toFloat(), (marginTop + 2 * singleBitmapSize).toFloat(), null)

               //3, 2
               val scaledBitmap10 = Bitmap.createScaledBitmap(bitmap2!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap10, (marginHorizontal + singleBitmapSize).toFloat(), (marginTop + 2 * singleBitmapSize).toFloat(), null)

               //3, 3
               val scaledBitmap11 = Bitmap.createScaledBitmap(bitmap1!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap11, (marginHorizontal + 2 * singleBitmapSize).toFloat(), (marginTop + 2 * singleBitmapSize).toFloat(), null)

               //3, 4
               val scaledBitmap12 = Bitmap.createScaledBitmap(bitmap2!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap12, (marginHorizontal + 3 * singleBitmapSize).toFloat(), (marginTop + 2 * singleBitmapSize).toFloat(), null)

               //4, 1
               val scaledBitmap13 = Bitmap.createScaledBitmap(bitmap3!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap13, marginHorizontal.toFloat(), (marginTop + 3 * singleBitmapSize).toFloat(), null)

               //4, 2
               val scaledBitmap14 = Bitmap.createScaledBitmap(bitmap4!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap14, (marginHorizontal + singleBitmapSize).toFloat(), (marginTop + 3 * singleBitmapSize).toFloat(), null)

               //4, 3
               val scaledBitmap15 = Bitmap.createScaledBitmap(bitmap3!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap15, (marginHorizontal + 2 * singleBitmapSize).toFloat(), (marginTop + 3 * singleBitmapSize).toFloat(), null)

               //4, 4
               val scaledBitmap16 = Bitmap.createScaledBitmap(bitmap4!!, singleBitmapSize, singleBitmapSize, true)
               canvas.drawBitmap(scaledBitmap16, (marginHorizontal + 3 * singleBitmapSize).toFloat(), (marginTop + 3 * singleBitmapSize).toFloat(), null)

           }

       }

    }

    fun clearCanvas() {
        path.reset()
        bitmap1 =  getDrawingBitmap()
        bitmap2 = flipBitmapHorizontally(bitmap1!!)
        bitmap3 = flipBitmapVertically(bitmap1!!)
        bitmap4 = flipBitmapVertically(bitmap2!!)
        isMirroredBitmap = false
        invalidate()
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
        createMirroredVerticalPathTop(path1,path4)
        createMirroredHorizontalPathLeft(path4, imgPath5)
        createMirroredHorizontalPathLeft(path4, path6)
        createMirroredHorizontalPathLeft(imgPath4, imgPath8)
        createMirroredHorizontalPathLeft(imgPath4, path9)
    }

    /**/

    fun duplicateBitmap(){
        bitmap1 =  getDrawingBitmap()
        bitmap2 = flipBitmapHorizontally(bitmap1!!)
        bitmap3 = flipBitmapVertically(bitmap1!!)
        bitmap4 = flipBitmapVertically(bitmap2!!)
        isMirroredBitmap = true
    }
    fun getDrawingBitmap(): Bitmap {
        // Lấy tọa độ của RectF
        val allowedRegion = RectF(
            width.toFloat() / 3f,
            0.6f * height.toFloat(),
            width.toFloat() * 2f / 3f,
            0.6f * height.toFloat() + width.toFloat() / 3f
        )

        // Tính kích thước hình vuông từ RectF
        val squareSize = minOf(allowedRegion.width(), allowedRegion.height()).toInt()

        // Tạo Bitmap mới với kích thước hình vuông
        val drawingBitmap = Bitmap.createBitmap(squareSize, squareSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(drawingBitmap)

        // Dịch canvas để vẽ đúng vị trí đường vẽ trong vùng RectF
        canvas.translate(-allowedRegion.left, -allowedRegion.top)

        // Vẽ đường dẫn của người dùng lên canvas của Bitmap
        canvas.drawPath(path, paint)

        return drawingBitmap
    }

    fun flipBitmapHorizontally(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun flipBitmapVertically(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            postScale(1f, -1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    /**/
    fun setLayoutModeCanvas(mode: LayoutMode) {
        layoutMode = mode
        invalidate() // Yêu cầu cập nhật lại view
    }

    fun saveCircularDrawingToBitmap(
        radiusPx: Float = width.toFloat() / 3,
        defaultY: Float = 0.7f
    ): Bitmap {
        // Tính toán các giá trị trung tâm
        val centerX = width.toFloat() / 2
        val centerY = width.toFloat() / 2

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



    fun saveDrawingToBitmap(): Bitmap {
        // Tạo một bitmap với kích thước của RectF
        val allowedRegion = RectF(
            width.toFloat() / 3f,
            0.6f * height.toFloat(),
            width.toFloat() * 2f / 3f,
            0.6f * height.toFloat() + width.toFloat() / 3f
        )

        // Tính kích thước của bitmap dựa trên RectF
        val rectWidth = allowedRegion.width().toInt()
        val rectHeight = allowedRegion.height().toInt()

        // Tạo bitmap với kích thước chính xác
        val bitmap = Bitmap.createBitmap(rectWidth, rectHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dịch chuyển canvas để vẽ từ gốc (0, 0)
        canvas.translate(-allowedRegion.left, -allowedRegion.top)

        // Vẽ đường path của người dùng lên canvas
        canvas.drawPath(path, paint)

        // Reset lại bản vẽ sau khi lưu (tùy chọn)
        path.reset()

        return bitmap
    }




}
