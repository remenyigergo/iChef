import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ichef.R
import com.example.ichef.models.activities.more.MyRecipe

class RecipeAdapter(
    private val context: Context,
    private val myRecipeList: ArrayList<MyRecipe>
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val recipeDescription: TextView = itemView.findViewById(R.id.recipeDescription)
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = myRecipeList[position]
        holder.recipeTitle.text = recipe.title
        holder.recipeDescription.text = recipe.description

        Glide.with(context)
            .load(recipe.imageUrl) // Assuming your SearchRecipe model has an imageUrl field
            .placeholder(R.mipmap.ichef_foreground) // Image to show while loading
            .error(R.mipmap.error_image_foreground) // Image to show in case of error
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.recipeImage)
    }

    override fun getItemCount(): Int {
        return myRecipeList.size
    }
}
