package com.app.gym.Repository.StepTracker


import android.content.Context

class StepTrackerLocalRepository(
    context: Context
) : StepTrackerRepository {

    private val prefs = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override suspend fun getTodaySteps(): Int {
        return prefs.getInt(KEY_TODAY_STEPS, 0)
    }

    override suspend fun saveTodaySteps(steps: Int) {
        prefs.edit()
            .putInt(KEY_TODAY_STEPS, steps)
            .apply()
    }

    override suspend fun getBaseline(): Long {
        return prefs.getLong(KEY_STEP_BASELINE, -1L)
    }

    override suspend fun saveBaseline(
        date: String,
        baseline: Long
    ) {
        prefs.edit()
            .putString(KEY_BASELINE_DATE, date)
            .putLong(KEY_STEP_BASELINE, baseline)
            .apply()
    }

    override suspend fun getBaselineDate(): String? {
        return prefs.getString(KEY_BASELINE_DATE, null)
    }

    override suspend fun getStepGoal(): Int {
        return prefs.getInt(KEY_STEP_GOAL, DEFAULT_STEP_GOAL)
    }

    override suspend fun saveStepGoal(goal: Int) {
        prefs.edit()
            .putInt(KEY_STEP_GOAL, goal)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "step_tracker_prefs"

        private const val KEY_STEP_BASELINE = "step_baseline"
        private const val KEY_BASELINE_DATE = "baseline_date"
        private const val KEY_TODAY_STEPS = "today_steps"
        private const val KEY_STEP_GOAL = "step_goal"

//        private const val DEFAULT_STEP_GOAL = 20
        private const val DEFAULT_STEP_GOAL = 5_000
        private const val DEFAULT_CALORIE_GOAL = 150
    }
}