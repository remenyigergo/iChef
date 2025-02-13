package com.example.ichef.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R
import com.example.ichef.models.activities.search.SearchRecipe

class SearchAdapter(
    private val context: Context,
    private val searchResultRecipes: ArrayList<SearchRecipe>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW = 1
    private val LOADING_VIEW = 2
    private var isLoadingAdded = false

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
            holder.recipeImage.setImageResource(R.mipmap.jenkins_foreground) // HARDCODED FOR NOW
        }
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
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }
}
