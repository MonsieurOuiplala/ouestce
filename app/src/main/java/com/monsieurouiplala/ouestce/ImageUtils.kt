package com.monsieurouiplala.ouestce

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun chargerBitmap(path:String,taille:Int=800):Bitmap?{
    val opt=BitmapFactory.Options().apply{inJustDecodeBounds=true}
    BitmapFactory.decodeFile(path,opt)
    opt.inSampleSize=maxOf(1,minOf(opt.outWidth/taille,opt.outHeight/taille))
    opt.inJustDecodeBounds=false
    return BitmapFactory.decodeFile(path,opt)
}