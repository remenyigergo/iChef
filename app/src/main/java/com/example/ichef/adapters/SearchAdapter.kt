package com.example.ichef.adapters

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ichef.R
//import com.example.ichef.models.activities.recipe.RecipeDetailActivity
import com.example.ichef.models.activities.search.SearchRecipe
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent
import com.google.android.material.button.MaterialButton

class SearchAdapter(
    private val context: Context,
    private val searchResultRecipes: ArrayList<SearchRecipe>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW = 1
    private val LOADING_VIEW = 2
    private var isLoadingAdded = false

    // Track expanded state
    private val expandedItems = mutableSetOf<Int>()

    override fun getItemViewType(position: Int): Int {
        return if (position == searchResultRecipes.size) LOADING_VIEW else ITEM_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW) {
            val view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false)
            RecipeViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecipeViewHolder) {
            val recipe = searchResultRecipes[position]

            holder.recipeTitle.text = recipe.title
            holder.recipeDescription.text = recipe.description

            // Load image using Glide
            Glide.with(context)
                .load(recipe.imageUrl)
                .placeholder(R.mipmap.ichef_foreground)
                .error(R.mipmap.error_image_foreground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.recipeImage)

            // Prevent duplicate views
            holder.missingIngredientsLayout.removeAllViews()

            // Missing ingredients to each card
            val ingredientsLayoutView =  AddMissingIngredientsToCard(arrayListOf("Kunpri", "Szöllő","Paradicsom","Cukor","Olaj"))
            holder.missingIngredientsLayout.addView(ingredientsLayoutView)

            // Handle expansion
            var isExpanded = expandedItems.contains(position)
            holder.missingIngredientsLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

            holder.itemView.setOnClickListener {
                if (isExpanded) {
                    expandedItems.remove(position)
                    holder.missingIngredientsLayout.visibility = View.GONE
                    isExpanded = false
                } else {
                    expandedItems.add(position)
                    holder.missingIngredientsLayout.visibility = View.VISIBLE
                    isExpanded = true
                }
            }

        }
    }

    private fun AddMissingIngredientsToCard(missingIngredients: ArrayList<String>): FlexboxLayout {
        val missingIngredientsLayoutView = FlexboxLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            flexWrap = FlexWrap.WRAP // Enable wrapping to next row
            justifyContent = JustifyContent.FLEX_START // Align items properly
            setPadding(8, 8, 8, 8)
        }

        for (ingredient in missingIngredients) {
            // Create Red X ImageView
            val redXIcon = ImageView(context).apply {
                setImageResource(android.R.drawable.ic_delete)
                layoutParams = LinearLayout.LayoutParams(60, 60).apply {
                    setMargins(0, 0, 8, 0)
                }
            }

            // Create Missing Ingredient TextView
            val textView = TextView(context).apply {
                text = ingredient
                setTextColor(Color.GRAY)
                textSize = 14f
                setPadding(0, 0, 16, 0)
            }

            // Create a container for each item
            val itemContainer = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
                addView(redXIcon)
                addView(textView)
            }

            // Add the container to the FlexboxLayout
            missingIngredientsLayoutView.addView(itemContainer)
        }

        // Create a horizontal layout for the More button
        val buttonLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER // Align the button to the right
        }

        // Dynamically add "More" button
        val moreButton = MaterialButton(context).apply {
            text = "More"
            textSize = 16f
            setPadding(16, 8, 16, 8)
            setBackgroundColor(ContextCompat.getColor(context, R.color.topbar)) // Set background to light gray

            layoutParams = LinearLayout.LayoutParams(
                (context.resources.displayMetrics.widthPixels * 0.85).toInt(), // 90% of screen width
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 0)
            }
        }

        // Handle "More" button click
        moreButton.setOnClickListener {
            // val intent = Intent(context, RecipeDetailActivity::class.java)
            // intent.putExtra("recipe_id", recipe.id)
            // context.startActivity(intent)
        }

        // Add the "More" button inside the right-aligned layout
        buttonLayout.addView(moreButton)

        // Add the button layout to the Flexbox container
        missingIngredientsLayoutView.addView(buttonLayout)

        return missingIngredientsLayoutView
    }

    override fun getItemCount(): Int {
        return searchResultRecipes.size + if (isLoadingAdded) 1 else 0
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        notifyItemInserted(searchResultRecipes.size)
    }

    fun removeLoadingFooter() {
        if (isLoadingAdded) {
            isLoadingAdded = false
            notifyItemRemoved(searchResultRecipes.size)
        }
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val recipeDescription: TextView = itemView.findViewById(R.id.recipeDescription)
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val missingIngredientsLayout: LinearLayout = itemView.findViewById(R.id.missingIngredientsLayout)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }
}
