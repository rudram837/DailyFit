package com.app.gym.Model.BMI

data class BMIRequest(
    val gender: Gender,
    val height: Float,
    val weight: Float,
    val age: Int
)

