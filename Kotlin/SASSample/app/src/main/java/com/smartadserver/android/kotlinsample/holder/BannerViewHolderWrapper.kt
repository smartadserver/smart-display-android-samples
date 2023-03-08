package com.smartadserver.android.kotlinsample.holder

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.smartadserver.android.library.model.SASAdElement
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.library.ui.SASBannerView
import kotlin.math.max
import kotlin.math.min

class BannerViewHolderWrapper(context: Context) {

    /**
     * Constants declaration
     */
    private val maxBannerHeight = 1800
    private val defaultBannerHeight = 0

    private var isAdLoaded = false
    val bannerView = SASBannerView(context)

    var bannerViewHolder: BannerViewHolder? = null
        set(holder) {

            // Fast exit if the current holder is the same that the given one.
            if (field == holder) {
                return
            }

            field = holder

            // Remove banner from its parent if possible
            val bannerParent: ViewGroup? = bannerView.parent as ViewGroup?

            // Dismiss sticky mode if activated (mandatory before removing the banner to avoid messing up the banner)
            bannerView.dismissStickyMode(false)
            bannerParent?.removeView(bannerView)

            // If holder is not null, add banner as a child
            // Then update banner size with a post (so it is executed when all layout has been update)
            bannerViewHolder?.let {
                val container: ViewGroup = it.binding.bannerContainer
                container.removeAllViews()
                container.addView(bannerView)

                bannerView.post { updateBannerSize(defaultBannerHeight) }
            }
        }

    init {
        // Banner initialization
        bannerView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        bannerView.bannerListener = object : SASBannerView.BannerListener {
            override fun onBannerAdLoaded(banner: SASBannerView, adElement: SASAdElement) {
                Log.i("Sample", "Banner loading completed")
                bannerView.executeOnUIThread {
                    val defaultHeight = (50 * bannerView.resources.displayMetrics.density).toInt()
                    updateBannerSize(defaultHeight)
                }
                isAdLoaded = true
            }

            override fun onBannerAdFailedToLoad(banner: SASBannerView, e: Exception) {
                Log.i("Sample", "Banner loading failed: $e")
                bannerView.executeOnUIThread { resizeBannerCell(0) }
            }

            override fun onBannerAdClicked(banner: SASBannerView) {
                Log.i("Sample", "Banner was clicked")
            }

            override fun onBannerAdExpanded(banner: SASBannerView) {
                Log.i("Sample", "Banner was expanded")
            }

            override fun onBannerAdCollapsed(banner: SASBannerView) {
                Log.i("Sample", "Banner was collapsed")
            }

            override fun onBannerAdClosed(banner: SASBannerView) {
                Log.i("Sample", "Banner was closed")
            }

            override fun onBannerAdResized(banner: SASBannerView) {
                Log.i("Sample", "Banner was resized")
            }

            override fun onBannerAdVideoEvent(banner: SASBannerView, event: Int) {
                Log.i("Sample", "Banner video event: $event")
            }
        }
    }

    fun isAvailable() = bannerViewHolder == null

    /**
     * Resize banner cell.
     * Will update the heights of the current holder and the banner.
     *
     * @param height The target height.
     */
    private fun resizeBannerCell(height: Int) {
        // Update itemView's layout params so it can display the banner
        bannerViewHolder?.run {
            itemView.layoutParams = itemView.layoutParams.apply { this.height = ViewGroup.LayoutParams.WRAP_CONTENT }

            val placeHolder: TextView = binding.placeHolder
            placeHolder.visibility = View.GONE

            // Update banner layout to fit the proper height
            bannerView.layoutParams = bannerView.layoutParams.apply { this.height = height }
        }
    }

    internal fun updateBannerSize(defaultHeight: Int) {
        // Execute only if holder exists, ad is loaded and not expanded
        bannerViewHolder?.let {
            if (isAdLoaded && !bannerView.isExpanded) {

                // Use ratio convenient property to compute the banner height to best fit the creative aspect ratio
                var height = defaultHeight
                val ratio: Double = bannerView.ratio
                if (ratio > 0) {
                    // retrieve screen width, as the banner stretches across the whole width
                    val width = bannerView.context.resources.displayMetrics.widthPixels
                    height = (width / ratio).toInt()
                }

                // Resize the table view cell if an height value is available
                height = max(defaultBannerHeight, min(maxBannerHeight, height))
                resizeBannerCell(height)
            }
        }
    }

    /**
     * Loads an ad only if no other ad is already loaded.
     *
     * @param adPlacement The ad placement to use.
     */
    fun loadAd(adPlacement: SASAdPlacement) {
        // Checking for ad loaded is not mandatory here, it just prevents us from reloading the ad.
        if (!isAdLoaded) {
            bannerView.loadAd(adPlacement)
        }
    }
}