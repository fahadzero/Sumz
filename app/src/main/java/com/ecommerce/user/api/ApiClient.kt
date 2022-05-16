package com.ecommerce.user.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    //Live Base Url
    val BASE_URL ="https://10.0.2.2:3306/"

    val APP_URL = BASE_URL + "api/"

    var TIMEOUT: Long = 60 * 1.toLong()
    val getClient: ApiInterface
        get() {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.addInterceptor(logging)
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl(APP_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
}