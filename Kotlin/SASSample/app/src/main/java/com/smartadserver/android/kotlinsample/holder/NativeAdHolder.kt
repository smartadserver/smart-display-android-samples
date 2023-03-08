package com.smartadserver.android.kotlinsample.holder

import android.graphics.Bitmap
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.smartadserver.android.kotlinsample.databinding.ListNativeAdBinding

class NativeAdHolder(binding: ListNativeAdBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val indexTextView = binding.indexLabel
    private val titleTextView = binding.titleLabel
    private val subtitleTextView = binding.subtitleLabel
    private val button = binding.callToActionButton
    private val subParentLayout = binding.subParentLayout
    private val adOptionsLayout = binding.adOptionsLayout

    // Optional, only for items that are ads.
    val iconImageView: ImageView = binding.iconImageView

    fun configureForContent(title: String, subtitle: String, position: Int) {
        titleTextView.text = title
        subtitleTextView.text = subtitle
        setIndex(position)

        subParentLayout.visibility = View.VISIBLE
        iconImageView.visibility = View.GONE
        titleTextView.gravity = Gravity.START
        adOptionsLayout.visibility = View.GONE
    }

    fun configureForAd(title: String?, subtitle: String?, callToAction: String?, image: Bitmap?) {
        titleTextView.text = title
        subtitleTextView.text = subtitle
        setIndex(0)
        button.text = callToAction

        if (image != null) {
            iconImageView.setImageBitmap(image)
            iconImageView.visibility = View.VISIBLE
        }

        adOptionsLayout.visibility = View.VISIBLE
    }

    private fun setIndex(index: Int) {
        if (index > 0) {
            indexTextView.visibility = View.VISIBLE
            subtitleTextView.setTextColor(Color.parseColor("#BBBBBB"))
            indexTextView.text = "$index"
        } else {
            indexTextView.visibility = View.GONE
            subtitleTextView.setTextColor(Color.parseColor("#ff009688"))
            indexTextView.text = ""
        }
    }
}