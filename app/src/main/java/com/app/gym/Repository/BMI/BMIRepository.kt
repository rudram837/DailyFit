package com.app.gym.Repository.BMI

import android.annotation.SuppressLint
import com.app.gym.Model.BMI.BMIRequest
import com.app.gym.Model.BMI.BMIResult
import com.app.gym.Model.BMI.Gender


class BMIRepository {
    @SuppressLint("DefaultLocale")
    fun calculateBMI(request: BMIRequest): BMIResult {

        val heightM = request.height / 100f
        val bmi = request.weight / (heightM * heightM)

        val status = when {
            bmi < 18.5f -> "Underweight"
            bmi < 25f -> "Healthy"
            bmi < 30f -> "Overweight"
            else -> "Obese"
        }

        // Ideal weight range (BMI 18.5 - 24.9) based on user's actual height
        val idealMin = 18.5f * heightM * heightM
        val idealMax = 24.9f * heightM * heightM
        val idealWeight = String.format("%.1f - %.1f kg", idealMin, idealMax)

        // BMR via Mifflin-St Jeor formula, using actual gender/height/weight/age
        val bmr = if (request.gender == Gender.MALE) {
            (10 * request.weight) + (6.25f * request.height) - (5 * request.age) + 5
        } else {
            (10 * request.weight) + (6.25f * request.height) - (5 * request.age) - 161
        }

        // Moderate activity multiplier for maintenance calories
        val calories = (bmr * 1.55f).toInt()

        val workout = when (status) {
            "Underweight" -> "Strength Training (Muscle Gain Focus)"
            "Healthy" -> "Balanced Strength + Cardio"
            "Overweight" -> "Cardio + Full Body Strength Training"
            else -> "Low-Impact Cardio + Supervised Strength Training"
        }

        val diet = when (status) {
            "Underweight" -> "High Calorie, High Protein Diet"
            "Healthy" -> "Balanced Maintenance Diet"
            "Overweight" -> "Calorie Deficit, High Protein Diet"
            else -> "Structured Calorie Deficit Diet"
        }

        return BMIResult(
            bmi = bmi,
            status = status,
            idealWeight = idealWeight,
            calories = calories,
            workout = workout,
            diet = diet
        )
    }

    suspend fun saveBMI(result: BMIResult) {
        // Future: Room / Firebase
    }
}