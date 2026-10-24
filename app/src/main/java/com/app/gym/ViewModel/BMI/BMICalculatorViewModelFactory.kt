package com.app.gym.ViewModel.BMI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.gym.Repository.BMI.BMIRepository

class BMICalculatorViewModelFactory(
    private val repository: BMIRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BMICalculatorViewModel(repository) as T
    }
}