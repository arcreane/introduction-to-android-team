package com.example.quizgame;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TriviaApiService {
    @GET("api.php")
    Call<TriviaResponse> getQuestions(
            @Query("amount") int amount,         // Number of questions
            @Query("difficulty") String difficulty, // Difficulty level (easy, medium, hard)
            @Query("type") String type           // Question type (multiple-choice or true/false)
    );
}

