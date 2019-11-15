package com.smartadserver.android.kotlinsample.holder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartadserver.android.kotlinsample.R
import kotlinx.android.synthetic.main.list_item.view.*

class ListItemHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {

    private val indexTextView: TextView = itemView.indexTextView
    private val titleTextView: TextView = itemView.titleTextView
    private val subtitleTextView: TextView = itemView.subtitleTextView

    private fun setIndexText(index: Int) {
        if (index > 0) {
            indexTextView.visibility = View.VISIBLE
            indexTextView.text = index.toString()
            subtitleTextView.setTextColor(Color.parseColor("#bbbbbb"))
        } else {
            indexTextView.visibility = View.GONE
            subtitleTextView.setTextColor(Color.parseColor("#ff009688"))
        }
    }

    private fun setTitleText(title: String) {
        titleTextView.text = title
    }

    private fun setSubtitleText(subtitle: String) {
        subtitleTextView.text = subtitle
    }

    fun configureForItem(title: String, subtitle: String, position: Int) {
        setTitleText(title)
        setSubtitleText(subtitle)
        setIndexText(position)
    }
}