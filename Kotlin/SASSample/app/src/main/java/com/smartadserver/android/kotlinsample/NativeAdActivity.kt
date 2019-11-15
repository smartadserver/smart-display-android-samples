package com.smartadserver.android.kotlinsample

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.library.model.SASNativeAdElement
import com.smartadserver.android.library.model.SASNativeAdManager
import com.smartadserver.android.library.ui.SASNativeAdMediaView
import com.smartadserver.android.kotlinsample.holder.NativeAdHolder
import com.smartadserver.android.kotlinsample.holder.NativeAdWithMediaHolder
import kotlinx.android.synthetic.main.activity_native.*
import java.io.IOException
import java.io.InputStream
import java.net.URL
import kotlin.math.min

class NativeAdActivity : AppCompatActivity() {

    private lateinit var nativeAdManager: SASNativeAdManager
    private var currentNativeAd: SASNativeAdElement? = null

    private var nativeAdIconBitmap: Bitmap? = null
    private var nativeAdCoverBitmap: Bitmap? = null
    private val nativeAdMediaViews = HashSet<SASNativeAdMediaView>()

    private var adLoaded = false
    private val adPosition = 8

    // Native Ads placements
    private val nativeAdPlacement = SASAdPlacement(104808, "720265", 15140, "")
    private val nativeAdWithMediaPlacement = SASAdPlacement(104808, "692588", 15140, "")

    private inner class ListLayoutAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var nativeContentType = 0
        private var mediaType = 1

        override fun getItemViewType(position: Int): Int {
            // If adPosition and Ad has a media, we will use a different ViewHolder to take full advantage of this format
            return if (position == adPosition && adLoaded && currentNativeAd?.mediaElement != null) {
                mediaType
            } else {
                // Else display our classic content view
                nativeContentType
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == mediaType) {
                NativeAdWithMediaHolder(inflater, parent)
            } else {
                NativeAdHolder(inflater, parent)
            }
        }

        override fun getItemCount(): Int = 40

        override fun onBindViewHolder(parent: RecyclerView.ViewHolder, position: Int) {
            // It is the ad position, configure the viewHolder for the ad
            if (position == adPosition && currentNativeAd != null && adLoaded) {

                currentNativeAd?.let {
                    // Check if native ad has a media and process accordingly
                    // No media = NativeAdholder
                    // Media = NativeAdWithMediaHolder
                    if (it.mediaElement == null) {
                        configureViewHolderForNativeAd(parent, it, nativeAdIconBitmap)
                    } else {
                        configureViewHolderForNativeAdWithMedia(
                            parent,
                            it,
                            nativeAdIconBitmap,
                            nativeAdCoverBitmap
                        )
                    }
                }

            } else {
                // Else configure the viewHolder for content
                val holder = parent as NativeAdHolder

                val title =
                    if (position == 0) "Simple NativeAd integration" else "Lorem ipsum dolor sit amet"
                val subtitle =
                    if (position == 0) "See implementation in NativeActivity. Please scroll down to see the ad." else "Phasellus in tellus eget arcu volutpat bibendum vulputate ac sapien. Vivamus enim elit, gravida vel consequat sit amet, scelerisque vitae ex."

                holder.configureForContent(title, subtitle, position)
            }
        }

        private fun configureViewHolderForNativeAd(
            listViewHolder: RecyclerView.ViewHolder,
            nativeAd: SASNativeAdElement,
            iconImage: Bitmap?
        ) {
            if (listViewHolder is NativeAdHolder) {

                var scaledIconImage: Bitmap? = null

                // Has iconImage been loaded?
                iconImage?.let {
                    scaledIconImage = Bitmap.createScaledBitmap(
                        iconImage,
                        listViewHolder.iconImageView.layoutParams.width,
                        listViewHolder.iconImageView.layoutParams.height,
                        false
                    )
                }

                // Configure view for Ad
                listViewHolder.configureForAd(
                    nativeAd.title,
                    nativeAd.subtitle,
                    nativeAd.calltoAction,
                    scaledIconImage
                )

                // Register View for Native Ad
                nativeAd.registerView(listViewHolder.itemView)
            }
        }

