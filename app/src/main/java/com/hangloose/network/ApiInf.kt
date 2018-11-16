package com.hangloose.network

import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerCreateRequest
import com.hangloose.model.ConsumerLoginRequest
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInf {
    @POST("consumers/login")
    fun consumerLogin(@Body consumerLoginRequestBody: ConsumerLoginRequest): Observable<ConsumerAuthDetailResponse>

    @POST("consumers/register")
    fun consumerRegister(@Body consumerRegisterRequestBody: ConsumerCreateRequest): Observable<ConsumerAuthDetailResponse>
}