package com.example.dominocounter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dominocounter.R
import com.example.dominocounter.models.Game

class RecyclerGameScore (var context : Context, var game : Game, val addBtnValue1 : Int = 5, val addBtnValue2 : Int = 10) : RecyclerView.Adapter<RecyclerGameScore.GameScoreViewHolder>() {
    class GameScoreViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var playerName: TextView
        var playerScore: TextView

        var addBtn1: TextView
        var addBtn2: TextView
        var subBtn1: TextView
        var subBtn2: TextView

        init {
            playerName = itemView.findViewById(R.id.tvPlayerName)
            playerScore = itemView.findViewById(R.id.tvPlayerScore)

            addBtn1 = itemView.findViewById(R.id.addBtn1)
            addBtn2 = itemView.findViewById(R.id.addBtn2)
            subBtn1 = itemView.findViewById(R.id.subBtn1)
            subBtn2 = itemView.findViewById(R.id.subBtn2)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_score, parent, false)
        val holder = GameScoreViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return game.players.size
    }

    override fun onBindViewHolder(holderScore: GameScoreViewHolder, position: Int) {

        holderScore.playerName.setText(game.players.get(position))

        holderScore.playerScore.setText(game.scores.get(position).toString())

        holderScore.addBtn1.setText("+" + addBtnValue1.toString())
        holderScore.addBtn2.setText("+" + addBtnValue2.toString())
        holderScore.subBtn1.setText("-" + addBtnValue1.toString())
        holderScore.subBtn2.setText("-" + addBtnValue2.toString())

        holderScore.addBtn1.setOnClickListener{
            game.addPoints(position, addBtnValue1)
            holderScore.playerScore.setText(game.scores.get(position).toString())
        }

        holderScore.addBtn2.setOnClickListener{
            game.addPoints(position, addBtnValue2)
            holderScore.playerScore.setText(game.scores.get(position).toString())
        }

        holderScore.subBtn1.setOnClickListener{
            game.removePoints(position, addBtnValue1)
            holderScore.playerScore.setText(game.scores.get(position).toString())
        }

        holderScore.subBtn2.setOnClickListener{
            game.removePoints(position, addBtnValue2)
            holderScore.playerScore.setText(game.scores.get(position).toString())
        }
    }
}