package com.app.gym.Model

data class CategoryResponse (
    val success: Boolean,
    val data: List<CategoryItem>
)

data class CategoryItem(
    val name: String
)