package com.app.gym.Network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://oss.exercisedb.dev/api/v1/"
//    private const val BASE_URL = "https://exercisedb.p.rapidapi.com/"


    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api by lazy {
        retrofit.create(ApiServices::class.java)
    }
}