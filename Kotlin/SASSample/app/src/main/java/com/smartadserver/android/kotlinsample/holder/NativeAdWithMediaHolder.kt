package com.smartadserver.android.kotlinsample.holder

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartadserver.android.library.ui.SASAdChoicesView
import com.smartadserver.android.library.ui.SASNativeAdMediaView
import com.smartadserver.android.kotlinsample.R
import kotlinx.android.synthetic.main.list_native_ad_media.view.*

class NativeAdWithMediaHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_native_ad_media, parent, false)) {

    private val titleTextView = itemView.titleLabel
    private val subtitleTextView = itemView.subtitleLabel
    private val button = itemView.callToActionButton
    private val iconImageView = itemView.iconImageView
    private val coverImageView = itemView.coverImageView
    val mediaView: SASNativeAdMediaView = itemView.mediaView
    val adChoicesView: SASAdChoicesView = itemView.adChoicesView

    fun configure(
        title: String,
        subtitle: String,
        calltoAction: String,
        iconImage: Bitmap?,
        coverImage: Bitmap?
    ) {
        titleTextView.text = title
        subtitleTextView.text = subtitle
        button.text = calltoAction

        if (iconImage != null) {
            iconImageView.setImageBitmap(iconImage)
        }

        if (coverImage != null) {
            coverImageView.setImageBitmap(coverImage)
        }
    }
}