        @Suppress("UsePropertyAccessSyntax")
        private fun configureViewHolderForNativeAdWithMedia(
            listViewHolder: RecyclerView.ViewHolder,
            nativeAd: SASNativeAdElement,
            iconImage: Bitmap?,
            coverImage: Bitmap?
        ) {
            if (listViewHolder is NativeAdWithMediaHolder) {

                // Configure View for Ad
                listViewHolder.configure(
                    nativeAd.title,
                    nativeAd.subtitle,
                    nativeAd.calltoAction,
                    iconImage,
                    coverImage
                )

                // Register View for Native Ad
                nativeAd.registerView(listViewHolder.itemView)

                // Register NativeAdElement on SASNativeAdMediaView
                // This will allow the media to be displayed / tracked properly
                listViewHolder.mediaView.setNativeAdElement(nativeAd)

                // Register NativeAdElement on SASAdChoiceView
                // This will allow the ad choice option to be opened properly.
                // This is required for Facebook Mediation
                listViewHolder.adChoicesView.setNativeAdElement(nativeAd)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native)

        // Do we want an ad with media?
        val adPlacement = if (intent.getBooleanExtra("withMedia", false)) {
            nativeAdWithMediaPlacement
        } else {
            nativeAdPlacement
        }

        // Setup recyclerView
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            adapter = ListLayoutAdapter()
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        // Create native ad manager
        nativeAdManager = SASNativeAdManager(this, adPlacement)

        // Set native ad manager listener
        nativeAdManager.nativeAdListener = object : SASNativeAdManager.NativeAdListener {
            override fun onNativeAdLoaded(nativeAdElement: SASNativeAdElement?) {
                currentNativeAd = nativeAdElement

                currentNativeAd?.let {
                    adLoaded = true

                    // Download icon for native ad
                    if (it.icon != null && it.icon.url != null && it.icon.url.isNotEmpty()) {
                        nativeAdIconBitmap =
                            scaledBitmapFromUrl(it.icon.url, it.icon.width, it.icon.height)
                    }

                    // download cover for native ad
                    if (it.coverImage != null && it.coverImage.url != null && it.coverImage.url.isNotEmpty()) {
                        nativeAdCoverBitmap = scaledBitmapFromUrl(
                            it.coverImage.url,
                            it.coverImage.width,
                            it.icon.height
                        )
                    }

                    recyclerView.post {
                        recyclerView.adapter?.notifyItemChanged(adPosition)
                    }
                }
            }

            override fun onNativeAdFailedToLoad(e: Exception?) {
                Log.i("NativeActivity", "Native Ad Loading Failed.")
            }

        }

        nativeAdManager.loadNativeAd()
    }

    /**
     * Clean up ad instances. This must be done to avoid IntentReceiver leak.
     */
    override fun onDestroy() {
        nativeAdManager.onDestroy()
        for (mediaView in nativeAdMediaViews) {
            mediaView.onDestroy()
        }
        super.onDestroy()
    }

    /*
     * Download an image from an URL and scales it to targetWidth * targetHeight
     */
    private fun scaledBitmapFromUrl(url: String, targetWidth: Int, targetHeight: Int): Bitmap? {
        var result: Bitmap? = null
        try {
            val inputStream = URL(url).content as InputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val bWidth = bitmap.width
            val bHeight = bitmap.height
            val resizeRatio =
                min(targetWidth / bWidth.toDouble(), targetHeight / bHeight.toDouble())
            result = Bitmap.createScaledBitmap(
                bitmap,
                (bWidth * resizeRatio).toInt(),
                (bHeight * resizeRatio).toInt(),
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return result
    }
}