package ru.mendel.apps.rcoko27.images

import android.graphics.*
import android.os.Build

object ImageHelper {

    fun answerBitmap(size: Int, total: Int):Bitmap{

        //считаем процент
        val sz = size.toFloat()/total.toFloat()

        val h = 50
        val wImg = 900
        val w = 1000

        //создаем картинку
        val output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#0093dd")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(0f,0f,wImg*sz,h.toFloat(),10f,10f,paint)
        }else{
            canvas.drawRect(0f,0f,wImg*sz,h.toFloat(),paint)
        }

        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        paint.textSize=40f
        canvas.drawText(size.toString(),wImg*sz+12f,40f,paint)

        return output
    }

}