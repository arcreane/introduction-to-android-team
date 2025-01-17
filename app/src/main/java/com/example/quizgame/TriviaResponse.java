package com.example.quizgame;

import java.util.List;

public class TriviaResponse {
    public int response_code;  // API response code
    public List<Question> results;  // List of questions

    public List<Question> getResults() {
        return results;
    }
}

