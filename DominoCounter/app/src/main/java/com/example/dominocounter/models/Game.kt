package com.example.dominocounter.models

import java.io.Serializable

class Game(var gameName : String, var players : ArrayList<String>, var scores: ArrayList<Int>, var maxScore : Int = 550, var winner : Int = 0) : Serializable {

    companion object {
        @JvmStatic var numberGame : Long = 0
    }

    init {
        if(numberGame<Long.MAX_VALUE) numberGame+=1
        else numberGame = 0
    }

    fun addPoints(position : Int, points: Int){
        scores[position] += points
        if(scores[position] >= maxScore) winner = position
    }

    fun removePoints(position : Int, points: Int){
        scores[position] -= points
        if(scores[position] <= maxScore && winner == position)  winner = 0
    }

}