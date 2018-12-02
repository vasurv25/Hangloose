package com.hangloose.network

import com.hangloose.model.ConsumerAuthDetailResponse
import com.hangloose.model.ConsumerCreateRequest
import com.hangloose.model.ConsumerResendOtpRequest
import com.hangloose.model.ConsumerLoginRequest
import com.hangloose.model.ConsumerOTPRequest
import com.hangloose.model.ConsumerOtpChangePasswordRequest
import com.hangloose.model.ConsumerOtpVerifyRequest
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInf {
    @POST("consumers/login")
    fun consumerLogin(@Body consumerLoginRequestBody: ConsumerLoginRequest): Observable<Response<ConsumerAuthDetailResponse>>

    @POST("consumers")
    fun consumerRegister(@Body consumerRegisterRequestBody: ConsumerCreateRequest): Observable<Response<ConsumerAuthDetailResponse>>

    @PATCH("consumers/{id}/{authType}/verified")
    fun consumerRegisterOTP(@Header("X_AUTH_TOKEN") header: String, @Path("id") consumerId: String, @Path("authType") authType: String, @Body consumerOTPRequest: ConsumerOTPRequest): Observable<Response<ConsumerAuthDetailResponse>>

    @POST("otp")
    fun initiateForgotPassword(@Body resendOtp: ConsumerResendOtpRequest): Observable<Response<Int>>

    @POST("otp/verify")
    fun otpVerifyForgotPassword(@Body verifyOtp: ConsumerOtpVerifyRequest): Observable<Response<ConsumerAuthDetailResponse>>

    @PATCH("consumers/{consumerId}/setPassword")
    fun consumerChangePassword(@Header("X_AUTH_TOKEN") header: String, @Path("consumerId") consumerId: String, @Body consumerOTPChangePassRequest: ConsumerOtpChangePasswordRequest): Observable<Response<Int>>
}