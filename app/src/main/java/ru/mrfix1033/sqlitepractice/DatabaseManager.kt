package ru.mrfix1033.sqlitepractice

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull

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
        val DATABASE_VERSION = 3
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "${Key.KEY_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${Key.KEY_TITLE} TEXT," +
                    "${Key.KEY_WEIGHT} FLOAT," +
                    "${Key.KEY_PRICE} FLOAT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE $TABLE_NAME")
    }

    /**
     * @return Row id if getId is true, otherwise null
     */
    @SuppressLint("Range")
    fun insert(product: Product, getId: Boolean = false): Int? {
        val values = ContentValues()
        values.run {
            product.run {
                put(Key.KEY_TITLE, title)
                put(Key.KEY_WEIGHT, weight)
                put(Key.KEY_PRICE, price)
            }
        }
        writableDatabase.use {
            it.insert(TABLE_NAME, null, values)
            if (getId)
                it.rawQuery("SELECT * FROM $TABLE_NAME", null).use {
                    it.moveToLast()
                    return it.getInt(it.getColumnIndex(Key.KEY_ID))
                }
        }
        return null
    }

    fun select(func: (Cursor) -> Any) {
        readableDatabase.use { it.rawQuery("SELECT * FROM $TABLE_NAME", null).use(func) }
    }

    fun delete(id: Int) {
        writableDatabase.use { it.delete(TABLE_NAME, "id=$id", null) }
    }

    fun update(product: Product) {
        val values = ContentValues()
        values.run {
            product.run {
                put("title", title)
                put("weight", weight)
                put("price", price)
            }
        }
        writableDatabase.use { it.update(TABLE_NAME, values, "id=${product.id}", null) }
    }
}