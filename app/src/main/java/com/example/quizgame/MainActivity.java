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

    RadioGroup difficultyRadioGroup;
    EditText usernameInput;
    String selectedDifficulty = "medium"; // Default difficulty
    String username = "";

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
        difficultyRadioGroup = findViewById(R.id.difficultyRadioGroup);
        usernameInput = findViewById(R.id.usernameInput);

        Collections.addAll(answerButtons, answerButton1, answerButton2, answerButton3, answerButton4);

        // Difficulty selection listener
        difficultyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.easyRadioButton) {
                selectedDifficulty = "easy";
            } else if (checkedId == R.id.mediumRadioButton) {
                selectedDifficulty = "medium";
            } else if (checkedId == R.id.hardRadioButton) {
                selectedDifficulty = "hard";
            }
        });

        // Start button click listener
        startButton.setOnClickListener(view -> {
            // Get the username
            username = usernameInput.getText().toString().trim();

            // Validate the username
            if (username.isEmpty()) {
                usernameInput.setError("Please enter your username");
                return;
            }

            // Log the username
            Log.d("MainActivity", "Username: " + username);

            // Hide the input field and difficulty options
            usernameInput.setVisibility(View.GONE);
            difficultyRadioGroup.setVisibility(View.GONE);

            // Start the game
            fetchQuestions();
            startButton.setVisibility(View.INVISIBLE);
        });
    }

    private void fetchQuestions() {
        Log.d("MainActivity", "Fetching questions...");
        TriviaApiService apiService = RetrofitClient.getClient().create(TriviaApiService.class);

        Call<TriviaResponse> call = apiService.getQuestions(
                NUM_OF_QUESTIONS_PER_GAME, // Fetch 10 questions
                selectedDifficulty,       // Selected difficulty
                "multiple"               // Question type
        );

        call.enqueue(new Callback<TriviaResponse>() {
            @Override
            public void onResponse(Call<TriviaResponse> call, Response<TriviaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questions = response.body().getResults();

                    if (questions == null || questions.isEmpty()) {
                        Log.e("MainActivity", "No questions fetched.");
                        return;
                    }

                    Log.d("MainActivity", "Questions fetched: " + questions.size());
                    Collections.shuffle(questions); // Shuffle for variety
                    startGame();
                } else {
                    Log.e("MainActivity", "Failed to fetch questions. Response: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<TriviaResponse> call, Throwable t) {
                Log.e("MainActivity", "API request failed", t);
            }
        });
    }

    private void startGame() {
        if (questions == null || questions.isEmpty()) {
            Log.e("MainActivity", "Questions list is null or empty!");
            return;
        }

        // Shuffle the list and pick the first 10 unique questions
        Collections.shuffle(questions);
        List<Question> gameQuestions = questions.subList(0, Math.min(NUM_OF_QUESTIONS_PER_GAME, questions.size()));

        Log.d("MainActivity", "Game questions: " + gameQuestions);

        // Reset the game state with the new question set
        questions = new ArrayList<>(gameQuestions);
        questionCounter = 0;
        score = 0;

        rootLayout.setBackgroundResource(0); // Remove the background image for the quiz screen
        displayQuestion(questions.get(questionCounter));
    }

    private void restartGame() {
        restartButton.setVisibility(View.INVISIBLE);
        endingText.setVisibility(View.INVISIBLE);
        commentText.setVisibility(View.INVISIBLE);

        score = 0;
        questionCounter = 0;
        Collections.shuffle(questions);
        fetchQuestions();
        displayQuestion(questions.get(questionCounter));
    }

    private void displayQuestion(Question question) {
        wrongButtons.clear();

        questionText.setText(question.question);
        questionText.setVisibility(View.VISIBLE);

        List<String> allAnswers = new ArrayList<>(question.incorrect_answers);
        allAnswers.add(question.correct_answer);
        Collections.shuffle(allAnswers);

        for (int i = 0; i < answerButtons.size(); i++) {
            Button button = answerButtons.get(i);
            button.setVisibility(View.VISIBLE);
            button.setText(allAnswers.get(i));

            if (allAnswers.get(i).equals(question.correct_answer)) {
                setButtonFeatures(button, green, 1, true);
            } else {
                setButtonFeatures(button, red, 0, false);
                wrongButtons.add(button);
            }
        }
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

        // Display score message
        String endInfo = "Quiz finished! \n" + username + ", you got " + score + "/" + NUM_OF_QUESTIONS_PER_GAME +
                "\ncorrect answers.";
        endingText.setText(endInfo);
        endingText.setVisibility(View.VISIBLE);

        // Display motivational comment
        if (score > 7) {
            commentText.setText("Amazingly done!");
        } else if (score > 3) {
            commentText.setText("Try again!");
        } else {
            commentText.setText("Oops! That didn't go very well... Maybe try again?");
        }
        commentText.setVisibility(View.VISIBLE);

        // Show restart button
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
}
