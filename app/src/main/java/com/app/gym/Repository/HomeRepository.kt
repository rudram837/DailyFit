package com.app.gym.Repository


import com.app.gym.Model.CategoryResponse
import com.app.gym.Model.ExerciseResponse
import com.app.gym.Network.RetrofitClient
import retrofit2.Response

class HomeRepository {

    suspend fun getCategories(): Response<CategoryResponse> {
        return RetrofitClient.api.getCategories()
    }

    suspend fun getPopularExercises(): Response<ExerciseResponse> {
        return RetrofitClient.api.getPopularExercises()
    }
}