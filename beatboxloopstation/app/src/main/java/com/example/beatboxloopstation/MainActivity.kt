package com.example.beatboxloopstation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adapters.DividerItemDecorator
import com.example.adapters.TrackRecyclerAdapter
import com.example.widgets.CustomCheckBox
import kotlinx.android.synthetic.main.track_layout.*

class MainActivity : AppCompatActivity() {

    lateinit var tracksList : List<Int>
    lateinit var trackRecyclerViewAdapter : TrackRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tracksList = listOf(1,2,3,4,5)
        initComponents()
    }

    fun initComponents(){
        initRecyclerViewAdapter()
    }

    fun initRecyclerViewAdapter(){
        trackRecyclerViewAdapter = TrackRecyclerAdapter(this, tracksList)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTrack)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setLayoutManager(linearLayoutManager)

        val drawableDivider = ContextCompat.getDrawable(this, R.drawable.line_divider)
        val DividerItemDecoration = DividerItemDecorator(drawableDivider!!)
        recyclerView.addItemDecoration(DividerItemDecoration)
        recyclerView.adapter = trackRecyclerViewAdapter
    }

}
