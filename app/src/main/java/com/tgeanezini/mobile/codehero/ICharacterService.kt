package com.tgeanezini.mobile.codehero

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ICharacterService {
    @GET("v1/public/characters")
    fun getCharacters(
        @Query("ts") ts: String,
        @Query("apikey") apikey: String,
        @Query("hash") hash: String) : Call<CharacterResponse>
}