package com.app.gym.Network

import com.app.gym.Model.CategoryResponse
import com.app.gym.Model.ExerciseResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServices {

//    category
    @GET("bodyparts")
    suspend fun getCategories(): Response<CategoryResponse>

//    excercise
//    @GET("exercises")
    @GET("exercises")
    suspend fun getPopularExercises(): Response<ExerciseResponse>
}