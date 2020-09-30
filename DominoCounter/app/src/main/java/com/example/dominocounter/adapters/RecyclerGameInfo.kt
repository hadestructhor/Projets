package com.example.dominocounter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dominocounter.R
import com.example.dominocounter.models.Game

class RecyclerGameInfo (var context : Context, var listGames: List<Game>) : RecyclerView.Adapter<RecyclerGameInfo.GameInfoViewHolder>() {
    class GameInfoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var gameNameTv: TextView
        var firstPlayerTv: TextView
        var secondPlayerTv: TextView
        var thirdPlayerTv: TextView
        var fourthPlayerTv: TextView

        init {
            gameNameTv = itemView.findViewById(R.id.gameNameTv)
            firstPlayerTv = itemView.findViewById(R.id.firstPlayerTv)
            secondPlayerTv = itemView.findViewById(R.id.secondPlayerTv)
            thirdPlayerTv = itemView.findViewById(R.id.thirdPlayerTv)
            fourthPlayerTv = itemView.findViewById(R.id.fourthPlayerTv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_game, parent, false)
        val holder = GameInfoViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return listGames.size
    }

    override fun onBindViewHolder(holder: GameInfoViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}