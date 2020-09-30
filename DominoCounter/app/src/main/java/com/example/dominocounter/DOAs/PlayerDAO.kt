package com.example.dominocounter.DOAs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.dominocounter.models.Player

@Dao
interface PlayerDAO {

    @Query("SELECT * FROM players")
    fun getAll(): Array<Player>

    @Query("SELECT * FROM players WHERE player_name LIKE :playerName")
    fun findByName(playerName: String): Player

    @Query("SELECT * FROM players WHERE player_id LIKE :playerId")
    fun findById(playerId: Int): Player

    @Insert
    fun insertAll(vararg player: Player)

    @Delete
    fun delete(user: Player)
}