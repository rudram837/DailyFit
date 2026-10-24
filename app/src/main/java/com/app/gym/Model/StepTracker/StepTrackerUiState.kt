package com.app.gym.Model.StepTracker

data class StepTrackerUiState(
    val steps: Int = 0,
    val goal: Int = 5_000,
    val progress: Int = 0,
    val remaining: Int = 5_000,
    val distanceKm: Double = 0.0,
    val calories: Int = 0,
    val activeTime: String = "—",
    val sensorAvailable: Boolean = true
)