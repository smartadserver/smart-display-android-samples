package com.smartadserver.android.kotlinsample.holder

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartadserver.android.library.ui.SASAdChoicesView
import com.smartadserver.android.library.ui.SASNativeAdMediaView
import com.smartadserver.android.kotlinsample.R
import com.smartadserver.android.kotlinsample.databinding.ListNativeAdBinding
import com.smartadserver.android.kotlinsample.databinding.ListNativeAdMediaBinding

class NativeAdWithMediaHolder(binding: ListNativeAdMediaBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val titleTextView = binding.titleLabel
    private val subtitleTextView = binding.subtitleLabel
    private val button = binding.callToActionButton
    private val iconImageView = binding.iconImageView
    private val coverImageView = binding.coverImageView
    val mediaView: SASNativeAdMediaView = binding.mediaView
    val adChoicesView: SASAdChoicesView = binding.adChoicesView

    fun configure(
        title: String?,
        subtitle: String?,
        calltoAction: String?,
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