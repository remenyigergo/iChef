package com.example.ichef.clients.apis

import co.infinum.retromock.meta.Mock
import co.infinum.retromock.meta.MockResponse
import com.example.ichef.clients.models.ShoppingList.ShoppingListResultItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ShoppingListApi {

    @Mock
    @MockResponse(
        body = """
    [
	{
		"storeName": "Lidl",
		"ingredients": [
			{
				"ingredientName": "Kurkuma",
				"isChecked": false
			},
			{
				"ingredientName": "Olaj",
				"isChecked": true
			}
		]
	},
	{
		"storeName": "Aldi",
		"ingredients": [
			{
				"ingredientName": "Paradicsom",
				"isChecked": true
			},
			{
				"ingredientName": "Só",
				"isChecked": true
			}
		]
	}
]
        """
    )
    @GET("/shoppinglist")
    suspend fun getShoppingList(@Query("userId") userId: Long) : Response<ArrayList<ShoppingListResultItem>>

}