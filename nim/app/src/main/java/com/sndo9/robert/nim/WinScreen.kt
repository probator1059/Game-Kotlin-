package com.sndo9.robert.nim

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import java.util.StringTokenizer

import android.R.id.edit
import android.support.v7.app.AppCompatActivity

class WinScreen : AppCompatActivity() {

    protected var hScores = IntArray(10)
    protected var hInitals = arrayOfNulls<String>(10)
    protected lateinit var ini: String
    protected var index: Int = 0
    protected lateinit var highscores: SharedPreferences
    protected lateinit var scoreEditor: SharedPreferences.Editor
    protected var j = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win_screen)

        var runningScore: Int
        val isOne: Boolean?
        val turns: Int
        val isAI: Boolean?

        val playAgain = findViewById(R.id.buttonPlayAgain) as ImageButton
        val mainMenu = findViewById(R.id.buttonMainMenu) as ImageButton

        val save = getSharedPreferences("save", 0)
        val editSave = save.edit()

        editSave.remove("rowOneSave")
        editSave.remove("rowTwoSave")
        editSave.remove("rowThreeSave")
        editSave.remove("rowFourSave")
        editSave.remove("numTurns")
        editSave.commit()


        val call = intent

        val extras = call.extras

        Log.w("WinScreen", "Launched")

        if (extras != null) {
            if (extras.containsKey("winner") && extras.containsKey("score") && extras.containsKey("numTurns")) {
                runningScore = extras.getInt("score")
                isOne = extras.getBoolean("winner")
                turns = extras.getInt("numTurns")
                isAI = extras.getBoolean("AI")

                var potentialPoints: Int

                Log.w("--------WinScreen Turns", "" + turns)
                Log.w("--------WinScreen score", "" + runningScore)

                potentialPoints = turns - 3
                Log.w("-------WinScreen.points", "" + potentialPoints)
                potentialPoints = potentialPoints * 2
                Log.w("-------WinScreen.points", "" + potentialPoints)
                potentialPoints = 10 - potentialPoints

                Log.w("-------WinScreen.points", "" + potentialPoints)

                val winnerText = findViewById(R.id.winnerText) as TextView
                val scoreText = findViewById(R.id.textViewScore) as TextView


                //AI won
                if (!isOne && isAI) {
                    winnerText.text = "you"
                    scoreText.text = "$runningScore pts"
                    playAgain.visibility = View.INVISIBLE
                    getHScore()
                    val isHScore = checkHScore(runningScore)
                    if (isHScore) updateHScore(runningScore)
                    runningScore = 0
                } else if (isOne && isAI) {
                    runningScore = runningScore + potentialPoints
                    winnerText.text = "Computer"
                    scoreText.text = "$runningScore pts"
                } else if (isOne && !isAI) {
                    winnerText.text = "Player Two"
                    scoreText.text = "In $turns turns"
                } else if (!isAI && !isOne) {
                    winnerText.text = "Player One"
                    scoreText.text = "In $turns turns"
                }//Player two won
                //Player one won against player two
                //Player one won and is playing the AI

                playAgain.setOnClickListener { view ->
                    val playAgainIntent = Intent(view.context, SinglePlayer::class.java)
                    playAgainIntent.putExtra("score", runningScore)
                    playAgainIntent.putExtra("PLAY_WITH_THE_AI_ON", isAI)
                    startActivity(playAgainIntent)
                }

                mainMenu.setOnClickListener { view ->
                    val mainMenuIntent = Intent(view.context, MainActivity::class.java)
                    startActivity(mainMenuIntent)
                }
                editSave.putString("points", "" + runningScore)
                editSave.commit()
            }
        }
    }

    fun getHScore() {
        //get and parse scores
        var i = 0

        val scores: String?
        val initals: String?

        val defaultScore = "0000*0000*0000*0000*0000*0000*0000*0000*0000*00000"
        val defaultInitials = "XXX*XXX*XXX*XXX*XXX*XXX*XXX*XXX*XXX*XXX"

        highscores = getSharedPreferences("highscore", 0)


        scores = highscores.getString("scores", defaultScore)
        initals = highscores.getString("initials", defaultInitials)

        var sT = StringTokenizer(scores, "*")
        while (sT.hasMoreTokens()) {
            hScores[i] = Integer.parseInt(sT.nextToken())
            i++
        }

        i = 0

        sT = StringTokenizer(initals, "*")
        while (sT.hasMoreTokens()) {
            hInitals[i] = sT.nextToken()
            i++
        }
    }

    fun checkHScore(points: Int): Boolean {
        var isHigher = false

        for (i in hScores.indices) {
            if (hScores[i] < points) {
                if (!isHigher) {
                    isHigher = true
                    index = i
                }

            }
        }
        return isHigher
    }

    fun updateHScore(points: Int) {
        val newIntArray = IntArray(11)
        val newStringArray = arrayOfNulls<String>(11)
        val initals = findViewById(R.id.enterName) as EditText
        initals.visibility = View.VISIBLE

        val submitName = findViewById(R.id.buttonSubmitName) as ImageButton
        submitName.visibility = View.VISIBLE
        //Need to check for more then three characters
        submitName.setOnClickListener {
            ini = initals.text.toString()
            var notSet = true

            run {
                var i = 0
                while (i < 11) {
                    newIntArray[i] = hScores[j]
                    newStringArray[i] = hInitals[j]
                    if (points > hScores[j] && notSet) {
                        newIntArray[j + 1] = points
                        newStringArray[j + 1] = ini
                        i++
                        notSet = false
                    }
                    j++
                    i++
                }
            }

            for (i in 0..9) {
                hScores[i] = newIntArray[i + 1]
                hInitals[i] = newStringArray[i + 1]
            }

            saveHScores()
            showHScores()
            saveHighScores()
        }
    }

    fun saveHScores() {
        val sBI = StringBuilder()
        val sBS = StringBuilder()
        for (i in hScores.indices) {
            sBI.append("" + hScores)
            sBS.append("" + hInitals)
            sBI.append("*")
            sBS.append("*")
        }

    }

    fun showHScores() {
        var res: Int
        var j = 1

        var ident: String

        var person: TextView

        val layoutOne = findViewById(R.id.winScreenLayout) as RelativeLayout
        layoutOne.visibility = View.GONE

        val layoutTwo = findViewById(R.id.highscores) as LinearLayout
        layoutTwo.visibility = View.VISIBLE

        val cont = findViewById(R.id.buttonContinue) as ImageButton
        cont.visibility = View.VISIBLE
        cont.setOnClickListener { view ->
            val menu = Intent(view.context, MainActivity::class.java)
            startActivity(menu)
        }

        for (i in 0..9) {
            ident = "rank$j"
            res = resources.getIdentifier(ident, "id", packageName)
            person = findViewById(res) as TextView
            person.text = "" + hScores[i] + ": " + hInitals[i]
            j++
        }
    }

    fun saveHighScores() {
        var storeScores = ""
        var storeInitals = ""

        scoreEditor = highscores.edit()

        for (i in 0..9) {
            storeScores = storeScores + "*" + hScores[i]
            storeInitals = storeInitals + "*" + hInitals[i]
        }

        scoreEditor.putString("scores", storeScores)
        scoreEditor.putString("initials", storeInitals)
        scoreEditor.commit()

    }

}
