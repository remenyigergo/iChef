package com.example.ichef.clients.apis

import co.infinum.retromock.meta.Mock
import co.infinum.retromock.meta.MockResponse
import com.example.ichef.clients.models.Search.SearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @Mock
    @MockResponse(
        body = """
[
	{
		"recipe_name": "Goulash soup",
		"recipe_description": "A classic Hungarian soup dish.",
		"recipe_image": ""
	},
	{
		"recipe_name": "Langos",
		"recipe_description": "A fluffy donut like dough without the sugar.",
		"recipe_image": ""
	},
	{
		"recipe_name": "Stuffed Cabbage",
		"recipe_description": "Meat, rice, and cabbage but all in one pack.",
		"recipe_image": ""
	},
	{
		"recipe_name": "Vegetable soup",
		"recipe_description": "This is a traditional soup with a lots of vegetables in it.",
		"recipe_image": ""
	},
	{
		"recipe_name": "Grilled Cheese Sandwich",
		"recipe_description": "Perfectly toasted cheese sandwich.",
		"recipe_image": ""
	},
	{
		"recipe_name": "Caesar Salad",
		"recipe_description": "Crisp romaine lettuce with creamy dressing.",
		"recipe_image": ""
	},
	{
		"recipe_name": "Chocolate Cake",
		"recipe_description": "Rich and moist chocolate cake.",
		"recipe_image": ""
	},
	{
		"recipe_name": "Gin & Tonic",
		"recipe_description": "Clean and tasty beverage for summer nights.",
		"recipe_image": ""
	}
]
        """
    )
    @GET("/search")
    suspend fun search(@Query("title") title: String, @Query("page") page: Int, @Query("pageSize") pageSize: Int) : Response<SearchResult>

}