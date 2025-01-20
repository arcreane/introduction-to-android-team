package com.example.quizgame;

import android.os.Bundle;
import android.widget.ListView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Log.d("LeaderboardActivity", "Activity started.");

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);
        Log.d("LeaderboardActivity", "DatabaseHelper initialized.");

        // Retrieve leaderboard data
        List<UserScore> scores = dbHelper.getLeaderboard();
        if (scores == null || scores.isEmpty()) {
            Log.e("LeaderboardActivity", "No leaderboard data found.");
        } else {
            Log.d("LeaderboardActivity", "Leaderboard data loaded: " + scores.size() + " entries.");
        }

        // Set up ListView and Adapter
        ListView leaderboardListView = findViewById(R.id.leaderboardListView);
        if (leaderboardListView == null) {
            Log.e("LeaderboardActivity", "ListView is null.");
        }
        LeaderboardAdapter adapter = new LeaderboardAdapter(this, scores);
        leaderboardListView.setAdapter(adapter);
        Log.d("LeaderboardActivity", "Adapter set on ListView.");
    }

}
