package com.example.ichef.database.interfaces

interface AddParentChildDataManager {
    fun insertOrUpdateParent(name: String, counter: Int)
    fun getTop3FavoriteParents(): List<String>

}