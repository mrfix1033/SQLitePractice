package ru.mrfix1033.sqlitepractice

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager(context: Context, cursorFactory: CursorFactory?) :
    SQLiteOpenHelper(context, TABLE_NAME, cursorFactory, DATABASE_VERSION) {
    class Key {
        companion object {
            val KEY_ID = "id"
            val KEY_TITLE = "title"
            val KEY_WEIGHT = "weight"
            val KEY_PRICE = "price"
        }
    }

    companion object {
        val TABLE_NAME = "products"
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "${Key.KEY_ID} INT PRIMARY KEY," +
                    "${Key.KEY_TITLE} TEXT," +
                    "${Key.KEY_WEIGHT} FLOAT," +
                    "${Key.KEY_PRICE} FLOAT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insert(product: Product) {
        val values = ContentValues()
        values.run {
            product.run {
                put(Key.KEY_TITLE, title)
                put(Key.KEY_WEIGHT, weight)
                put(Key.KEY_PRICE, price)
            }
        }
        writableDatabase.use { it.insert(TABLE_NAME, null, values) }
    }

    fun select(func: (Cursor) -> Any) {
        readableDatabase.use { it.rawQuery("SELECT * FROM $TABLE_NAME", null).use(func) }
    }
}