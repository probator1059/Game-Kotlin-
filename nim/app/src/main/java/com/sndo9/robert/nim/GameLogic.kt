package com.sndo9.robert.nim

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class GameLogic(c: SinglePlayer, v: View) : AppCompatActivity() {
    /**
     * ArrayList holding first row of sticks
     */
    protected var arrayOne = ArrayList<stick>()

    /**
     * ArrayList holding second row of sticks
     */
    protected var arrayTwo = ArrayList<stick>()

    /**
     * ArrayList holding third row of sticks
     */
    protected var arrayThree = ArrayList<stick>()

    /**
     * ArrayList holding forth row of sticks
     */
    protected var arrayFour = ArrayList<stick>()

    /**
     * Instance of the computer AI
     */
    protected var computer: AI

    /**
     * Imagebutton for confirm
     */
    protected var confirm: ImageButton

    /**
     * Imagebutton for cancel
     */
    protected var cancel: ImageButton

    /**
     * Imagebutton for icons
     */
    protected var icons: ImageButton? = null

    /**
     * Textview for telling the user whos turn it is
     */
    protected var infoTextField: TextView

    /**
     * Textview for number of turns
     */
    protected var numOfTurns: TextView

    /**
     * Holds the view of the Activity that calls GameLogic
     */
    protected var view: View

    /**
     * Holds activity that calls this function
     */
    protected var context: SinglePlayer

    /**
     * Boolean keeping track of who's turn it is
     */
    protected var isPlayerOne: Boolean

    /**
     * variable tracking if a stick has been selected
     */
    protected var hasSelected: Boolean

    /**
     * Imageview holding the new icon
     */
    protected var newIcon: ImageView? = null

    /**
     * Count of the number of existing sticks
     */
    protected var count: Int

    /**
     * The Points left.
     */
    protected var ptsLeft: Int

    /**
     * The Score
     */
    protected var score: Int

    /**
     * The number of turns.
     */
    protected var turns = 0

    /**
     * Boolean of if the ai is on
     */
    private var useAI: Boolean
    private var level_easy: Boolean

    /**
     * Tracks if the game is over
     */
    private var isOver = false

    /**
     * shared prefrences for the save state of the game
     */
    protected var save: SharedPreferences

    /**
     * The save state prefrences
     */
    protected var editSave: Editor

    /**
     * Method to start the game
     */
    fun startGame() {
        var identifier: String
        var res: Int
        var newImageView: ImageView

        // Create Row One
        run {
            identifier = "stick14"
            res = context.resources.getIdentifier(identifier, "id", context.packageName)
            newImageView = view.findViewById<View>(res) as ImageView
            val newStick = stick(newImageView, 1, 4, context)
            newImageView.setOnClickListener {
                if (!newStick.isSelected) {
                    selectStick(newStick)
                } else {
                    unSelectStick(newStick)
                }
            }
            arrayOne.add(newStick)
        }

        //Creates Row two
        for (i in 3..5) {
            identifier = "stick2$i"
            res = context.resources.getIdentifier(identifier, "id", context.packageName)
            newImageView = view.findViewById<View>(res) as ImageView
            val newStick = stick(newImageView, 2, i, context)
            newImageView.setOnClickListener {
                if (!newStick.isSelected) {
                    selectStick(newStick)
                } else {
                    unSelectStick(newStick)
                }
            }
            arrayTwo.add(newStick)
        }
        //creates Row three
        for (i in 2..6) {
            identifier = "stick3$i"
            res = context.resources.getIdentifier(identifier, "id", context.packageName)
            newImageView = view.findViewById<View>(res) as ImageView
            val newStick = stick(newImageView, 3, i, context)
            newImageView.setOnClickListener {
                if (!newStick.isSelected) {
                    selectStick(newStick)
                } else {
                    unSelectStick(newStick)
                }
            }
            arrayThree.add(newStick)
        }
        //creates Row four
        for (i in 1..7) {
            identifier = "stick4$i"
            res = context.resources.getIdentifier(identifier, "id", context.packageName)
            newImageView = view.findViewById<View>(res) as ImageView
            val newStick = stick(newImageView, 4, i, context)
            newImageView.setOnClickListener {
                if (!newStick.isSelected) {
                    selectStick(newStick)
                } else {
                    unSelectStick(newStick)
                }
            }
            arrayFour.add(newStick)
        }
        //If the AI is on resume the previous game state if there is one
        if (useAI) resumeState()

        //Cancel and confirm touch listeners
        confirm.setOnClickListener { //Next turn
            endTurn(arrayOne, arrayTwo, arrayThree, arrayFour)
            isPlayerOne = !isPlayerOne
            if (useAI) {
                if (computer.isPlayerOne != isPlayerOne) {
                    infoTextField.text = "Computer's Turn"
                    val handler = Handler()
                    handler.postDelayed({
                        val rowSelected: Int
                        rowSelected = if (level_easy) computer.doTurn_easy(arrayOne, arrayTwo, arrayThree, arrayFour) else computer.doTurn_hard(arrayOne, arrayTwo, arrayThree, arrayFour)
                        endTurn(arrayOne, arrayTwo, arrayThree, arrayFour)
                        isPlayerOne = !isPlayerOne
                        infoTextField.text = "Your turn"
                        turns++
                        Log.w("GameLogic.turns", "" + turns)
                    }, 1000)
                }
            } else {
                if (isPlayerOne) {
                    infoTextField.text = "Player 1's turn"
                    turns++
                    Log.w("GameLogic.turns", "" + turns)
                } else {
                    infoTextField.text = "Player 2's turn"
                }
            }
        }
        //Resets the sticks
        cancel.setOnClickListener { resetSelect() }
    }

    /**
     * Register touch of a stick
     *
     * @param row      the row of the stick
     * @param position the position of the stick
     */
    fun registerTouch(row: Int, position: Int) {
        count = count + 1
        enableButtons()
        if (row == 1) {
            for (i in arrayTwo.indices) {
                if (arrayTwo[i].isSelected) {
                    unSelectStick(arrayTwo[i])
                }
            }
            for (i in arrayThree.indices) {
                if (arrayThree[i].isSelected) {
                    unSelectStick(arrayThree[i])
                }
            }
            for (i in arrayFour.indices) {
                if (arrayFour[i].isSelected) {
                    unSelectStick(arrayFour[i])
                }
            }
        }
        if (row == 2) {
            for (i in arrayOne.indices) {
                if (arrayOne[i].isSelected) {
                    unSelectStick(arrayOne[i])
                }
            }
            for (i in arrayThree.indices) {
                if (arrayThree[i].isSelected) {
                    unSelectStick(arrayThree[i])
                }
            }
            for (i in arrayFour.indices) {
                if (arrayFour[i].isSelected) {
                    unSelectStick(arrayFour[i])
                }
            }
        }
        if (row == 3) {
            for (i in arrayOne.indices) {
                if (arrayOne[i].isSelected) {
                    unSelectStick(arrayOne[i])
                }
            }
            for (i in arrayTwo.indices) {
                if (arrayTwo[i].isSelected) {
                    unSelectStick(arrayTwo[i])
                }
            }
            for (i in arrayFour.indices) {
                if (arrayFour[i].isSelected) {
                    unSelectStick(arrayFour[i])
                }
            }
        }
        if (row == 4) {
            for (i in arrayOne.indices) {
                if (arrayOne[i].isSelected) {
                    unSelectStick(arrayOne[i])
                }
            }
            for (i in arrayTwo.indices) {
                if (arrayTwo[i].isSelected) {
                    unSelectStick(arrayTwo[i])
                }
            }
            for (i in arrayThree.indices) {
                if (arrayThree[i].isSelected) {
                    unSelectStick(arrayThree[i])
                }
            }
        }
    }

    /**
     * Unselects given stick
     * @param selection stick to be unselected
     */
    private fun unSelectStick(selection: stick) {
        if (selection.unSelect()) if (count != 0) count = count - 1
        if (count == 0) disableButtons()
    }

    /**
     * Selects given stick
     * @param selection stick to be selected
     */
    private fun selectStick(selection: stick) {
        if (selection.select()) {
            registerTouch(selection.row, selection.position)
        }
    }

    /**
     * Disables confirm and cancel buttons
     */
    fun unTouch() {
        if (count > 0) count = count - 1
        if (count == 0) disableButtons()
    }

    /**
     * Disable buttons.
     */
    fun disableButtons() {
        confirm.isEnabled = false
        confirm.setImageDrawable(context.getDrawable(R.drawable.circlethin))
        cancel.isEnabled = false
        cancel.setImageDrawable(context.getDrawable(R.drawable.circlethinblack))
    }

    /**
     * Enable buttons.
     */
    fun enableButtons() {
        confirm.isEnabled = true
        confirm.setImageDrawable(context.getDrawable(R.drawable.checkedblue))
        cancel.isEnabled = true
        cancel.setImageDrawable(context.getDrawable(R.drawable.cancel))
    }

    /**
     * Reset selection
     */
    fun resetSelect() {
        resetSelectRow(arrayOne)
        resetSelectRow(arrayTwo)
        resetSelectRow(arrayThree)
        resetSelectRow(arrayFour)
        disableButtons()
    }

    /**
     * Reset selected list
     *
     * @param list the list to be reset
     */
    fun resetSelectRow(list: ArrayList<stick>) {
        for (i in list.indices) unSelectStick(list[i])
    }

    /**
     * End turn.
     *
     * @param stacksToCheck the stacks to check
     */
    fun endTurn(vararg stacksToCheck: ArrayList<stick>) {
        for (stack in stacksToCheck) {
            for (s in stack) {
                if (s.isSelected) {
                    s.remove()
                    ptsLeft = ptsLeft - 1
                }
            }
        }
        disableButtons()
        Log.w("saveState", "Near")
        if (useAI && !isPlayerOne) {
            saveState()
            numOfTurns.text = "Number of Turns: $turns"
        }
        checkWin()
    }

    /**
     * Check win.
     */
    fun checkWin() {
        if (checkAllSticksRemoved(arrayOne, arrayTwo, arrayThree, arrayFour)) {
            if (!isOver) {
                itsOverGoHome()
                context.endGame(isPlayerOne, context, turns, useAI)
            }
        }
    }

    /**
     * pauses all icons
     */
    fun pause() {
        pauseRow(arrayOne)
        pauseRow(arrayTwo)
        pauseRow(arrayThree)
        pauseRow(arrayFour)
    }

    /**
     * unpauses all icons
     */
    fun unPause() {
        unPauseRow(arrayOne)
        unPauseRow(arrayTwo)
        unPauseRow(arrayThree)
        unPauseRow(arrayFour)
    }

    /**
     * Turns on te Ai
     *
     * @param playWithAI the play with ai
     */
    fun turnAiOn(playWithAI: Boolean, pc_level: Boolean) {
        useAI = playWithAI
        level_easy = pc_level
    }

    /**
     * Sets isOver
     * Its over go home.
     */
    fun itsOverGoHome() {
        isOver = true
    }

    /**
     * Saves game state to shared prefrences
     */
    fun saveState() {
        Log.w("saveState", "Reached")
        var rowString = ""
        for (i in arrayOne.indices) {
            rowString = rowString + arrayOne[i].toString()
        }
        editSave.putString("rowOneSave", rowString)
        rowString = ""
        for (i in arrayTwo.indices) {
            rowString = rowString + arrayTwo[i].toString()
        }
        editSave.putString("rowTwoSave", rowString)
        rowString = ""
        for (i in arrayThree.indices) {
            rowString = rowString + arrayThree[i].toString()
        }
        Log.w("-----------saving", rowString)
        editSave.putString("rowThreeSave", rowString)
        rowString = ""
        for (i in arrayFour.indices) {
            rowString = rowString + arrayFour[i].toString()
        }
        Log.w("-----------saving", rowString)
        editSave.putString("rowFourSave", rowString)
        editSave.putString("numTurns", "" + turns)
        editSave.commit()
        if (isOver) {
            editSave.remove("rowOneSave")
            editSave.remove("rowTwoSave")
            editSave.remove("rowThreeSave")
            editSave.remove("rowFourSave")
            editSave.remove("numTurns")
            editSave.commit()
        }
    }

    /**
     * Resume state from shared prefrences
     */
    fun resumeState() {
        //Fixes close on win screen
        if (save.getString("rowOneSave", "0") == "1" && save.getString("rowTwoSave", "000") == "111" && save.getString("rowThreeSave", "00000") == "11111" && save.getString("rowFourSave", "1111111") == "1111111") return
        var rowString = save.getString("rowOneSave", "0")
        for (i in arrayOne.indices) {
            arrayOne[i].fromString(Character.getNumericValue(rowString!![i]))
        }
        Log.w("-----------resuming", rowString)
        rowString = save.getString("rowTwoSave", "000")
        for (i in arrayTwo.indices) {
            arrayTwo[i].fromString(Character.getNumericValue(rowString!![i]))
        }
        Log.w("-----------resuming", rowString)
        rowString = save.getString("rowThreeSave", "00000")
        for (i in arrayThree.indices) {
            arrayThree[i].fromString(Character.getNumericValue(rowString!![i]))
        }
        Log.w("-----------resuming", rowString)
        rowString = save.getString("rowFourSave", "0000000")
        for (i in arrayFour.indices) {
            arrayFour[i].fromString(Character.getNumericValue(rowString!![i]))
        }
        Log.w("-----------resuming", rowString)
        turns = save.getString("numTurns", "1")!!.toInt()
    }

    /**
     * Changes icon.
     *
     * @param newPic the pic to be changed to
     */
    fun changeIcon(newPic: Drawable?) {
        for (i in arrayOne.indices) {
            arrayOne[i].changeImage(newPic)
        }
        for (i in arrayTwo.indices) {
            arrayTwo[i].changeImage(newPic)
        }
        for (i in arrayThree.indices) {
            arrayThree[i].changeImage(newPic)
        }
        for (i in arrayFour.indices) {
            arrayFour[i].changeImage(newPic)
        }
    }

    companion object {
        /**
         * Check all sticks removed boolean.
         *
         * @param rows the rows
         * @return the true if all sticks are removed
         */
        fun checkAllSticksRemoved(vararg rows: ArrayList<stick>): Boolean {
            for (r in rows) {
                for (s in r) {
                    if (!s.isRemoved) return false
                }
            }
            return true
        }

        /**
         * Pauses icons in a row.
         *
         * @param list the list
         */
        fun pauseRow(list: ArrayList<stick>) {
            for (i in list.indices) list[i].disable()
        }

        /**
         * unpauses all icons in a row
         *
         * @param list the list
         */
        fun unPauseRow(list: ArrayList<stick>) {
            for (i in list.indices) list[i].enable()
        }
    }

    /**
     * Instantiates a new Game logic.
     *
     * @param context of prevous activity
     * @param view of previous activity
     */
    init {
        score = turns
        ptsLeft = score
        count = ptsLeft
        context = c
        view = v
        computer = AI(true)
        isPlayerOne = true
        hasSelected = false
        useAI = true
        level_easy = true

        //Icon drawer listener
        val getClickDrawable = View.OnClickListener { view ->
            newIcon = view as ImageView
            changeIcon(newIcon!!.drawable)
            context.findViewById(R.id.gameLayout).visibility = View.VISIBLE
            context.findViewById(R.id.iconDrawer).visibility = View.GONE
            //                context.findViewById(R.id.buttonChangIcon).setVisibility(View.VISIBLE);
            context.findViewById(R.id.buttonInformation).visibility = View.VISIBLE
            context.findViewById(R.id.textNumOfTurns).visibility = View.VISIBLE
        }

        //Icon Drawer
        val word = "icon"
        //Favorite line of code in this project
//        for (i in 0..7) view.findViewById<View>(context.resources.getIdentifier(word + i, "id", context.packageName)).setOnClickListener(getClickDrawable)

        //Find and hold buttons
        confirm = view.findViewById<View>(R.id.buttonConfirm) as ImageButton
        cancel = view.findViewById<View>(R.id.buttonCancel) as ImageButton
        //        icons = (ImageButton)view.findViewById(R.id.buttonChangIcon);
//        icons.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                context.findViewById(R.id.gameLayout).setVisibility(View.GONE);
//                context.findViewById(R.id.iconDrawer).setVisibility(View.VISIBLE);
////                context.findViewById(R.id.buttonChangIcon).setVisibility(View.GONE);
//                context.findViewById(R.id.buttonInformation).setVisibility(View.GONE);
//                context.findViewById(R.id.textNumOfTurns).setVisibility(View.GONE);
//            }
//        });
        infoTextField = view.findViewById<View>(R.id.helpText) as TextView
        infoTextField.text = "Player 1's turn"
        numOfTurns = view.findViewById<View>(R.id.textNumOfTurns) as TextView
        save = context.getSharedPreferences("save", 0)
        editSave = save.edit()
    }
}


