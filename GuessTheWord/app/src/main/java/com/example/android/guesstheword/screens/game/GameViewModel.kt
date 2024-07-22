package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    // The current word
    var word = ""

    // The current score
    var score = 0

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        Log.i("GameViewModel", "GameViewModel created!")
        resetList()
        nextWord()
        Log.i("GameViewModel", "Word list: $wordList")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed!")
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            Log.i("GameViewModel", "Word list is EMPTY")
//            gameFinished()
        } else {
            word = wordList.removeAt(0)
            Log.i("GameViewModel", "Setting word to $word")
        }
//        updateWordText()
//        updateScoreText()
    }

    fun onSkip() {
        Log.i("GameViewModel", "onSkip clicked, adjusting score")
        score--
        nextWord()
    }

    fun onCorrect() {
        Log.i("GameViewModel", "onCorrect clicked, adjusting score")
        score++
        nextWord()
    }

}

