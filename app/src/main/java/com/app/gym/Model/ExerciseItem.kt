package com.app.gym.Model

data class ExerciseItem(
    val exerciseId: String,
    val name: String,
    val gifUrl: String,
    val bodyParts: List<String>,
    val equipments: List<String>,
    val targetMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val instructions: List<String>
)