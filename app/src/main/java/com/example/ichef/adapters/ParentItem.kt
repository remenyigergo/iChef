package com.example.ichef.adapters

data class ParentItem(
    val title: String,
    val children: MutableList<ChildItem>
)