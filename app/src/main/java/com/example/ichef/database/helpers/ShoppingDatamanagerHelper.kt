package com.example.ichef.database.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ShoppingDatamanagerHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "store_database.db"
        private const val DATABASE_VERSION = 1

        // Table and Column Names
        const val TABLE_STORE = "stores"
        const val COLUMN_STORE_ID = "store_id"
        const val COLUMN_STORE_NAME = "store_name"

        const val TABLE_INGREDIENT = "ingredients"
        const val COLUMN_INGREDIENT_ID = "ingredient_id"
        const val COLUMN_INGREDIENT_NAME = "ingredient_name"
        const val COLUMN_INGREDIENT_CHECKED = "is_checked"
        const val COLUMN_INGREDIENT_STORE_ID = "store_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createStoreTable = """
            CREATE TABLE $TABLE_STORE (
                $COLUMN_STORE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_STORE_NAME TEXT NOT NULL
            )
        """
        db.execSQL(createStoreTable)

        val createIngredientTable = """
            CREATE TABLE $TABLE_INGREDIENT (
                $COLUMN_INGREDIENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_INGREDIENT_NAME TEXT NOT NULL,
                $COLUMN_INGREDIENT_CHECKED INTEGER NOT NULL,
                $COLUMN_INGREDIENT_STORE_ID INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_INGREDIENT_STORE_ID) REFERENCES $TABLE_STORE($COLUMN_STORE_ID)
            )
        """
        db.execSQL(createIngredientTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INGREDIENT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STORE")
        onCreate(db)
    }
}
