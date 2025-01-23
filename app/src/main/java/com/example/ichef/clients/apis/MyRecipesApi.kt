package com.example.ichef.clients.apis

import co.infinum.retromock.meta.Mock
import co.infinum.retromock.meta.MockResponse
import com.example.ichef.clients.models.MyRecipes.MyRecipeResult
import com.example.ichef.clients.models.MyRecipes.MyRecipesResultItem
import com.example.ichef.models.activities.more.Recipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MyRecipesApi {

    @Mock
    @MockResponse(
        body = """
[
	{
		"title": "Goulash soup",
		"description": "A classic Hungarian soup dish.",
		"imageResId": ""
	},
	{
		"title": "Langos",
		"description": "A fluffy donut like dough without the sugar.",
		"imageResId": ""
	},
	{
		"title": "Stuffed Cabbage",
		"description": "Meat, rice, and cabbage but all in one pack.",
		"imageResId": ""
	},
	{
		"title": "Vegetable soup",
		"description": "This is a traditional soup with a lots of vegetables in it.",
		"imageResId": ""
	},
	{
		"title": "Grilled Cheese Sandwich",
		"description": "Perfectly toasted cheese sandwich.",
		"imageResId": ""
	},
	{
		"title": "Caesar Salad",
		"description": "Crisp romaine lettuce with creamy dressing.",
		"imageResId": ""
	},
	{
		"title": "Chocolate Cake",
		"description": "Rich and moist chocolate cake.",
		"imageResId": ""
	},
	{
		"title": "Gin & Tonic",
		"description": "Clean and tasty beverage for summer nights.",
		"imageResId": ""
	}
]
        """
    )
    @GET("/myrecipes")
    suspend fun getMyRecipes(@Query("userId") userId: Long) : Response<ArrayList<Recipe>> //Recipe is not a swagger object at the moment. When real client is introduced, this needs to be mapped to BLA class

}