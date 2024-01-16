package com.example.tictactoegame;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTextPlayer1, editTextPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextPlayer1 = findViewById(R.id.editTextPlayer1);
        editTextPlayer2 = findViewById(R.id.editTextPlayer2);
        Button saveButton = findViewById(R.id.buttonSave);

        loadPlayerNames();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlayerNames();
            }
        });
    }

    private void loadPlayerNames() {
        SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String player1Name = prefs.getString("player1Name", "Player 1");
        String player2Name = prefs.getString("player2Name", "Player 2");
        editTextPlayer1.setText(player1Name);
        editTextPlayer2.setText(player2Name);
    }

    private void savePlayerNames() {
        SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String player1Name = editTextPlayer1.getText().toString();
        String player2Name = editTextPlayer2.getText().toString();

        // Check if the fields are empty and set default names
        if (player1Name.isEmpty()) {
            player1Name = "Player 1";
        }
        if (player2Name.isEmpty()) {
            player2Name = "Player 2";
        }

        editor.putString("player1Name", player1Name);
        editor.putString("player2Name", player2Name);
        editor.apply();
        finish(); // Close the activity
    }
}