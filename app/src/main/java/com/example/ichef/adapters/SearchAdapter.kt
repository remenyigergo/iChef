package com.example.ichef.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R
import com.example.ichef.models.activities.search.SearchRecipe

class SearchAdapter(
    private val context: Context,
    private val searchResultRecipes: ArrayList<SearchRecipe>
) : RecyclerView.Adapter<SearchAdapter.SearchResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false)
        return SearchResultViewHolder(view)
    }

    inner class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val recipeDescription: TextView = itemView.findViewById(R.id.recipeDescription)
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val recipe = searchResultRecipes[position]
        holder.recipeTitle.text = recipe.title
        holder.recipeDescription.text = recipe.description
        holder.recipeImage.setImageResource(R.mipmap.jenkins_foreground) //HARDCODED FOR NOW
    }

    override fun getItemCount(): Int {
        return searchResultRecipes.size
    }
}