package com.example.quizgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LeaderboardAdapter extends BaseAdapter {

    private final Context context;
    private final List<UserScore> scores;

    // Constructor
    public LeaderboardAdapter(Context context, List<UserScore> scores) {
        this.context = context;
        this.scores = scores;
    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Object getItem(int position) {
        return scores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.leaderboard_item, parent, false);
        }

        TextView usernameText = convertView.findViewById(R.id.usernameText);
        TextView scoreText = convertView.findViewById(R.id.scoreText);

        UserScore userScore = scores.get(position);
        usernameText.setText(userScore.getUsername());
        scoreText.setText(String.valueOf(userScore.getScore()));

        return convertView;
    }
}
