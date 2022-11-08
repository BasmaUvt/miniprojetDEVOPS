package com.example.tunisavia.remote

import com.example.tunisavia.entity.response.MessageResponse
import com.example.tunisavia.entity.LoginParam
import com.example.tunisavia.entity.TechnicalParam
import com.example.tunisavia.entity.TeckParam
import com.example.tunisavia.entity.User
import com.example.tunisavia.entity.response.UserResponse
import com.example.tunisavia.entity.response.VolResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServiceEndPoint {
    @GET("getAll")
    suspend fun getAllUsers(): UserResponse

    @GET("getAllVol")
    suspend fun getAllVol(): VolResponse

    @POST("getFlightById")
    suspend fun getFlightById(@Body user: TechnicalParam): VolResponse

    @POST("deleteFlightById")
    suspend fun deleteFlightById(@Body user: TechnicalParam): MessageResponse

    @POST("getUserByEmail")
    suspend fun getUserByEmail(@Body user: LoginParam): UserResponse

    @POST("save")
    suspend fun saveUser(@Body user: User): MessageResponse

    @POST("saveTechnic")
    suspend fun saveTechnic(@Body user: TechnicalParam): MessageResponse

    @POST("updateTechnic")
    suspend fun updateTechnic(@Body user: TeckParam): MessageResponse
}