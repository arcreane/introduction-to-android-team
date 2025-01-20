package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    static int NUM_OF_QUESTIONS_PER_GAME = 10;

    private ConstraintLayout rootLayout;

    private List<Question> questions;
    private List<Button> answerButtons;

    private TextView questionText;
    private TextView counterText;
    private TextView endingText;
    private TextView commentText;

    private Button startButton;
    private Button restartButton;
    private Button leaderboardButton;

    private RadioGroup difficultyRadioGroup;
    private EditText usernameInput;
    private String selectedDifficulty = "medium";
    private String username = "";

    private int score = 0;
    private int questionCounter = 0;

    private final String red = "#FB7C7C";
    private final String green = "#A3FFB2";
    private final String buttonColor = "#C3E1D8";

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        initializeViews();

        // Set up difficulty selection listener
        setupDifficultyListener();

        // Set up start button listener
        setupStartButtonListener();

        // Set up leaderboard button listener
        leaderboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });
    }

    private void initializeViews() {
        rootLayout = findViewById(R.id.rootLayout);
        Objects.requireNonNull(getSupportActionBar()).hide();

        questions = new ArrayList<>();
        answerButtons = new ArrayList<>();

        questionText = findViewById(R.id.questionText);
        counterText = findViewById(R.id.counterText);
        endingText = findViewById(R.id.endingText);
        commentText = findViewById(R.id.commentText);

        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);
        leaderboardButton = findViewById(R.id.leaderboardButton);

        difficultyRadioGroup = findViewById(R.id.difficultyRadioGroup);
        usernameInput = findViewById(R.id.usernameInput);

        answerButtons.add(findViewById(R.id.answerButton1));
        answerButtons.add(findViewById(R.id.answerButton2));
        answerButtons.add(findViewById(R.id.answerButton3));
        answerButtons.add(findViewById(R.id.answerButton4));
    }

    private void setupDifficultyListener() {
        difficultyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.easyRadioButton) {
                selectedDifficulty = "easy";
            } else if (checkedId == R.id.mediumRadioButton) {
                selectedDifficulty = "medium";
            } else if (checkedId == R.id.hardRadioButton) {
                selectedDifficulty = "hard";
            }
        });
    }

    private void setupStartButtonListener() {
        startButton.setOnClickListener(view -> {
            username = usernameInput.getText().toString().trim();

            if (username.isEmpty()) {
                usernameInput.setError("Please enter your username");
                return;
            }

            Log.d("MainActivity", "Username: " + username);

            usernameInput.setVisibility(View.GONE);
            difficultyRadioGroup.setVisibility(View.GONE);

            fetchQuestions();
            startButton.setVisibility(View.INVISIBLE);
        });
    }

    private void fetchQuestions() {
        TriviaApiService apiService = RetrofitClient.getClient().create(TriviaApiService.class);
        Call<TriviaResponse> call = apiService.getQuestions(
                NUM_OF_QUESTIONS_PER_GAME, selectedDifficulty, "multiple"
        );

        call.enqueue(new Callback<TriviaResponse>() {
            @Override
            public void onResponse(Call<TriviaResponse> call, Response<TriviaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questions = response.body().getResults();
                    if (questions.isEmpty()) {
                        Log.e("MainActivity", "No questions fetched.");
                        return;
                    }
                    Collections.shuffle(questions);
                    startGame();
                } else {
                    Log.e("MainActivity", "Failed to fetch questions.");
                }
            }

            @Override
            public void onFailure(Call<TriviaResponse> call, Throwable t) {
                Log.e("MainActivity", "API request failed", t);
            }
        });
    }

    private void startGame() {
        Collections.shuffle(questions);
        questionCounter = 0;
        score = 0;

        displayQuestion(questions.get(questionCounter));
    }

    private void displayQuestion(Question question) {
        questionText.setText(question.question);
        questionText.setVisibility(View.VISIBLE);

        List<String> allAnswers = new ArrayList<>(question.incorrect_answers);
        allAnswers.add(question.correct_answer);
        Collections.shuffle(allAnswers);

        for (int i = 0; i < answerButtons.size(); i++) {
            Button button = answerButtons.get(i);
            button.setText(allAnswers.get(i));
            button.setVisibility(View.VISIBLE);

            if (allAnswers.get(i).equals(question.correct_answer)) {
                setAnswerButton(button, green, true);
            } else {
                setAnswerButton(button, red, false);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setAnswerButton(Button button, String color, boolean isCorrect) {
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                button.setBackgroundColor(Color.parseColor(color));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                button.setBackgroundColor(Color.parseColor(buttonColor));
            }
            return false;
        });

        button.setOnClickListener(v -> {
            if (isCorrect) {
                score++;
            }
            questionCounter++;
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

        String resultMessage = username + ", your score is: " + score + "/" + NUM_OF_QUESTIONS_PER_GAME;
        endingText.setText(resultMessage);
        endingText.setVisibility(View.VISIBLE);

        dbHelper.insertUser(username, score);
        restartButton.setVisibility(View.VISIBLE);
        restartButton.setOnClickListener(v -> recreate());
    }
}
