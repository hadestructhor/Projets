package com.example.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beatboxloopstation.R
import com.example.widgets.CustomCheckBox
import com.example.widgets.VerticalSeekBar

class TrackRecyclerAdapter(var context: Context, var tracksList : List<Int>) : RecyclerView.Adapter<TrackRecyclerAdapter.TrackViewHolder>()  {

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var deleteTrackButton: Button
        var editTrackCustomCheckBox: CustomCheckBox
        var trackNumberTv: TextView
        var trackVolumeVerticalSeekBar: VerticalSeekBar
        var playButton: Button

        init {
            deleteTrackButton = itemView.findViewById(R.id.deleteTrackButton)
            editTrackCustomCheckBox = itemView.findViewById(R.id.editTrack)
            trackNumberTv = itemView.findViewById(R.id.trackNumber)
            trackVolumeVerticalSeekBar = itemView.findViewById(R.id.trackVolume)
            playButton = itemView.findViewById(R.id.playButton)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
        val holder = TrackViewHolder(view)

        return holder
    }

    override fun getItemCount(): Int {
        return tracksList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.trackNumberTv.setText(tracksList[position].toString())
        holder.trackNumberTv.setTypeface(holder.trackNumberTv.typeface, Typeface.BOLD)
    }
}