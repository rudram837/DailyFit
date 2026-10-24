package com.app.gym.ViewModel.StepTracker


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.gym.Repository.StepTracker.StepTrackerLocalRepository

class StepTrackerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                StepTrackerViewModel::class.java
            )
        ) {

            val repository =
                StepTrackerLocalRepository(
                    context.applicationContext
                )

            @Suppress("UNCHECKED_CAST")
            return StepTrackerViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}