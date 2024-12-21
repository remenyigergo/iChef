package com.example.ichef.database

import android.content.Context
import android.content.ContentValues
import com.example.ichef.models.IngredientCheckbox
import com.example.ichef.models.StoreCheckBox

class ShoppingDataManager(context: Context) {

    private val dbHelper = DbHelper(context)

    // Insert a store
    fun insertStore(storeName: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbHelper.COLUMN_STORE_NAME, storeName)
        }
        return db.insert(DbHelper.TABLE_STORE, null, values)
    }

    // Insert an ingredient
    fun insertIngredient(storeId: Long, ingredientName: String, isChecked: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbHelper.COLUMN_INGREDIENT_NAME, ingredientName)
            put(DbHelper.COLUMN_INGREDIENT_CHECKED, if (isChecked) 1 else 0)
            put(DbHelper.COLUMN_INGREDIENT_STORE_ID, storeId)
        }
        db.insert(DbHelper.TABLE_INGREDIENT, null, values)
    }

    // Retrieve all stores with ingredients
    fun getStores(): List<StoreCheckBox> {
        val db = dbHelper.readableDatabase
        val stores = mutableListOf<StoreCheckBox>()

        val storeCursor = db.query(
            DbHelper.TABLE_STORE,
            null, null, null, null, null, null
        )

        while (storeCursor.moveToNext()) {
            val storeId = storeCursor.getLong(storeCursor.getColumnIndexOrThrow(DbHelper.COLUMN_STORE_ID))
            val storeName = storeCursor.getString(storeCursor.getColumnIndexOrThrow(DbHelper.COLUMN_STORE_NAME))

            val ingredients = getIngredientsForStore(storeId)
            stores.add(StoreCheckBox(storeName, ingredients))
        }
        storeCursor.close()

        return stores
    }

    // Retrieve ingredients for a store
    private fun getIngredientsForStore(storeId: Long): MutableList<IngredientCheckbox> {
        val db = dbHelper.readableDatabase
        val ingredients = mutableListOf<IngredientCheckbox>()

        val cursor = db.query(
            DbHelper.TABLE_INGREDIENT,
            null,
            "${DbHelper.COLUMN_INGREDIENT_STORE_ID} = ?",
            arrayOf(storeId.toString()),
            null, null, null
        )

        while (cursor.moveToNext()) {
            val ingredientName = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_INGREDIENT_NAME))
            val isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_INGREDIENT_CHECKED)) == 1
            ingredients.add(IngredientCheckbox(ingredientName, isChecked))
        }
        cursor.close()

        return ingredients
    }

    // Update an ingredient's checked state
    fun updateIngredientChecked(ingredientId: Long, isChecked: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbHelper.COLUMN_INGREDIENT_CHECKED, if (isChecked) 1 else 0)
        }
        db.update(
            DbHelper.TABLE_INGREDIENT,
            values,
            "${DbHelper.COLUMN_INGREDIENT_ID} = ?",
            arrayOf(ingredientId.toString())
        )
    }

    // Delete a store and its ingredients
    fun deleteStore(storeId: Long) {
        val db = dbHelper.writableDatabase
        db.delete(DbHelper.TABLE_INGREDIENT, "${DbHelper.COLUMN_INGREDIENT_STORE_ID} = ?", arrayOf(storeId.toString()))
        db.delete(DbHelper.TABLE_STORE, "${DbHelper.COLUMN_STORE_ID} = ?", arrayOf(storeId.toString()))
    }
}
