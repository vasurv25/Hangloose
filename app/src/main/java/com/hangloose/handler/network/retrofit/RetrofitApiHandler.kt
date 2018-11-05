package com.hangloose.handler.network.retrofit

import android.content.Context
import com.google.gson.GsonBuilder
import com.hangloose.BuildConfig
import com.hangloose.utils.HTTPCLIENT_CONNECT_TIMEOUT
import com.hangloose.utils.HTTPCLIENT_READ_TIMEOUT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *  This class is a implementation of {@ApiInf}. It uses Retrofit for network connectivity.
 */
class RetrofitApiHandler(val context: Context) {

    private val retrofitApis: RetrofitApis

    init {

        val gsonBuilder = GsonBuilder()
            .setLenient()
            .create()

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(HTTPCLIENT_READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(HTTPCLIENT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(ConnectivityInterceptor(context))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
            .build()

        retrofitApis = retrofit.create(RetrofitApis::class.java)
    }

    interface RetrofitApis {}
}