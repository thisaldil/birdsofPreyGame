package com.example.madlab3game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity(), GameTask {

    lateinit var rootLayout: LinearLayout
    lateinit var startBtn: Button
    lateinit var optionsBtn: Button // New button for options
    lateinit var exitBtn: Button // New button to exit the game
    lateinit var mGameView: GameView
    lateinit var score: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var highScoreTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val optionButton = findViewById<Button>(R.id.optionsBtn)
        optionButton.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        startBtn = findViewById(R.id.startBtn)
        optionsBtn = findViewById(R.id.optionsBtn) // Initialize options button
        exitBtn = findViewById(R.id.exitBtn) // Initialize exit button
        rootLayout = findViewById(R.id.rootLayout)
        score = findViewById(R.id.score)
        highScoreTextView = findViewById(R.id.highScoreTextView)

        sharedPreferences = getSharedPreferences("GamePreferences", Context.MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("HighScore", 0)
        highScoreTextView.text = "High Score: $highScore"

        mGameView = GameView(this, this)
        startBtn.setOnClickListener {
            startGame()
        }



        exitBtn.setOnClickListener {
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        optionsBtn.visibility = View.VISIBLE
        exitBtn.visibility = View.VISIBLE
    }

    fun startGame() {
        mGameView = GameView(this, this)
        rootLayout.addView(mGameView)
        startBtn.visibility = View.GONE
        optionsBtn.visibility = View.GONE
        exitBtn.visibility = View.GONE
        score.visibility = View.GONE
        highScoreTextView.visibility = View.VISIBLE
    }

    override fun closeGame(mScore: Int) {
        val highScore = sharedPreferences.getInt("HighScore", 0)
        if (mScore > highScore) {
            val editor = sharedPreferences.edit()
            editor.putInt("HighScore", mScore)
            editor.apply()
            highScoreTextView.text = "High Score: $mScore"
        }

        score.text = "Score : $mScore"
        rootLayout.removeView(mGameView)
        startBtn.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
        exitBtn.visibility = View.VISIBLE
        highScoreTextView.visibility = View.VISIBLE
    }
}