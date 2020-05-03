package com.sndo9.robert.nim

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class level : AppCompatActivity() {
    private var singlePlayer: Intent? = null
    private var levelDialog: Dialog? = null
    private var btnEasy: Button? = null
    private var btnHard: Button? = null
    var level_easy = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level_popup)

//        btnEasy = new Intent(this, SinglePlayer.class);
//        btnEasy.putExtra(SinglePlayer.WITH_AI, true);
//        multiplayer = new Intent(this, SinglePlayer.class);
//        multiplayer.putExtra(SinglePlayer.WITH_AI, false);
        levelDialog = Dialog(this)
        btnEasy = findViewById(R.id.btnEasy) as Button
        btnHard = findViewById(R.id.btnHard) as Button
        singlePlayer = Intent(this, SinglePlayer::class.java)
        singlePlayer!!.putExtra(SinglePlayer.Companion.WITH_AI, true)
        //        singlePlayer.putExtra(SinglePlayer.LEVEL_EASY, true);
        btnEasy!!.setOnClickListener {
            level_easy = true
            //                singlePlayer = new Intent(this, SinglePlayer.class);
//                singlePlayer.putExtra(SinglePlayer.WITH_AI, true);
            singlePlayer!!.putExtra(SinglePlayer.Companion.LEVEL_EASY, true)
            startActivity(singlePlayer)
        }
        btnHard!!.setOnClickListener {
            level_easy = false
            //                singlePlayer = new Intent(this, SinglePlayer.class);
//                singlePlayer.putExtra(SinglePlayer.WITH_AI, true);
            singlePlayer!!.putExtra(SinglePlayer.Companion.LEVEL_EASY, false)
            startActivity(singlePlayer)
        }
    }
}