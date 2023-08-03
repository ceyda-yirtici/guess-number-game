package com.example.movieproject.service

import com.example.movieproject.model.GenreList
import com.example.movieproject.model.MovieGenre
import com.example.movieproject.model.MovieList
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("movie/popular")
    suspend fun getMovieList(@Query("api_key") api_key:String, @Query("page") page:Int) : MovieList

    @GET("genre/movie/list?language=en")
    suspend fun getMovieGenres(@Query("api_key") api_key:String) : GenreList

}
