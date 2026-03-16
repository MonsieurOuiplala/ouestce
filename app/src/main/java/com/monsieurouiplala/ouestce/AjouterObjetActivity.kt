package com.monsieurouiplala.ouestce

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import androidx.exifinterface.media.ExifInterface
import android.graphics.Matrix

class AjouterObjetActivity : AppCompatActivity() {

    private lateinit var base:BaseDonnees
    private lateinit var image:ImageView
    private lateinit var champDescription:EditText
    private lateinit var champEmplacement:EditText

    private var cheminPhoto=""
    private var objetId=-1
    private var tempPhotoFile:File?=null

    private fun compresserPhoto(path:String){
        val bmp=BitmapFactory.decodeFile(path)?:return
        val max=1200
        val ratio=minOf(max.toFloat()/bmp.width,max.toFloat()/bmp.height,1f)
        val w=(bmp.width*ratio).toInt()
        val h=(bmp.height*ratio).toInt()

        val small=Bitmap.createScaledBitmap(bmp,w,h,true)

        File(path).outputStream().use{
            small.compress(Bitmap.CompressFormat.JPEG,80,it)
        }

        bmp.recycle()
        small.recycle()
    }
    private val prendrePhoto=registerForActivityResult(ActivityResultContracts.TakePicture()){ok->
        if(ok && tempPhotoFile!=null){
            cheminPhoto=tempPhotoFile!!.absolutePath
            compresserPhoto(cheminPhoto)
            chargerBitmap(cheminPhoto)?.let{image.setImageBitmap(it)}
        }
    }

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_objet)

        base=BaseDonnees(this)
        image=findViewById(R.id.photo)
        champDescription=findViewById(R.id.champDescription)
        champEmplacement=findViewById(R.id.champEmplacement)

        objetId=intent.getIntExtra("id",-1)

        if(objetId!=-1){
            base.lireObjet(objetId)?.let{
                champDescription.setText(it.description)
                champEmplacement.setText(it.emplacement)
                if(it.photo.isNotEmpty()){
                    chargerBitmap(it.photo)?.let{bmp->image.setImageBitmap(bmp)}
                    cheminPhoto=it.photo
                }
            }
        }

        findViewById<Button>(R.id.btnPhoto).setOnClickListener{prendrePhoto()}
        findViewById<Button>(R.id.btnSauver).setOnClickListener{sauvegarderObjet()}
    }

    private fun prendrePhoto(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.CAMERA),100)
            return
        }

        tempPhotoFile=File(filesDir,"photo_${System.currentTimeMillis()}.jpg")
        val uri:Uri=FileProvider.getUriForFile(this,"${packageName}.provider",tempPhotoFile!!)
        prendrePhoto.launch(uri)
    }

    override fun onRequestPermissionsResult(req:Int,p:Array<out String>,r:IntArray){
        super.onRequestPermissionsResult(req,p,r)
        if(req==100 && r.isNotEmpty() && r[0]==PackageManager.PERMISSION_GRANTED)prendrePhoto()
    }

    private fun sauvegarderObjet(){
        val desc=champDescription.text.toString()
        val emp=champEmplacement.text.toString()

        if(desc.isBlank()){
            champDescription.error="Description requise"
            return
        }

        if(objetId!=-1)base.mettreAJourObjet(objetId,cheminPhoto,desc,emp)
        else base.ajouterObjet(cheminPhoto,desc,emp)

        finish()
    }

    private fun chargerBitmap(path:String,taille:Int=800):Bitmap?{
        val opt=BitmapFactory.Options().apply{inJustDecodeBounds=true}
        BitmapFactory.decodeFile(path,opt)
        opt.inSampleSize=maxOf(1,minOf(opt.outWidth/taille,opt.outHeight/taille))
        opt.inJustDecodeBounds=false
        var bmp=BitmapFactory.decodeFile(path,opt)?:return null
        try{
            val exif=ExifInterface(path)
            val orient=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
            val angle=when(orient){
                ExifInterface.ORIENTATION_ROTATE_90->90f
                ExifInterface.ORIENTATION_ROTATE_180->180f
                ExifInterface.ORIENTATION_ROTATE_270->270f
                else->0f
            }
            if(angle!=0f){
                val m=Matrix().apply{postRotate(angle)}
                bmp=Bitmap.createBitmap(bmp,0,0,bmp.width,bmp.height,m,true)
            }
        }catch(_:Exception){}
        return bmp
    }
}