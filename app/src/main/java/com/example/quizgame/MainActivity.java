package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    static int NUM_OF_QUESTIONS_PER_GAME = 10;

    private ConstraintLayout rootLayout; // Reference to the root layout

    List<Question> questions;
    List<Button> answerButtons;
    List<Button> wrongButtons;

    TextView questionText;
    TextView counterText;
    TextView endingText;
    TextView commentText;

    Button answerButton1;
    Button answerButton2;
    Button answerButton3;
    Button answerButton4;

    Button startButton;
    Button restartButton;

    Integer score = 0;

    int questionCounter = 0;

    // My custom colors
    String red = "#FB7C7C";
    String green = "#A3FFB2";
    String buttonColor = "#C3E1D8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize root layout
        rootLayout = findViewById(R.id.rootLayout);

        // Hide the top menu bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Initialize lists
        questions = new ArrayList<>();
        answerButtons = new ArrayList<>();
        wrongButtons = new ArrayList<>();

        // Initialize views
        questionText = findViewById(R.id.questionText);
        counterText = findViewById(R.id.counterText);
        endingText = findViewById(R.id.endingText);
        commentText = findViewById(R.id.commentText);
        answerButton1 = findViewById(R.id.answerButton1);
        answerButton2 = findViewById(R.id.answerButton2);
        answerButton3 = findViewById(R.id.answerButton3);
        answerButton4 = findViewById(R.id.answerButton4);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);

        Collections.addAll(answerButtons, answerButton1, answerButton2, answerButton3, answerButton4);

        // Set the background image for the first page
        rootLayout.setBackgroundResource(R.drawable.background_image);

        // Start button click listener
        startButton.setOnClickListener(view -> {
            startGame();
            startButton.setVisibility(View.INVISIBLE);
        });
    }

    private void startGame() {
        // Remove the background image for the quiz screen
        rootLayout.setBackgroundResource(0);

        makeQuestions();
        Collections.shuffle(questions);
        displayQuestion(questions.get(questionCounter));
    }

    private void restartGame() {
        restartButton.setVisibility(View.INVISIBLE);
        endingText.setVisibility(View.INVISIBLE);
        commentText.setVisibility(View.INVISIBLE);

        score = 0;
        questionCounter = 0;
        Collections.shuffle(questions);

        displayQuestion(questions.get(questionCounter));
    }

    private void displayQuestion(Question question) {
        wrongButtons.clear();

        questionText.setText(question.question);
        questionText.setVisibility(View.VISIBLE);

        int randNum = new Random().nextInt(answerButtons.size());
        int i = 0;

        for (Button button : answerButtons) {
            button.setVisibility(View.VISIBLE);

            if (i == randNum) {
                button.setText(question.correctAnswer);
                setButtonFeatures(button, green, 1, true);
            } else {
                setButtonFeatures(button, red, 0, false);
                wrongButtons.add(button);
            }
            i++;
        }

        wrongButtons.get(0).setText(question.wrongAnswer1);
        wrongButtons.get(1).setText(question.wrongAnswer2);
        wrongButtons.get(2).setText(question.wrongAnswer3);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonFeatures(Button button, String color, Integer points, boolean correct) {
        button.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                button.setBackgroundColor(Color.parseColor(color));
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                button.setBackgroundColor(Color.parseColor(buttonColor));
            }
            return false;
        });

        button.setOnClickListener(view -> {
            score += points;
            displayScore();

            if (!correct) sendVibration();

            questionCounter += 1;
            if (questionCounter < NUM_OF_QUESTIONS_PER_GAME) {
                displayQuestion(questions.get(questionCounter));
            } else {
                endGame();
            }
        });
    }

    private void endGame() {
        for (Button button : answerButtons) {
            button.setVisibility(View.INVISIBLE);
        }
        counterText.setVisibility(View.INVISIBLE);
        questionText.setVisibility(View.INVISIBLE);

        String endInfo = "Quiz finished! \nYou got " + score + "/" + NUM_OF_QUESTIONS_PER_GAME +
                "\ncorrect answers.";
        endingText.setText(endInfo);
        endingText.setVisibility(View.VISIBLE);

        if (score > 7) {
            commentText.setText("Amazingly done!");
        } else if (score > 3) {
            commentText.setText("Try again!");
        } else {
            commentText.setText("Oops! That didn't go very well... Maybe try again?");
        }
        commentText.setVisibility(View.VISIBLE);

        restartButton.setVisibility(View.VISIBLE);
        restartButton.setOnClickListener(view -> restartGame());
    }

    private void displayScore() {
        String scoreKeeping = score + "/" + (questionCounter + 1);
        counterText.setText(scoreKeeping);
        counterText.setVisibility(View.VISIBLE);
    }

    public void sendVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(200);
        }
    }

    // Function to create unique Question objects and add them to a list called questions
    private void makeQuestions() {
        Question q1 = new Question("Why don't skeletons fight each other?",
                "They don't have the guts", "They are too polite", "They love to dance", "They are always asleep");
        questions.add(q1);

        Question q2 = new Question("What is the largest planet in our solar system?",
                "Jupiter", "Saturn", "Earth", "Mars");
        questions.add(q2);

        Question q3 = new Question("Why did the scarecrow win an award?",
                "He was outstanding in his field", "He scared the most crows", "He was really stylish", "He told great jokes");
        questions.add(q3);

        Question q4 = new Question("Who painted the ceiling of the Sistine Chapel?",
                "Michelangelo", "Leonardo da Vinci", "Raphael", "Donatello");
        questions.add(q4);

        Question q5 = new Question("What do you call a fish wearing a bowtie?",
                "Sofishticated", "Dapper", "Classy Catch", "Fin-tastic");
        questions.add(q5);

        Question q6 = new Question("What is the smallest country in the world?",
                "Vatican City", "Monaco", "San Marino", "Liechtenstein");
        questions.add(q6);

        Question q7 = new Question("Why did the tomato turn red?",
                "Because it saw the salad dressing!", "It was shy", "It was sunburnt", "It blushed");
        questions.add(q7);

        Question q8 = new Question("What is the capital of Australia?",
                "Canberra", "Sydney", "Melbourne", "Perth");
        questions.add(q8);

        Question q9 = new Question("Why do bees hum?",
                "Because they don’t know the words!", "To attract flowers", "To communicate", "To stay calm");
        questions.add(q9);

        Question q10 = new Question("What is the tallest mountain in the world?",
                "Mount Everest", "K2", "Kangchenjunga", "Lhotse");
        questions.add(q10);

        Question q11 = new Question("Why did the bicycle fall over?",
                "It was two tired", "It hit a rock", "It lost its balance", "It saw a ghost");
        questions.add(q11);

        Question q12 = new Question("What is the longest river in the world?",
                "Nile", "Amazon", "Yangtze", "Mississippi");
        questions.add(q12);

        Question q13 = new Question("Why was the math book sad?",
                "It had too many problems", "It got wet", "It was torn", "It was lonely");
        questions.add(q13);

        Question q14 = new Question("Which element has the chemical symbol 'O'?",
                "Oxygen", "Osmium", "Oganesson", "Oxide");
        questions.add(q14);

        Question q15 = new Question("Why did the golfer bring an extra pair of pants?",
                "In case he got a hole in one", "It was cold", "They were stylish", "For good luck");
        questions.add(q15);

        Question q16 = new Question("What is the largest ocean on Earth?",
                "Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean");
        questions.add(q16);

        Question q17 = new Question("Why are ghosts bad liars?",
                "Because you can see right through them", "They get scared", "They stutter", "They forget their lines");
        questions.add(q17);

        Question q18 = new Question("What is the boiling point of water at sea level?",
                "100°C", "90°C", "80°C", "70°C");
        questions.add(q18);

        Question q19 = new Question("Why did the computer go to the doctor?",
                "It caught a virus", "It froze", "It needed a reboot", "It was overheating");
        questions.add(q19);

        Question q20 = new Question("Who was the first President of the United States?",
                "George Washington", "Abraham Lincoln", "Thomas Jefferson", "John Adams");
        questions.add(q20);
    }
}