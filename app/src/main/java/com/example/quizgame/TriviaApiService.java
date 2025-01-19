package com.example.quizgame;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TriviaApiService {
    @GET("api.php")
    Call<TriviaResponse> getQuestions(
            @Query("amount") int amount,         
            @Query("difficulty") String difficulty, 
            @Query("type") String type           
    );
}

