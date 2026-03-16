package com.monsieurouiplala.ouestce

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var base: BaseDonnees
    private lateinit var recycler: RecyclerView
    private lateinit var allObjets: MutableList<Objet>
    private lateinit var adapter:ObjetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        base = BaseDonnees(this)
        recycler = findViewById(R.id.recyclerObjets)
        recycler.layoutManager = GridLayoutManager(this, 2)

        findViewById<Button>(R.id.btnAjouter).setOnClickListener {
            startActivity(Intent(this, AjouterObjetActivity::class.java))
        }

        findViewById<SearchView>(R.id.searchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean { filtrer(newText ?: ""); return true }
        })

        charger()
    }

    override fun onResume() {
        super.onResume()
        charger()
    }

    private fun charger(){
        allObjets=base.lireObjets()
        adapter=ObjetAdapter(allObjets){obj->
            startActivity(Intent(this,ObjetActivity::class.java).apply{putExtra("id",obj.id)})
        }
        recycler.adapter=adapter
    }

    private fun filtrer(t:String){
        val f=allObjets.filter{it.description.contains(t,true)}
        adapter.update(f)
    }
}