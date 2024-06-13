package com.example.madlab3game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import java.util.ArrayList

class GameView(var c: Context, var gameTask: GameTask) : View(c) {

    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var myWalkPosition = 0
    private val otherDisbs = ArrayList<HashMap<String, Any>>()
    private lateinit var mediaPlayer: MediaPlayer



    var viewWidth = 0
    var viewHeight = 0
    private var isGameRunning = false // Track if the game is running

    init {
        myPaint = Paint()
        mediaPlayer = MediaPlayer.create(context, R.raw.avengers)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isGameRunning) {

            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }

            // Reset game state when game starts
            time = 0
            score = 0
            myWalkPosition = 0
            otherDisbs.clear()
            isGameRunning = true
        }

        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        // Calculate the time increment per frame
        val frameTimeIncrement = 10 + speed // Adjust this value as needed

        // Increment the time by the frame time increment
        time += frameTimeIncrement

        // Check if it's time to spawn a new obstacle
        if (time % 500 < frameTimeIncrement) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherDisbs.add(map)
        }

        val manWidth = viewWidth / 5
        val manHeight = manWidth + 10
        myPaint!!.style = Paint.Style.FILL

        val d = ContextCompat.getDrawable(context, R.drawable.jetpackrun)

        d?.let {
            it.setBounds(
                myWalkPosition * viewWidth / 3 + viewWidth / 15 + 25,
                viewHeight - 2 - manHeight,
                myWalkPosition * viewWidth / 3 + viewWidth / 15 + manWidth - 25,
                viewHeight - 2
            )
            it.draw(canvas)
        }

        myPaint!!.color = Color.GREEN
        var highScore = 0

        for (i in otherDisbs.indices.reversed()) {
            try {
                val treeX = otherDisbs[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                val treeY = time - otherDisbs[i]["startTime"] as Int
                val d2 = ContextCompat.getDrawable(context, R.drawable.eagle)

                d2?.let {
                    it.setBounds(
                        treeX + 25, treeY - manHeight, treeX + manWidth - 25, treeY
                    )
                    it.draw(canvas)
                }

                // Inside the onDraw() method
// Adjust the collision detection logic
                if (otherDisbs[i]["lane"] as Int == myWalkPosition) {
                    val collisionThreshold = viewHeight - 2 - manHeight - 110 // Adjust this threshold value as needed
                    if (treeY > collisionThreshold) {
                        gameTask.closeGame(score)
                        isGameRunning = false // Game ends, set isGameRunning to false

                        return // End the game immediately on collision
                    }
                }

                if (treeY > viewHeight + manHeight) {
                    otherDisbs.removeAt(i)
                    score++
                    speed = 1 + Math.abs(score / 8)
                    if (score > highScore) {
                        highScore = score
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 40f
        canvas.drawText("Score : $score", 80f, 80f, myPaint!!)
        canvas.drawText("Speed : $speed", 380f, 80f, myPaint!!)

        // Continue the game by invalidating the view for the next frame
        invalidate()
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isGameRunning) {
                    // Start the game if it's not running
                    isGameRunning = true
                    invalidate()
                    return true
                }
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myWalkPosition > 0) {
                        myWalkPosition--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (myWalkPosition < 2) {
                        myWalkPosition++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }
}
