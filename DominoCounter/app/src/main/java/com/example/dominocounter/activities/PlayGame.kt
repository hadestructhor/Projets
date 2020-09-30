package com.example.dominocounter.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dominocounter.R
import com.example.dominocounter.adapters.RecyclerGameScore
import com.example.dominocounter.models.Game

class PlayGame : AppCompatActivity() {
    val addBtnValue1 = 5
    val addBtnValue2 = 10
    lateinit var game : Game
    lateinit var recyclerScoreAdapter : RecyclerGameScore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)
        initComponents()
    }

    fun initComponents(){
        game = (intent.getSerializableExtra("game") as? Game)!!

        val gameNameTv = findViewById<TextView>(R.id.gameNameTv)
        gameNameTv.setText(game.gameName)

        initRecyclerViewAdapter()
    }

    fun initRecyclerViewAdapter(){

        recyclerScoreAdapter = RecyclerGameScore(this, game, addBtnValue1, addBtnValue2)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewScores)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.setLayoutManager(linearLayoutManager)

        recyclerView.adapter = recyclerScoreAdapter
    }
}