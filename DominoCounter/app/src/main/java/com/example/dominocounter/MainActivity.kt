package com.example.dominocounter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.dominocounter.activities.CreateGame
import com.example.dominocounter.adapters.RecyclerGameScore
import com.example.dominocounter.viewmodels.GameViewModel

class MainActivity : AppCompatActivity() {
    lateinit var recyclerViewAdapterScore : RecyclerGameScore
    lateinit var addGameButton : Button
//    var gameViewModel : ViewModelProvider.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
    }

    fun initComponents(){
        addGameButton = findViewById(R.id.btnAddGame)
        addGameButton.setOnClickListener {
            var addgame = Intent(this, CreateGame::class.java)
            startActivity(addgame)
        }
    }
}