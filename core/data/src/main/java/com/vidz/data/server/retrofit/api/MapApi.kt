package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.map.AutoCompleteResponseDto
import com.vidz.data.server.retrofit.dto.map.PlaceDetailResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface MapApi{
    @GET("/Place/AutoComplete")
    suspend fun getAutoComplete(
        @Query("input") input: String?,
        @Query("api_key") apiKey: String?
    ): Response<AutoCompleteResponseDto>?

    @GET("/Place/Detail")
    suspend fun getPlaceDetail(
        @Query("place_id") placeId: String?,
        @Query("api_key") apiKey: String?
    ): Response<PlaceDetailResponseDto>?
}