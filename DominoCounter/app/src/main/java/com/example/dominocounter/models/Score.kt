package com.example.dominocounter.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class Score(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "score_id") var scoreId : Int,
    @ColumnInfo(name = "game_id") var gameId : Int,
    @ColumnInfo(name = "player_id") var playerId : Int,
    @ColumnInfo(name = "score") var score : Int
)