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
    {
        "results": [
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
        ],
        "page": 1,
        "totalPage": 2
    }
        """
    )
    @GET("/search")
    suspend fun searchPage1(@Query("title") title: String, @Query("page") page: Int, @Query("pageSize") pageSize: Int) : Response<SearchResult>

    @Mock
    @MockResponse(
        body = """
    {
        "results": [
            {
                "recipe_name": "Spaghetti Carbonara",
                "recipe_description": "A classic Italian pasta dish made with eggs, cheese, pancetta, and black pepper for a creamy, savory flavor.",
                "recipe_image": ""
            },
            {
                "recipe_name": "Chicken Tikka Masala",
                "recipe_description": "Tender chicken marinated in yogurt and spices, cooked in a rich, creamy tomato-based sauce.",
                "recipe_image": ""
            },
            {
                "recipe_name": "French Onion Soup",
                "recipe_description": "A deeply flavorful soup made with caramelized onions, beef broth, and topped with melted cheese and toasted bread.",
                "recipe_image": ""
            },
            {
                "recipe_name": "Beef Stroganoff",
                "recipe_description": "A comforting Russian dish featuring sautéed beef strips in a creamy mushroom sauce, served over noodles or rice.",
                "recipe_image": ""
            },
            {
                "recipe_name": "Caprese Salad",
                "recipe_description": "A refreshing Italian salad made with ripe tomatoes, fresh mozzarella, basil, olive oil, and balsamic glaze.",
                "recipe_image": ""
            },
            {
                "recipe_name": "Tiramisu",
                "recipe_description": "A decadent Italian dessert with layers of coffee-soaked ladyfingers, mascarpone cheese, and cocoa powder.",
                "recipe_image": ""
            }
        ],
        "page": 2,
        "totalPage": 2
    }
        """
    )
    @GET("/search")
    suspend fun searchPage2(@Query("title") title: String, @Query("page") page: Int, @Query("pageSize") pageSize: Int) : Response<SearchResult>
}