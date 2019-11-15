package com.smartadserver.android.kotlinsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.smartadserver.android.library.headerbidding.SASBiddingAdResponse
import com.smartadserver.android.library.headerbidding.SASBiddingFormatType
import com.smartadserver.android.library.headerbidding.SASBiddingManager
import com.smartadserver.android.library.model.SASAdElement
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.library.ui.SASBannerView
import com.smartadserver.android.library.ui.SASRotatingImageLoader
import com.smartadserver.android.library.util.SASUtil
import kotlinx.android.synthetic.main.activity_inapp_bidding_banner.*


/**
 * Simple activity featuring an In-App Bidding banner ad.
 */
class InAppBiddingBannerActivity : AppCompatActivity() {

    // Manager object that will handle all bidding ad calls.
    private val biddingManager by lazy {
        // Create needed SASAdPlacement
        val adPlacement = SASAdPlacement(104808, "1160279", 85867, "banner-inapp-bidding")

        // Create the bidding manager with appropriate Context, SASAdPlacement, Format Type, Currency and Listener
        SASBiddingManager(this, adPlacement, SASBiddingFormatType.BANNER, "EUR", object : SASBiddingManager.SASBiddingManagerListener {
            override fun onBiddingManagerAdLoaded(adResponse: SASBiddingAdResponse) {
                isBiddingManagerLoading = false
                biddingAdResponse = adResponse

                // A bidding ad response has been received.
                // You can now load it into an ad view or discard it. See showBiddingAd() for more info.

                Log.i("Sample", "A bidding ad response has been loaded: $adResponse")
                updateUiStatus()
            }

            override fun onBiddingManagerAdFailedToLoad(e: java.lang.Exception) {
                isBiddingManagerLoading = false
                biddingAdResponse = null

                Log.i("Sample", "Fail to load a bidding ad response: ${e.message}")
                updateUiStatus()
            }
        })
    }

    private var isBiddingManagerLoading = false

    private var biddingAdResponse: SASBiddingAdResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inapp_bidding_banner)

        // Add a loader view on the banner. This view covers the banner placement, to indicate profress, whenever the banner is loading an ad.
        // This is optional.
        bannerView.loaderView = SASRotatingImageLoader(this).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorLoaderBackground))
        }

        // Set the banner listener
        bannerView.bannerListener = object : SASBannerView.BannerListener {
            override fun onBannerAdLoaded(banner: SASBannerView?, adElement: SASAdElement?) {
                Log.i("Sample", "Banner loading completed")
            }

            override fun onBannerAdFailedToLoad(banner: SASBannerView?, e: Exception?) {
                Log.i("Sample", "Banner loading failed: $e")
            }

            override fun onBannerAdClicked(banner: SASBannerView?) {
                Log.i("Sample", "Banner was clicked")
            }

            override fun onBannerAdExpanded(banner: SASBannerView?) {
                Log.i("Sample", "Banner was expanded")
            }

            override fun onBannerAdCollapsed(banner: SASBannerView?) {
                Log.i("Sample", "Banner was collapsed")
            }

            override fun onBannerAdClosed(banner: SASBannerView?) {
                Log.i("Sample", "Banner was closed")
            }

            override fun onBannerAdResized(banner: SASBannerView?) {
                Log.i("Sample", "Banner was resized")
            }

            override fun onBannerAdVideoEvent(banner: SASBannerView?, event: Int) {
                Log.i("Sample", "Video event $event was triggered on banner")
            }
        }

        // Set the buttons click listener
        loadButton.setOnClickListener { loadBiddingAd() }
        showButton.setOnClickListener { showBiddingAd() }

        updateUiStatus()
    }

    private fun loadBiddingAd() {
        if (!isBiddingManagerLoading) {
            isBiddingManagerLoading = true

            biddingManager.load()
            updateUiStatus()
        }
    }

    /***
     * Shows the previously loaded bidding ad, if any.
     */
    private fun showBiddingAd() {
        biddingAdResponse?.let {
            // We use our banner to display the bidding response retrieved earlier.
            //
            // Note: in an actual application, you would load Smart bidding ad response only if it
            // is better than responses you got from other third party SDKs.
            //
            // You can check the CPM associated with the bidding ad response by checking:
            // - biddingAdResponse.biddingAdPrice.cpm
            // - biddingAdResponse.biddingAdPrice.currency
            //
            // If you decide not to use the bidding ad response, you can simply discard it.
            bannerView.loadAd(it)
            showButton.post {
                showButton.isEnabled = false
            }
        }
    }

    // A bidding ad response is valid only if it has not been consumed already.
    private fun hasValidBiddingAdResponse() = biddingAdResponse?.isConsumed == false

    private fun updateUiStatus() {
        SASUtil.getMainLooperHandler().post {
            // Buttons
            loadButton.isEnabled = !isBiddingManagerLoading
            showButton.isEnabled = hasValidBiddingAdResponse()

            // Status textview
            statusTextView.text = when {
                isBiddingManagerLoading -> "loading a bidding ad…"
                biddingAdResponse != null -> biddingAdResponse?.let {
                    "bidding response: ${it.biddingAdPrice.cpm} ${it.biddingAdPrice.currency}"
                }
                else -> "(no bidding ad response loaded)"
            }
        }
    }
}