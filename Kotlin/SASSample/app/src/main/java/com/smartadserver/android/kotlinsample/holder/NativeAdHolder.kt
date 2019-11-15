package com.smartadserver.android.kotlinsample.holder

import android.graphics.Bitmap
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.smartadserver.android.kotlinsample.R
import kotlinx.android.synthetic.main.list_native_ad.view.*

class NativeAdHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_native_ad, parent, false)) {

    private val indexTextView = itemView.indexLabel
    private val titleTextView = itemView.titleLabel
    private val subtitleTextView = itemView.subtitleLabel
    private val button = itemView.callToActionButton
    private val subParentLayout = itemView.subParentLayout
    private val adOptionsLayout = itemView.adOptionsLayout

    // Optional, only for items that are ads.
    val iconImageView: ImageView = itemView.iconImageView

    fun configureForContent(title: String, subtitle: String, position: Int) {
        titleTextView.text = title
        subtitleTextView.text = subtitle
        setIndex(position)

        subParentLayout.visibility = View.VISIBLE
        iconImageView.visibility = View.GONE
        titleTextView.gravity = Gravity.START
        adOptionsLayout.visibility = View.GONE
    }

    fun configureForAd(title: String, subtitle: String, callToAction: String, image: Bitmap?) {
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