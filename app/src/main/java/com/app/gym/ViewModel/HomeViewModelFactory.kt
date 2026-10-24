package com.app.gym.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.gym.Repository.HomeRepository

class HomeViewModelFactory(
    private val repository: HomeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")

    }

}