package com.example.quizgame;

public class Question {

    String question;
    String correctAnswer;
    String wrongAnswer1;
    String wrongAnswer2;
    String wrongAnswer3;

    public Question(String question, String correctAnswer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3 ) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
        this.wrongAnswer3 = wrongAnswer3;
    }
}
