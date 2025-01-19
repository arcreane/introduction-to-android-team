package com.example.quizgame;

import java.util.List;

public class TriviaResponse {
    public int response_code;  
    public List<Question> results; 

    public List<Question> getResults() {
        return results;
    }
}

