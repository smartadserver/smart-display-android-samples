package com.smartadserver.android.kotlinsample.holder

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartadserver.android.kotlinsample.databinding.ListItemBinding

class ListItemHolder(private val binding: ListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val indexTextView: TextView = binding.indexTextView
    private val titleTextView: TextView = binding.titleTextView
    private val subtitleTextView: TextView = binding.subtitleTextView

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