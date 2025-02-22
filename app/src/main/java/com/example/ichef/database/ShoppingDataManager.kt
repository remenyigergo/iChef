package com.example.ichef.database

import android.content.Context
import android.content.ContentValues
import com.example.ichef.models.IngredientCheckbox
import com.example.ichef.models.StoreCheckBox
import javax.inject.Inject

class ShoppingDataManager @Inject constructor(context: Context) {

    private val dbHelper = DbHelper(context)

    // Insert a store
    fun insertStore(storeName: String): Long {
        val db = dbHelper.writableDatabase

        // Check if the store already exists
        val cursor = db.query(
            DbHelper.TABLE_STORE,
            arrayOf(DbHelper.COLUMN_STORE_ID),
            "${DbHelper.COLUMN_STORE_NAME} = ?",
            arrayOf(storeName),
            null, null, null
        )

        // If the store already exists, return -1 (or any other indication you prefer)
        if (cursor.moveToFirst()) {
            cursor.close() // Don't forget to close the cursor
            return -1 // Indicating that the store already exists
        }
        cursor.close()

        // If the store doesn't exist, insert the store
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

    // update a store with a new ingredient
    fun storeNewIngredientsInStore(storeName: String, ingredients: List<String>) {
        val db = dbHelper.writableDatabase

        // Query the store ID based on the store name
        val cursor = db.query(
            DbHelper.TABLE_STORE,
            arrayOf(DbHelper.COLUMN_STORE_ID),
            "${DbHelper.COLUMN_STORE_NAME} = ?",
            arrayOf(storeName),
            null, null, null
        )

        var storeId: Long? = null
        if (cursor.moveToFirst()) {
            storeId = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_STORE_ID))
        }
        cursor.close()

        if (storeId != null) {
            // Store exists, insert the list of ingredients
            db.beginTransaction()
            try {
                for (ingredient in ingredients) {
                    // Check if the ingredient already exists
                    val ingredientCursor = db.query(
                        DbHelper.TABLE_INGREDIENT,
                        arrayOf(DbHelper.COLUMN_INGREDIENT_ID),
                        "${DbHelper.COLUMN_INGREDIENT_NAME} = ? AND ${DbHelper.COLUMN_INGREDIENT_STORE_ID} = ?",
                        arrayOf(ingredient, storeId.toString()),
                        null, null, null
                    )

                    if (!ingredientCursor.moveToFirst()) {
                        // Ingredient does not exist, so insert it
                        val values = ContentValues().apply {
                            put(DbHelper.COLUMN_INGREDIENT_NAME, ingredient)
                            put(DbHelper.COLUMN_INGREDIENT_CHECKED, 0) // Default to unchecked
                            put(DbHelper.COLUMN_INGREDIENT_STORE_ID, storeId)
                        }
                        db.insert(DbHelper.TABLE_INGREDIENT, null, values)
                    }

                    ingredientCursor.close()
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } else {
            // Store does not exist, handle this scenario
            throw IllegalArgumentException("Store with name $storeName does not exist.")
        }
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

    fun deleteStoreWithIngredientsByName(storeName: String) {
        val db = dbHelper.writableDatabase

        // Start a transaction to ensure both deletions occur together
        db.beginTransaction()
        try {
            // Get the store ID based on the store name
            val cursor = db.query(
                DbHelper.TABLE_STORE,
                arrayOf(DbHelper.COLUMN_STORE_ID),
                "${DbHelper.COLUMN_STORE_NAME} = ?",
                arrayOf(storeName),
                null,
                null,
                null
            )

            var storeId: Long? = null
            if (cursor.moveToFirst()) {
                storeId = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_STORE_ID))
            }
            cursor.close()

            if (storeId != null) {
                // Delete all ingredients associated with the store
                db.delete(
                    DbHelper.TABLE_INGREDIENT,
                    "${DbHelper.COLUMN_INGREDIENT_STORE_ID} = ?",
                    arrayOf(storeId.toString())
                )

                // Delete the store itself
                db.delete(
                    DbHelper.TABLE_STORE,
                    "${DbHelper.COLUMN_STORE_NAME} = ?",
                    arrayOf(storeName)
                )
            }

            // Mark transaction as successful
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteIngredientFromStore(storeName: String, ingredientName: String) {
        val db = dbHelper.writableDatabase

        // Query the store ID based on the store name
        val cursor = db.query(
            DbHelper.TABLE_STORE,
            arrayOf(DbHelper.COLUMN_STORE_ID),
            "${DbHelper.COLUMN_STORE_NAME} = ?",
            arrayOf(storeName),
            null, null, null
        )

        var storeId: Long? = null
        if (cursor.moveToFirst()) {
            storeId = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_STORE_ID))
        }
        cursor.close()

        if (storeId != null) {
            // Delete the ingredient associated with the store
            val rowsDeleted = db.delete(
                DbHelper.TABLE_INGREDIENT,
                "${DbHelper.COLUMN_INGREDIENT_STORE_ID} = ? AND ${DbHelper.COLUMN_INGREDIENT_NAME} = ?",
                arrayOf(storeId.toString(), ingredientName)
            )

            if (rowsDeleted == 0) {
                throw IllegalArgumentException("Ingredient '$ingredientName' not found in store '$storeName'.")
            }
        } else {
            throw IllegalArgumentException("Store with name '$storeName' does not exist.")
        }
    }


}
