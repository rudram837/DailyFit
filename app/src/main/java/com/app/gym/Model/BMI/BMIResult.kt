package com.app.gym.Model.BMI

data class BMIResult(
    val bmi: Float,
    val status: String,
    val idealWeight: String,
    val calories: Int,
    val workout: String,
    val diet: String
)