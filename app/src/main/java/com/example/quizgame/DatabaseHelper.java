package com.example.quizgame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quizgame.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_SCORES = "scores";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_SCORE = "score";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating database...");
        String CREATE_SCORES_TABLE = "CREATE TABLE scores (" +
                "username TEXT," +
                "score INTEGER)";
        db.execSQL(CREATE_SCORES_TABLE);
        Log.d("DatabaseHelper", "Database created successfully.");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database...");
        db.execSQL("DROP TABLE IF EXISTS scores");
        onCreate(db);
        Log.d("DatabaseHelper", "Database upgraded successfully.");
    }


    // Insert a new score into the database
    public void insertUser(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + TABLE_SCORES + " (" + COLUMN_USERNAME + ", " + COLUMN_SCORE + ") VALUES ('"
                + username + "', " + score + ")";
        db.execSQL(query);
        db.close();
    }

    // Fetch leaderboard data
    public List<UserScore> getLeaderboard() {
        Log.d("DatabaseHelper", "Fetching leaderboard data.");
        List<UserScore> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USERNAME + ", " + COLUMN_SCORE + " FROM " + TABLE_SCORES
                + " ORDER BY " + COLUMN_SCORE + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                int score = cursor.getInt(1);
                scores.add(new UserScore(username, score));
            } while (cursor.moveToNext());
        } else {
            Log.e("DatabaseHelper", "No data found in the scores table.");
        }

        cursor.close();
        db.close();
        Log.d("DatabaseHelper", "Leaderboard data retrieved: " + scores.size() + " entries.");
        return scores;
    }
}
