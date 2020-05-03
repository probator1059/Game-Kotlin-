package com.sndo9.robert.nim

import android.util.Log
import java.util.*

class AI(
        /**
         * Determines if this AI is player 1. true means it is, and false means it isn't
         */
        var isPlayerOne: Boolean) {

    /**
     * This method will select all the sticks that this AI wants to select
     * @param rows a list of all the rows the AI can choose from
     * @return the row that was chosen and had sticks selected from
     */
    fun doTurn_easy(vararg rows: ArrayList<stick>): Int {
        val row = getRand(rows.size - 1)
        Log.w("AI", "I'm taking my turn")

        //Count how many sticks are left in this row
        val totalSticks = 1 + row * 2
        var remainingSticks = totalSticks
        for (s in rows[row]) {
            if (s.isRemoved) remainingSticks--
        }
        if (remainingSticks == 0) {
            //Check to see if all sticks are removed
            if (GameLogic.Companion.checkAllSticksRemoved(*rows)) {
                Log.w("AI", "No more moves")
                return 0
            }
            Log.w("AI", "restarting doTurn")

            //Try randomly selecting another row that hopefully has sticks in it. One ahs to exist
            return doTurn_easy(*rows)
        }
        Log.w("AI", "Remaining Sticks $remainingSticks")
        selectSticksToRemove(rows[row], remainingSticks, totalSticks, true)
        Log.w("AI", "Done Selecting sticks")
        return row
    }

    fun doTurn_hard(vararg rows: ArrayList<stick>): Int {
        Log.w("AI", "" + rows.size)
        if (rows.size < 4) {
            Log.w("AI", "" + rows.size)
            return 3
        }
        Log.w("AI", "I'm taking my turn")

        //Count how many sticks are left in this row
        val remainingSticks: IntArray
        remainingSticks = IntArray(4)
        //Count how many sticks are left in this row
        val totalSticks: IntArray // = 1 + (row * 2);
        totalSticks = IntArray(4)
        //int remainingSticks = totalSticks;
        val n = 4
        for (i in 0..3) {
            totalSticks[i] = 1 + i * 2
            remainingSticks[i] = totalSticks[i]
            for (s in rows[i]) {
                if (s.isRemoved) remainingSticks[i]--
            }
        }

        Log.w("calculate_sumpile_index", "Here")
        var i: Int
        val nim_sum = calculateNimSum(remainingSticks, n)
        Log.w("calculate_sum", "" + nim_sum)
        var pile_index = -1
        var stones_removed = 0
        if (nim_sum != 0) {
            i = 0
            while (i < n) {

                // If this is not an illegal move
                // then make this move.
                if (remainingSticks[i] xor nim_sum < remainingSticks[i]) {
                    pile_index = i
                    Log.w("calculate_sumpile_index", "" + pile_index)
                    stones_removed = remainingSticks[i] - (remainingSticks[i] xor nim_sum)
                    Log.w("calculate_sumremoved", "" + stones_removed)
                    remainingSticks[i] = remainingSticks[i] xor nim_sum
                    break
                }
                i++
            }
            if (pile_index < 0) {
                stones_removed = 1
                i = 0
                while (i < n) {
                    if (remainingSticks[i] > 0) pile_index = i
                    i++
                }
                remainingSticks[pile_index] -= 1
                if (remainingSticks[pile_index] < 0) remainingSticks[pile_index] = 0
            }
        } else {
//             Create an array to hold indices of non-empty piles
//                int[] non_zero_indices;
//                non_zero_indices = new int[n];
//                int count;
//
//                for (i = 0, count = 0; i < n; i++)
//                    if (remainingSticks[i] > 0)
//                        non_zero_indices[count++] = i;
//
////            pile_index = (getRand(n-1) % (count)) + 1;
//                pile_index = (int) (Math.random()) % count;
//                Log.w("calculate_pile_index", "" + pile_index);
//                stones_removed =
//                        1 + (int) (Math.random()) % (remainingSticks[pile_index]);
//                remainingSticks[pile_index] = remainingSticks[pile_index] - stones_removed;
//                Log.w("calculate_stines", "" + stones_removed);
//
//                if (remainingSticks[pile_index] < 0)
//                    remainingSticks[pile_index] = 0;
            stones_removed = 1
            i = 0
            while (i < n) {
                if (remainingSticks[i] > 0) pile_index = i
                i++
            }
            remainingSticks[pile_index] -= 1
            if (remainingSticks[pile_index] < 0) remainingSticks[pile_index] = 0
        }
        //        return;

        Log.w("AI", "Remaining Sticks $remainingSticks")
        selectSticksToRemove(rows[pile_index], stones_removed, totalSticks[pile_index], false)
        Log.w("AI", "Done Selecting sticks")
        return pile_index
    }

    fun calculateNimSum(piles: IntArray, n: Int): Int {
        var i: Int
        var nimsum: Int
        nimsum = if (piles[0] == 1) 0 else 1
        i = 1
        while (i < n) {
            nimsum = nimsum xor piles[i]
            i++
        }
        return nimsum
    }

    /**
     * This method will use the stick's select method to randomly select a stick from a given row
     * @param arrayOne the row that a stick will be selected from
     * @param sticksToRemove how many sticks there are possible to remove
     * @param totalNumSticks the maximum amount of sticks in the row
     */
    private fun selectSticksToRemove(arrayOne: ArrayList<stick>, sticksToRemove: Int, totalNumSticks: Int, easy: Boolean) {
        val numSticks: Int
        numSticks = if (easy) getRand(sticksToRemove - 1) + 1 else sticksToRemove
        //Get how many sticks the AI wants to remove
        Log.w("AI", "Removing $numSticks/$sticksToRemove/$totalNumSticks sticks\n-----------------------")
        for (x in 0 until numSticks) {
            var stick: Int
            do {
                stick = getRand(totalNumSticks - 1)
                Log.w("AI", "Trying to remove $stick")
                //Loop back is choice is invalid
            } while (arrayOne[stick].isRemoved || arrayOne[stick].isSelected)
            Log.w("AI", "Removing stick $stick")
            arrayOne[stick].select()
        }
    }

    /**
     * Helper method to deal with getting a random number.
     * @param seed the seed to multiply the random result by
     * @return a random value between 0-seed
     */
    private fun getRand(seed: Int): Int {
        return Math.round(Math.random() * seed).toInt()
    }

}