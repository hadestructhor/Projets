package com.example.dominocounter.repository

import androidx.lifecycle.MutableLiveData
import com.example.dominocounter.models.Game
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object GameRepository {

    var listGames = ArrayList<Game>()

}
