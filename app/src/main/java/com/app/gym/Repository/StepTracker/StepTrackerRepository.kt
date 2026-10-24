package com.app.gym.Repository.StepTracker

interface StepTrackerRepository {

    suspend fun getTodaySteps(): Int

    suspend fun saveTodaySteps(steps: Int)

    suspend fun getBaseline(): Long

    suspend fun saveBaseline(
        date: String,
        baseline: Long
    )

    suspend fun getBaselineDate(): String?

    suspend fun getStepGoal(): Int

    suspend fun saveStepGoal(goal: Int)
}