package com.sndo9.robert.nim

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button

/**
 * This is the main screen for the app. It allows the user to choose whether to face another player or an AI
 */
class MainActivity : AppCompatActivity() {
    /**
     * Button allowing user to pick AI
     */
    private var single: Button? = null

    /**
     * Button allowing user to pick play with a friend
     */
    private var multi: Button? = null

    /**
     * Intent for the single player
     */
    private var singlePlayer: Intent? = null

    /**
     * Intent for multiplayer
     */
    private var multiplayer: Intent? = null
    private var Level: Intent? = null
    var level_easy = true

    /**
     * Creates the screen and interface for the main screen
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        levelDialog = new Dialog(this);
//        btnEasy = (Button) findViewById(R.id.btnEasy);
//        btnHard = (Button) findViewById(R.id.btnHard);
//
//        btnEasy.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                level_easy = true;
//            }
//        });
//
//        btnHard.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                level_easy = false;
//            }
//        });
        //Set intents and bundle boolean to specify opponent
        singlePlayer = Intent(this, SinglePlayer::class.java)
        singlePlayer!!.putExtra(SinglePlayer.Companion.WITH_AI, true)
        multiplayer = Intent(this, SinglePlayer::class.java)
        multiplayer!!.putExtra(SinglePlayer.Companion.WITH_AI, false)
        Level = Intent(this, level::class.java)

        //Attach UI to Button variables
        single = findViewById(R.id.singleplayer) as Button
        multi = findViewById(R.id.multiplayer) as Button
        f = supportFragmentManager
        //Launch game with AI playing the user
        single!!.setOnClickListener { //                startActivity(singlePlayer);
//                ShowLevelPopup();
            startActivity(Level)
        }
        //Launch game with two players playing
        multi!!.setOnClickListener { startActivity(multiplayer) }
    } //    public void  ShowLevelPopup(){

    //        levelDialog.setContentView(R.layout.level_popup);
    //        levelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    //        levelDialog.show();
    //    }
    companion object {
        protected var f: FragmentManager? = null
        protected var iPage = Instruction_Page()
    }
}