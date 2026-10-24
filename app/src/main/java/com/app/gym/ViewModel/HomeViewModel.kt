package com.app.gym.ViewModel

import androidx.lifecycle.*
import com.app.gym.Model.CategoryResponse
import com.app.gym.Model.ExerciseResponse
import com.app.gym.Repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private val _categories = MutableLiveData<CategoryResponse>()
    val categories: LiveData<CategoryResponse> = _categories

    private val _exercises = MutableLiveData<ExerciseResponse>()
    val exercises: LiveData<ExerciseResponse> = _exercises

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /*fun getCategories() {

        viewModelScope.launch {

            _loading.value = true
            try {
                val response = repository.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    _categories.value = response.body()
                } else {
                    _error.value = response.message()
                }

            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }

            _loading.value = false
        }
    }*/

    fun getCategories() {

        viewModelScope.launch {

            _loading.postValue(true)

            try {

                val response = repository.getCategories()

                if (response.isSuccessful && response.body() != null) {
                    _categories.postValue(response.body())
                } else {
                    _error.postValue(response.message())
                }

            } catch (e: Exception) {
                _error.postValue(e.message ?: "Something went wrong")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    /*fun getPopularExercises() {

        viewModelScope.launch {

            val response = repository.getPopularExercises()

            if (response.isSuccessful && response.body() != null) {
                _exercises.value = response.body()
            }
        }
    }*/

    fun getPopularExercises() {

        viewModelScope.launch {

            try {

                val response = repository.getPopularExercises()

                if (response.isSuccessful && response.body() != null) {
                    _exercises.postValue(response.body())
                } else {
                    _error.postValue(response.message())
                }

            } catch (e: Exception) {
                _error.postValue(e.message ?: "Something went wrong")
            }
        }
    }
}