package com.monsieurouiplala.ouestce

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ObjetAdapter(objetsInit:List<Objet>,private val onItemClick:(Objet)->Unit)
    :RecyclerView.Adapter<ObjetAdapter.ViewHolder>(){

    private val objets=objetsInit.toMutableList()

    class ViewHolder(v:View):RecyclerView.ViewHolder(v){
        val img:ImageView=v.findViewById(R.id.imageObjet)
        val txt:TextView=v.findViewById(R.id.nomObjet)
    }

    override fun onCreateViewHolder(p:ViewGroup,v:Int)=
        ViewHolder(LayoutInflater.from(p.context).inflate(R.layout.item_objet,p,false))

    override fun getItemCount()=objets.size

    override fun onBindViewHolder(h:ViewHolder,pos:Int){
        val obj=objets[pos]
        h.txt.text=obj.description
        if(obj.photo.isNotEmpty())chargerBitmap(obj.photo)?.let{h.img.setImageBitmap(it)}
        else h.img.setImageResource(android.R.color.transparent)
        h.itemView.setOnClickListener{onItemClick(obj)}
    }

    fun update(l:List<Objet>){
        objets.clear()
        objets.addAll(l)
        notifyDataSetChanged()
    }
}