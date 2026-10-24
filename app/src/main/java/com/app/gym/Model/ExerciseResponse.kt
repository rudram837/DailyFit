package com.app.gym.Model

data class ExerciseResponse(
    val success: Boolean,
    val meta: Meta,
    val data: List<ExerciseItem>
)

