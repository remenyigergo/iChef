package com.example.ichef.database

import AddParentChildDataManagerHelper
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.ichef.database.interfaces.AddParentChildDataManager
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class AddParentChildDataManagerImpl @Inject constructor(@ActivityContext context: Context) : AddParentChildDataManager {

    private val dbHelper = AddParentChildDataManagerHelper(context)

    // Insert or Update a parent in the database
    override fun insertOrUpdateParent(name: String, counter: Int) {
        val db = dbHelper.writableDatabase

        // Check if the parent already exists
        val cursor: Cursor = db.query(
            AddParentChildDataManagerHelper.TABLE_NAME,
            arrayOf(AddParentChildDataManagerHelper.COLUMN_NAME, AddParentChildDataManagerHelper.COLUMN_COUNTER),
            "${AddParentChildDataManagerHelper.COLUMN_NAME} = ?",
            arrayOf(name),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            // Parent exists, update the counter
            val colIndex = cursor.getColumnIndex(AddParentChildDataManagerHelper.COLUMN_COUNTER)

            if (colIndex != -1) {
                val currentCounter = cursor.getInt(colIndex)
                val updatedCounter = currentCounter + counter

                Log.i("AddParentChildDataManager","CurrentCounter is: $currentCounter")
                Log.i("AddParentChildDataManager","UpdatedCounter is: $updatedCounter")

                val values = ContentValues().apply {
                    put(AddParentChildDataManagerHelper.COLUMN_COUNTER, updatedCounter)
                }
                Log.i("AddParentChildDataManager","values is: $values")

                var rowsUpdated = db.update(AddParentChildDataManagerHelper.TABLE_NAME, values, "${AddParentChildDataManagerHelper.COLUMN_NAME} = ?", arrayOf(name))
                Log.i("AddParentChildDataManager","Rows updated: $rowsUpdated")
            }
        } else {
            // Parent doesn't exist, insert a new one
            Log.i("AddParentChildDataManager","Am I here?")

            val values = ContentValues().apply {
                put(AddParentChildDataManagerHelper.COLUMN_NAME, name)
                put(AddParentChildDataManagerHelper.COLUMN_COUNTER, counter)
            }
            db.insert(AddParentChildDataManagerHelper.TABLE_NAME, null, values)
        }
        cursor.close()
    }

    // Retrieve top 3 parents by counter
    override fun getTop3FavoriteParents(): List<String> {
        val db = dbHelper.readableDatabase
        val topParents = mutableListOf<String>()

        val cursor: Cursor = db.query(
            AddParentChildDataManagerHelper.TABLE_NAME,
            arrayOf(AddParentChildDataManagerHelper.COLUMN_NAME),
            null,
            null,
            null,
            null,
            "${AddParentChildDataManagerHelper.COLUMN_COUNTER} DESC",
            "3"
        )

        while (cursor.moveToNext()) {
            Log.i("AddParentChildDataManager","GetResult is: $cursor")

            val colIndex = cursor.getColumnIndex(AddParentChildDataManagerHelper.COLUMN_NAME)
            if (colIndex != -1) {
                val name = cursor.getString(colIndex)
                topParents.add(name)
            }
        }
        cursor.close()
        return topParents
    }
}