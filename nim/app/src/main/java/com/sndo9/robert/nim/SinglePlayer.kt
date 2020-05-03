package com.sndo9.robert.nim

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.sndo9.robert.nim.Instruction_Page.OnFragmentInteractionListener

/**
 * This activity creates the game and decides if the AI is to be used
 */
class SinglePlayer : AppCompatActivity(), OnFragmentInteractionListener {
    /**
     * The Running score, gets passed on to win screen to record score.
     */
    protected var runningScore = 0

    /**
     * Boolean recording if the information page is open
     */
    protected var pageOpen = false

    /**
     * Instance of the game logic
     */
    protected var logic: GameLogic? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_player)

        //Getting extras from intent call. If extra exists set playWithAI
        val call = intent
        val extras = call.extras
        var playWithAI = true // Default to true
        var level_easy = true
        if (extras != null) {
            if (extras.containsKey(WITH_AI)) playWithAI = extras.getBoolean(WITH_AI)
            if (extras.containsKey(LEVEL_EASY)) level_easy = extras.getBoolean(LEVEL_EASY)
        }
        //Get prefrences from save file and set running score
        val save = getSharedPreferences("save", 0)
        runningScore = save.getString("points", "0")!!.toInt()
        //Create game logic instance for user to play
        logic = GameLogic(this, findViewById(R.id.activity_single_player))
        logic!!.turnAiOn(playWithAI, level_easy)

        //Set fragment manager, button, and click listener to open and close the instruction page
        val f = supportFragmentManager
        information = findViewById(R.id.buttonInformation) as Button
        information!!.setOnClickListener {
            if (!pageOpen) {
                pageOpen = true
                logic!!.pause()
                val fT = f.beginTransaction()
                fT.add(R.id.container, iPage, "hi")
                fT.commit()
            } else {
                pageOpen = false
                logic!!.unPause()
                val f = supportFragmentManager
                val fT = f.beginTransaction()
                fT.remove(iPage)
                fT.commit()
            }
        }
        logic!!.startGame()
    }

    /**
     * End game.
     *
     * @param playerOne Boolean telling endGame if it was player one's turn when called
     * @param c         Context of activity calling endgame
     * @param turns     The number of turns that it took someone to win
     * @param isAI      Boolean telling endGame if the AI was playing
     */
    fun endGame(playerOne: Boolean?, c: Context, turns: Int, isAI: Boolean?) {
        var turns = turns
        val passingTurns = turns++ + 1
        //Create intent for the win screen and bundle information needed for the score screen
        val goToWin = Intent(c, WinScreen::class.java)
        val extra = Bundle()
        extra.putInt("score", runningScore)
        extra.putBoolean("winner", playerOne!!)
        extra.putInt("numTurns", passingTurns)
        extra.putBoolean("AI", isAI!!)
        goToWin.putExtras(extra)
        c.startActivity(goToWin)
    }

    override fun onFragmentInteraction(uri: Uri?) {
        //you can leave it empty
    }

    companion object {
        /**
         * The constant WITH_AI. This is stores the string tag for the extra specifying AI usage
         */
        const val WITH_AI = "PLAY_WITH_THE_AI_ON"
        const val LEVEL_EASY = "EASY LEVEL"

        /**
         * Holds the information button
         */
        private var information: Button? = null

        /**
         * Holds the fragment manager used for the information page
         */
        var f: FragmentManager? = null

        /**
         * Holds the fragment for the information page
         */
        protected var iPage = Instruction_Page()
    }
}