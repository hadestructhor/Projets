package com.example.dominocounter.activities

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.dominocounter.R
import com.example.dominocounter.models.Game

class CreateGame : AppCompatActivity() {
    lateinit var createGameBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        initComponents()
    }

    fun initComponents(){
        createGameBtn = findViewById(R.id.createGameBtn)
        createGameBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                verifyInputs()
            }})

        val numberGameTv = findViewById<TextView>(R.id.numberGameTv)
        numberGameTv.setText("Game " + (Game.numberGame + 1).toString())
    }

    private fun verifyInputs() {
        val txtETGameName= findViewById<EditText>(R.id.eTGameName).text.toString().trim()
        val txtETMaximum = findViewById<EditText>(R.id.eTMaximum).text.toString().trim()
        val txtETPlayer1 = findViewById<EditText>(R.id.eTPlayer1).text.toString().trim()
        val txtETPlayer2 = findViewById<EditText>(R.id.eTPlayer2).text.toString().trim()
        val txtETPlayer3 = findViewById<EditText>(R.id.eTPlayer3).text.toString().trim()
        val txtETPlayer4 = findViewById<EditText>(R.id.eTPlayer4).text.toString().trim()

        //verification of players
        var nbPlayers = 0
        var listPlayers : ArrayList<String> = ArrayList()

        if (!txtETPlayer1.isNullOrBlank()){
            listPlayers.add(txtETPlayer1)
            nbPlayers++
        }

        if (!txtETPlayer2.isNullOrBlank()){
            listPlayers.add(txtETPlayer2)
            nbPlayers++
        }

        if (!txtETPlayer3.isNullOrBlank()){
            listPlayers.add(txtETPlayer3)
            nbPlayers++
        }

        if (!txtETPlayer4.isNullOrBlank()){
            listPlayers.add(txtETPlayer4)
            nbPlayers++
        }

        //verification of game name
        var gameName = "Game n°"
        if (!txtETGameName.isNullOrBlank()){
            gameName = txtETGameName
        }else {
            gameName+= (Game.numberGame + 1).toString()
        }

        //verification of max score
        var maxScore = getString(R.string.default_max_score).toInt()
        if (!txtETMaximum.isNullOrBlank()){
            if(txtETMaximum.toIntOrNull() != null ){
                val number = txtETMaximum.toInt()
                if(number > 0) maxScore = number
                else{
                    maxScore = getString(R.string.default_max_score).toInt()
                }
            }
        }else {
            maxScore = getString(R.string.default_max_score).toInt()
        }

        if(nbPlayers>=2 && nbPlayers<=4){
            var listScores = ArrayList<Int>()
            for (i in 0..nbPlayers-1) listScores.add(0)

            val game = Game(gameName, listPlayers, listScores, maxScore)

            println(listPlayers)
            println(listScores)

            val startGameIntent = Intent(this, PlayGame::class.java)
            startGameIntent.putExtra("game", game)
            startActivity(startGameIntent)
        }else Toast.makeText(this, getString(R.string.not_enough_players), Toast.LENGTH_SHORT).show()
    }
}