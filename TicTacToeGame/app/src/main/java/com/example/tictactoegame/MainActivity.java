package com.example.tictactoegame;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring game buttons 3X3 grid
    private final Button[][] gameButtons = new Button[3][3];

    // Variables for tracking the current player's turn, number of turns, and scores
    private boolean isFirstPlayerTurn = true;
    private int numberOfTurns;
    private int firstPlayerScore;
    private int secondPlayerScore;

    // Variables for color
    private int purpleColor;
    private int cyanColor;
    private int magentaColor;
    private int yellowColor;

    //TextViews for displaying names, scores, and turn status
    private TextView firstPlayerScoreTextView;
    private TextView secondPlayerScoreTextView;
    private TextView firstPlayerTextView;
    private TextView secondPlayerTextView;
    private TextView turnStatusTextView;

    //For Logging when debugging
    private static final String TAG = "TicTacToe";

    private static final String DEFAULT_PLAYER_1 = "Player 1";
    private static final String DEFAULT_PLAYER_2 = "Player 2";
    private static final String DEFAULT_PLAYER_1_KEY = "player1Name";
    private static final String DEFAULT_PLAYER_2_KEY = "player2Name";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        purpleColor = ContextCompat.getColor(this, R.color.purple_500);
        cyanColor = ContextCompat.getColor(this, R.color.cyan);
        magentaColor = ContextCompat.getColor(this, R.color.magenta);
        yellowColor = ContextCompat.getColor(this, R.color.yellow);

        //Initializing TextViews for names, scores and game status
        firstPlayerScoreTextView = findViewById(R.id.score_player1);
        secondPlayerScoreTextView= findViewById(R.id.score_player2);
        firstPlayerTextView = findViewById(R.id.text_view_player1);
        secondPlayerTextView = findViewById(R.id.text_view_player2);
        turnStatusTextView = findViewById(R.id.text_view_turn);

        //Setting up the buttons
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "buttonPosition_" + i + j;

                //Get the button resource ID and initialize each button
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                gameButtons[i][j] = findViewById(resID);
                if (gameButtons[i][j] != null) {
                    gameButtons[i][j].setOnClickListener(this);
                }
            }
        }

        //Setting up new game button and its click listener
        Button newGameButton = findViewById(R.id.newGame_button);
        newGameButton.setOnClickListener(view -> {
            resetGameBoard(); // Reset the game board and the game state
            resetScores(); // Reset the scores of both players to zero
        });
    }

    // Method to reset the game board and game state
    private void resetGameBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameButtons[i][j].setText("");
                gameButtons[i][j].setBackgroundColor(purpleColor); // Reset to default background
            }
        }

        numberOfTurns = 0;
        isFirstPlayerTurn = true;
        updateTurnStatus();
    }

    // Method to reset the scores of both players
    private void resetScores() {
        firstPlayerScore = 0;
        secondPlayerScore = 0;
        updateScore(); // Update the score display
    }


    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerNames();
        updateTurnStatus();
    }

    //Update names to textview after adding in settings menu
    private void updatePlayerNames() {
        String player1Name = getPlayerName(DEFAULT_PLAYER_1_KEY, DEFAULT_PLAYER_1);
        String player2Name = getPlayerName(DEFAULT_PLAYER_2_KEY, DEFAULT_PLAYER_2);
        firstPlayerTextView.setText(player1Name);
        secondPlayerTextView.setText(player2Name);
    }

    private String getPlayerName(String key, String defaultName) {
        SharedPreferences sharedPref = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Updating status message to see whose turn it is to make a move
    private void updateTurnStatus() {
        String playerOneName = getPlayerName(DEFAULT_PLAYER_1_KEY, DEFAULT_PLAYER_1);
        String playerTwoName = getPlayerName(DEFAULT_PLAYER_2_KEY, DEFAULT_PLAYER_2);
        String turnText = isFirstPlayerTurn ? playerOneName + "'s turn" : playerTwoName + "'s turn";
        turnStatusTextView.setText(turnText);

        // Set text color based on whose turn it is
        if (isFirstPlayerTurn) {
            turnStatusTextView.setTextColor(cyanColor); // Cyan for Player One
        } else {
            turnStatusTextView.setTextColor(magentaColor); // Magenta for Player Two
        }
    }

    //handle button clicks for game with "X" and "O"
    @Override
    public void onClick(View view) {
        Log.d(TAG, "Button clicked");

        Button button = (Button) view;

        if (!((Button) view).getText().toString().equals("")) {
            return;
        }

        if (isFirstPlayerTurn){
            button.setText("X");
            button.setTextColor(cyanColor);// Cyan
            Log.d(TAG, "Set text to X");
        } else {
            button.setText("O");
            button.setTextColor(magentaColor);// Magenta
            Log.d(TAG, "Set text to O");
        }

        numberOfTurns++;

        int[][] winningPositions = checkWin();
        if (winningPositions != null) {
            if (isFirstPlayerTurn) {
                playerWins(1);
            } else {
                playerWins(2);
            }
            highlightWinningButtons(winningPositions);
        } else if (numberOfTurns == 9) {
            itsATie();
        } else {
            isFirstPlayerTurn = !isFirstPlayerTurn;
            updateTurnStatus();
        }
    }

    private int[][] checkWin() {
        int[][] rowsWin = checkRowsForWin();
        if (rowsWin != null) {
            return rowsWin;
        }

        int[][] columnsWin = checkColumnsForWin();
        if (columnsWin != null) {
            return columnsWin;
        }

        int[][] diagonalsWin = checkDiagonalsForWin();
        if (diagonalsWin != null) {
            return diagonalsWin;
        }

        return null;
    }


    private int[][] checkRowsForWin() {
        for (int i = 0; i < 3; i++) {
            if (gameButtons[i][0].getText().toString().equals(gameButtons[i][1].getText().toString())
                    && gameButtons[i][0].getText().toString().equals(gameButtons[i][2].getText().toString())
                    && !gameButtons[i][0].getText().toString().equals("")) {
                return new int[][]{{i, 0}, {i, 1}, {i, 2}};
            }
        }
        return null;
    }

    private int[][] checkColumnsForWin() {
        for(int i = 0; i < 3; i++){
            if(gameButtons[0][i].getText().toString().equals(gameButtons[1][i].getText().toString())
                    && gameButtons[0][i].getText().toString().equals(gameButtons[2][i].getText().toString())
                    && !gameButtons[0][i].getText().toString().equals("")){
                return new int[][]{{0, i}, {1, i}, {2, i}};
            }
        }

        return null;
    }

    private int[][] checkDiagonalsForWin() {
        // Check first diagonal
        if (gameButtons[0][0].getText().toString().equals(gameButtons[1][1].getText().toString())
                && gameButtons[0][0].getText().toString().equals(gameButtons[2][2].getText().toString())
                && !gameButtons[0][0].getText().toString().equals("")) {
            return new int[][]{{0, 0}, {1, 1}, {2, 2}};
        }

        // Check second diagonal
        if (gameButtons[0][2].getText().toString().equals(gameButtons[1][1].getText().toString())
                && gameButtons[0][2].getText().toString().equals(gameButtons[2][0].getText().toString())
                && !gameButtons[0][2].getText().toString().equals("")) {
            return new int[][]{{0, 2}, {1, 1}, {2, 0}};
        }

        return null;
    }

    // Method to disable all buttons
    private void disableAllButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameButtons[i][j].setEnabled(false);
            }
        }
    }
    // Method to enable all buttons
    private void enableAllButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameButtons[i][j].setEnabled(true);
            }
        }
    }

    private void playerWins(int playerNum) {
        String winnerName;
        if (playerNum == 1) {
            firstPlayerScore++;
            winnerName = getPlayerName(DEFAULT_PLAYER_1_KEY, DEFAULT_PLAYER_1);
        } else {
            secondPlayerScore++;
            winnerName = getPlayerName(DEFAULT_PLAYER_2_KEY, DEFAULT_PLAYER_2);
        }
        disableAllButtons();
        updateScore();
        turnStatusTextView.setText(winnerName + " wins!");
        turnStatusTextView.setTextColor(Color.GREEN); // Set text color to green for the win
        Log.d(TAG, winnerName + " wins!");

        int[][] winningPositions = checkWin();
        if (winningPositions != null) {
            highlightWinningButtons(winningPositions);
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> resetGameButtons());
            }
        }, 2000); // 2000 milliseconds delay
    }

    private void itsATie(){
        turnStatusTextView.setText("It's a tie!");
        Log.d(TAG, "Game Tie: It's a tie!");
        resetGameButtons();
    }

    //Highlight Buttons when it's a win
    private void highlightWinningButtons(int[][] winningPositions) {
        for (int[] pos : winningPositions) {
            gameButtons[pos[0]][pos[1]].setBackgroundColor(yellowColor); // Yellow color
        }
    }

    //Update textview for player score
    private void updateScore(){
        firstPlayerScoreTextView.setText(String.valueOf(firstPlayerScore));
        secondPlayerScoreTextView.setText(String.valueOf(secondPlayerScore));

    }

    //Resetting the button board after end of game to start another game
    private void resetGameButtons(){
        enableAllButtons();
        for (int i= 0; i<3; i++){
            for(int j=0; j<3; j++){
                gameButtons[i][j].setText("");
                gameButtons[i][j].setBackgroundColor(purpleColor); // Reset to default background
            }
        }

        numberOfTurns = 0;
        isFirstPlayerTurn = true;

        // Delay the updateTurnStatus call
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> updateTurnStatus());
            }
        }, 2000); // 2000 milliseconds delay

    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("numberOfTurns", numberOfTurns);
        outState.putInt("firstPlayerScore", firstPlayerScore);
        outState.putInt("secondPlayerScore", secondPlayerScore);
        outState.putBoolean("player1Turn", isFirstPlayerTurn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        numberOfTurns = savedInstanceState.getInt("numberOfTurns");
        firstPlayerScore = savedInstanceState.getInt("firstPlayerScore");
        secondPlayerScore = savedInstanceState.getInt("secondPlayerScore");
        isFirstPlayerTurn = savedInstanceState.getBoolean("isFirstPlayerTurn");
    }
}
