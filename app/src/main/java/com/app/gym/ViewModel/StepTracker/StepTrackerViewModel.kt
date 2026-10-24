package com.app.gym.ViewModel.StepTracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gym.Model.StepTracker.StepTrackerUiState
import com.app.gym.Repository.StepTracker.StepTrackerRepository
import com.app.gym.Utils.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StepTrackerViewModel(
    private val repository: StepTrackerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        StepTrackerUiState()
    )

    val uiState: StateFlow<StepTrackerUiState> =
        _uiState.asStateFlow()


    /*
     * One-time UI event.
     *
     * Emits the goal value when the user actually
     * crosses the daily goal.
     */
    private val _goalCelebration =
        MutableSharedFlow<Long>(
            replay = 0,
            extraBufferCapacity = 1
        )

    val goalCelebration: SharedFlow<Long> =
        _goalCelebration.asSharedFlow()


    /*
     * Used only for detecting:
     *
     * previousSteps < goal
     * currentSteps >= goal
     *
     * These are ViewModel-level values, so the Fragment
     * does not contain business logic for goal completion.
     */
    private var previousSteps: Int? = null
    private var previousGoal: Int? = null

    /*
     * Prevents the same goal celebration from firing
     * repeatedly after the goal has been crossed.
     */
    private var goalCelebrationTriggered = false

    /*
     * Used to reset the transition detector when a
     * new calendar day starts.
     */
    private var trackedDate: String? = null


    fun loadSavedData() {

        viewModelScope.launch(Dispatchers.IO) {

            val todayKey =
                Utility.getTodayKey()

            /*
             * New day.
             *
             * Reset only the goal-crossing detector.
             * Do NOT emit a celebration here.
             */
            if (trackedDate != todayKey) {

                trackedDate = todayKey

                previousSteps = null
                previousGoal = null
                goalCelebrationTriggered = false
            }

            val steps =
                repository.getTodaySteps()

            val goal =
                repository.getStepGoal()

            calculateAndPublish(
                steps = steps,
                goal = goal
            )

            /*
             * IMPORTANT:
             *
             * loadSavedData() must NOT trigger celebration.
             *
             * If the user already completed today's goal
             * before opening the screen, we simply initialise
             * the detector with the current value.
             */
            previousSteps = steps
            previousGoal = goal

            if (steps >= goal) {
                goalCelebrationTriggered = true
            }
        }
    }


    fun onSensorSteps(
        currentSensorSteps: Long
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            val todayKey =
                Utility.getTodayKey()

            /*
             * Detect a new calendar day.
             */
            if (trackedDate != todayKey) {

                trackedDate = todayKey

                previousSteps = null
                previousGoal = null
                goalCelebrationTriggered = false
            }

            val savedDate =
                repository.getBaselineDate()

            var baseline =
                repository.getBaseline()


            /*
             * First sensor reading of the day.
             */
            if (
                savedDate != todayKey ||
                baseline == -1L
            ) {

                baseline = currentSensorSteps

                repository.saveBaseline(
                    date = todayKey,
                    baseline = baseline
                )

                /*
                 * New day starts at zero.
                 */
                previousSteps = 0
                previousGoal = null
                goalCelebrationTriggered = false
            }


            val todaySteps =
                (currentSensorSteps - baseline)
                    .coerceAtLeast(0L)
                    .coerceAtMost(
                        Int.MAX_VALUE.toLong()
                    )
                    .toInt()


            repository.saveTodaySteps(
                todaySteps
            )


            val goal =
                repository.getStepGoal()


            /*
             * Goal-crossing detection happens BEFORE
             * publishing the new UI state.
             */
            checkGoalCelebration(
                currentSteps = todaySteps,
                currentGoal = goal
            )


            calculateAndPublish(
                steps = todaySteps,
                goal = goal
            )
        }
    }


    private fun checkGoalCelebration(
        currentSteps: Int,
        currentGoal: Int
    ) {

        /*
         * Goal changed.
         */
        if (previousGoal != currentGoal) {

            /*
             * If this is the first value we know about,
             * simply initialise.
             */
            if (previousGoal == null) {

                previousSteps = currentSteps
                previousGoal = currentGoal

                /*
                 * If already completed when first observed,
                 * don't celebrate.
                 */
                if (currentSteps >= currentGoal) {
                    goalCelebrationTriggered = true
                }

                return
            }

            /*
             * A genuinely new goal was set.
             */
            goalCelebrationTriggered = false

            previousGoal = currentGoal
        }


        val oldSteps =
            previousSteps ?: run {

                previousSteps = currentSteps
                return
            }


        /*
         * THE IMPORTANT CONDITION:
         *
         * 9998 -> 10000  = celebrate
         * 10000 -> 10001 = don't celebrate
         * 10001 -> 10002 = don't celebrate
         */
        val crossedGoal =
            oldSteps < currentGoal &&
                    currentSteps >= currentGoal


        if (
            crossedGoal &&
            !goalCelebrationTriggered
        ) {

            goalCelebrationTriggered = true

            /*
             * SharedFlow emission is lightweight and safe
             * from this IO coroutine.
             */
            _goalCelebration.tryEmit(
                currentGoal.toLong()
            )
        }


        previousSteps = currentSteps
        previousGoal = currentGoal
    }


    fun saveStepGoal(goal: Int) {

        if (goal <= 0) return

        viewModelScope.launch(Dispatchers.IO) {

            repository.saveStepGoal(goal)

            val steps =
                repository.getTodaySteps()

            /*
             * Update detector for the new goal.
             *
             * If the user changes the goal to a value that is
             * already below their current steps, we don't fake
             * a "crossing" because there was no step transition.
             */
            previousGoal = goal

            if (steps >= goal) {
                goalCelebrationTriggered = true
            } else {
                goalCelebrationTriggered = false
            }

            previousSteps = steps

            calculateAndPublish(
                steps = steps,
                goal = goal
            )
        }
    }


    private fun calculateAndPublish(
        steps: Int,
        goal: Int
    ) {

        val progress =
            Utility.calculateProgress(
                steps,
                goal
            )

        val remaining =
            Utility.getRemainingSteps(
                steps,
                goal
            )


        /*
         * Temporary profile values.
         * Later these can come from user profile.
         */
        val userHeightCm = 175f
        val userWeightKg = 80f


        val distanceKm =
            Utility.calculateDistanceKm(
                steps,
                userHeightCm
            )


        val calories =
            Utility.calculateCalories(
                steps,
                userWeightKg
            )


        _uiState.value =
            StepTrackerUiState(
                steps = steps,
                goal = goal,
                progress = progress,
                remaining = remaining,
                distanceKm = distanceKm,
                calories = calories,
                activeTime = "—",
                sensorAvailable = true
            )
    }


    fun setSensorUnavailable() {

        /*
         * StateFlow value assignment itself doesn't require
         * Main thread. Keep this lightweight operation here.
         */
        _uiState.value =
            _uiState.value.copy(
                sensorAvailable = false
            )
    }
}