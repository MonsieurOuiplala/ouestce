package com.monsieurouiplala.ouestce

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

class BaseDonnees(ctx: Context) : SQLiteOpenHelper(ctx, "objets.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE objets(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                photo TEXT,
                description TEXT,
                emplacement TEXT
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {}

    fun ajouterObjet(photo: String, description: String, emplacement: String) {
        writableDatabase.execSQL(
            "INSERT INTO objets(photo,description,emplacement) VALUES(?,?,?)",
            arrayOf(photo, description, emplacement)
        )
    }

    fun lireObjets(): MutableList<Objet> {
        val liste = mutableListOf<Objet>()
        val c = readableDatabase.rawQuery("SELECT id,photo,description,emplacement FROM objets", null)
        while (c.moveToNext()) {
            liste.add(Objet(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)))
        }
        c.close()
        return liste
    }

    fun lireObjet(id: Int): Objet? {
        val c = readableDatabase.rawQuery("SELECT id,photo,description,emplacement FROM objets WHERE id=?", arrayOf(id.toString()))
        val obj = if (c.moveToFirst()) Objet(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)) else null
        c.close()
        return obj
    }

    fun supprimerObjet(id: Int) {
        val obj = lireObjet(id)
        obj?.photo?.takeIf { it.isNotEmpty() }?.let { File(it).delete() }
        writableDatabase.delete("objets", "id=?", arrayOf(id.toString()))
    }

    fun mettreAJourObjet(id: Int, photo: String, description: String, emplacement: String) {
        writableDatabase.execSQL(
            "UPDATE objets SET photo=?, description=?, emplacement=? WHERE id=?",
            arrayOf(photo, description, emplacement, id)
        )
    }
}