package com.example.dominocounter.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "player_id") var playerId : Int,
    @ColumnInfo(name = "player_name") var playerName : String?
)