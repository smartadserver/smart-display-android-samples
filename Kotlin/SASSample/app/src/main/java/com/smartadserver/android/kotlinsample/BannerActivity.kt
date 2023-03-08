package com.smartadserver.android.kotlinsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.smartadserver.android.kotlinsample.databinding.ActivityBannerBinding
import com.smartadserver.android.library.model.SASAdElement
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.library.ui.SASBannerView
import com.smartadserver.android.library.ui.SASRotatingImageLoader

/**
 * Simple activity featuring a banner ad.
 */
class BannerActivity : AppCompatActivity() {

    // If you are an inventory reseller, you must provide your Supply Chain Object information.
    // More info here: https://help.smartadserver.com/s/article/Sellers-json-and-SupplyChain-Object
    private val supplyChainObjectString: String? = null // "1.0,1!exchange1.com,1234,1,publisher,publisher.com";

    private val bannerPlacement = SASAdPlacement(104808, 663262, 15140, "", supplyChainObjectString)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding object to retrieve UI elements
        val binding = ActivityBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add a loader view on the banner. This view covers the banner placement, to indicate profress, whenever the banner is loading an ad.
        // This is optional.
        binding.bannerView.loaderView = SASRotatingImageLoader(this).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorLoaderBackground))
        }

        // Set the banner listener
        binding.bannerView.bannerListener = object : SASBannerView.BannerListener {
            override fun onBannerAdLoaded(banner: SASBannerView, adElement: SASAdElement) {
                Log.i("Sample", "Banner loading completed")
            }

            override fun onBannerAdFailedToLoad(banner: SASBannerView, e: Exception) {
                Log.i("Sample", "Banner loading failed: $e")
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
                Log.i("Sample", "Video event $event was triggered on banner")
            }
        }


        // Set the button click listener
        binding.reloadButton.setOnClickListener { binding.bannerView.loadAd(bannerPlacement) }

        // Load the ad
        binding.bannerView.loadAd(bannerPlacement)
    }
}