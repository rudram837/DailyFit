package com.app.gym.Model

data class Meta(
    val total: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val nextCursor: String?
)