package com.monsieurouiplala.ouestce

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class ObjetActivity : AppCompatActivity() {

    private lateinit var base: BaseDonnees
    private var objetId=-1
    private lateinit var photoView: ImageView
    private lateinit var descView: TextView
    private lateinit var empView: TextView
    private lateinit var btnModifier: Button
    private lateinit var btnSupprimer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_objet)

        base=BaseDonnees(this)
        objetId=intent.getIntExtra("id",-1)
        if(objetId==-1)finish()

        photoView=findViewById(R.id.photo)
        descView=findViewById(R.id.description)
        empView=findViewById(R.id.emplacement)
        btnModifier=findViewById(R.id.btnModifier)
        btnSupprimer=findViewById(R.id.btnSupprimer)

        btnModifier.setOnClickListener {
            startActivity(Intent(this,AjouterObjetActivity::class.java).apply{putExtra("id",objetId)})
        }

        btnSupprimer.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Supprimer ?")
                .setMessage("Voulez-vous vraiment supprimer cet objet ?")
                .setPositiveButton("Oui"){_,_->base.supprimerObjet(objetId);finish()}
                .setNegativeButton("Non",null)
                .show()
        }

        photoView.setOnClickListener {
            val obj=base.lireObjet(objetId)?:return@setOnClickListener
            if(obj.photo.isNotEmpty()){
                val fichier=File(obj.photo)
                if(fichier.exists()){
                    val uri=FileProvider.getUriForFile(this,"${packageName}.provider",fichier)
                    startActivity(Intent(Intent.ACTION_VIEW).apply{
                        setDataAndType(uri,"image/*")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    })
                }
            }
        }

        afficherObjet()
    }

    override fun onResume(){super.onResume();afficherObjet()}

    private fun afficherObjet(){
        val obj=base.lireObjet(objetId)?:run{finish();return}
        descView.text=obj.description
        empView.text="Emplacement : ${obj.emplacement}"
        if(obj.photo.isNotEmpty()){
            BitmapFactory.decodeFile(obj.photo)?.let{photoView.setImageBitmap(it)}
        }
    }
}