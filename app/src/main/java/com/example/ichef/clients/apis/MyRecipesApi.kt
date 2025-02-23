package com.example.ichef.clients.apis

import co.infinum.retromock.meta.Mock
import co.infinum.retromock.meta.MockResponse
import com.example.ichef.clients.models.MyRecipes.MyRecipeResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MyRecipesApi {

    @Mock
    @MockResponse(
        body = """
[
        {
            "recipe_name": "Goulash soup",
            "recipe_description": "A classic Hungarian soup dish.",
            "recipe_image": "https://image-api.nosalty.hu/nosalty/images/recipes/II/C7/alfoldi-gulyas.jpeg?w=1340&fit=crop&fm=webp&crop=670%2C460%2C%2C&h=920&mark=watermark.png&markpos=bottom-left&markpad=20&markw=&s=b7dbdd67f0fe7d11c27fc6a3f9cee95b"
        },
        {
            "recipe_name": "Langos",
            "recipe_description": "A fluffy donut like dough without the sugar.",
            "recipe_image": "https://image-api.nosalty.hu/nosalty/images/recipes/VR/Tk/vizes-langos.jpeg?w=1340&fit=crop&fm=webp&crop=3994%2C3001%2C28%2C16&h=920&mark=watermark.png&markpos=bottom-left&markpad=20&markw=&s=95e8933e6a79c3d540541c275a761fac"
        },
        {
            "recipe_name": "Stuffed Cabbage",
            "recipe_description": "Meat, rice, and cabbage but all in one pack.",
            "recipe_image": "https://image-api.nosalty.hu/nosalty/images/recipes/8T/wE/toltott-kaposzta-4.jpeg?w=1340&fit=crop&fm=webp&crop=0%2C0%2C0%2C0&h=920&mark=watermark.png&markpos=bottom-left&markpad=20&markw=&s=900b05a41a01ee8c1d79576a5ec5347b"
        },
        {
            "recipe_name": "Vegetable soup",
            "recipe_description": "This is a traditional soup with a lots of vegetables in it.",
            "recipe_image": "https://image-api.nosalty.hu/nosalty/images/recipes/RD/Ko/nagymamam-kulonleges-huslevese.jpeg?w=1340&fit=crop&fm=webp&crop=1024%2C703%2C%2C&h=920&mark=watermark.png&markpos=bottom-left&markpad=20&markw=&s=b175f01accb293ce7fcc020a8c165a9d"
        },
        {
            "recipe_name": "Grilled Cheese Sandwich",
            "recipe_description": "Perfectly toasted cheese sandwich.",
            "recipe_image": "https://image-api.nosalty.hu/nosalty/images/recipes/Oe/ba/snoop-dogg-kedvenc-szendvicse.jpg?w=1340&fit=crop&fm=webp&crop=5445%2C3623%2C23%2C23&h=920&mark=watermark.png&markpos=bottom-left&markpad=20&markw=&s=6aa52d7b0ab2339ef46fbb00bdfba841"
        },
        {
            "recipe_name": "Caesar Salad",
            "recipe_description": "Crisp romaine lettuce with creamy dressing.",
            "recipe_image": "https://image-api.nosalty.hu/nosalty/images/recipes/tY/Jf/cezar-tesztasalata-zoldfuszeres-olajjal.jpg?w=670&fit=crop&fm=webp&crop=2048%2C1363%2C0%2C2&h=460&mark=watermark.png&markpos=bottom-left&markpad=20&markw=&s=6ee1c2e1330ae95be514f883d59b0762"
        },
        {
            "recipe_name": "Chocolate Cake",
            "recipe_description": "Rich and moist chocolate cake.",
            "recipe_image": "https://image-api.nosalty.hu/nosalty/images/recipes/N3/ny/legegyszerubb-csokitorta.jpg?w=670&fit=crop&fm=webp&crop=2037%2C1358%2C5%2C6&h=460&mark=watermark.png&markpos=bottom-left&markpad=20&markw=&s=7431c8c1f49669885e6897348f2391ff"
        },
        {
            "recipe_name": "Gin & Tonic",
            "recipe_description": "Clean and tasty beverage for summer nights.",
            "recipe_image": "https://image-api.nosalty.hu/nosalty/images/recipes/eT/Os/bodzas-gin-tonic.jpeg?w=1340&fit=crop&fm=webp&crop=1631%2C1120%2C%2C&h=920&mark=watermark.png&markpos=bottom-left&markpad=20&markw=&s=760381f9ec18669f6120394cda37a797"
        }
]
        """
    )
    @GET("/myrecipes")
    suspend fun getMyRecipes(@Query("userId") userId: Long) : Response<MyRecipeResult>

}