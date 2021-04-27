package com.smartadserver.android.kotlinsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.smartadserver.android.library.headerbidding.SASBiddingAdResponse
import com.smartadserver.android.library.headerbidding.SASBiddingFormatType
import com.smartadserver.android.library.headerbidding.SASBiddingManager
import com.smartadserver.android.library.model.SASAdElement
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.library.ui.SASInterstitialManager
import com.smartadserver.android.library.util.SASUtil
import kotlinx.android.synthetic.main.activity_inapp_bidding_interstitial.*

/**
 * Simple activity featuring an interstitial ad.
 */
class InAppBiddingInterstitialActivity : AppCompatActivity() {

    private var isBiddingManagerLoading = false

    private var biddingAdResponse: SASBiddingAdResponse? = null

    // Manager object that will handle all bidding ad calls.
    private val biddingManager by lazy {
        // Create needed SASAdPlacement
        val adPlacement = SASAdPlacement(104808, 1160279, 85867, "interstitial-inapp-bidding")

        // Create the bidding manager with appropriate Context, SASAdPlacement, Format Type, Currency and Listener
        SASBiddingManager(this, adPlacement, SASBiddingFormatType.INTERSTITIAL, "EUR", object : SASBiddingManager.SASBiddingManagerListener {
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
    
    private var interstitialManager: SASInterstitialManager? = null

    // Handler class to be notified of ad loading outcome
    private val interstitialListener by lazy {
        object : SASInterstitialManager.InterstitialListener {
            override fun onInterstitialAdLoaded(
                interstitialManager: SASInterstitialManager,
                adElement: SASAdElement
            ) {
                Log.i("Sample", "Interstitial loading completed.")

                // In our case we decide to directly show the interstitial
                interstitialManager.show()
                updateUiStatus()
            }

            override fun onInterstitialAdFailedToLoad(
                interstitialManager: SASInterstitialManager,
                e: Exception
            ) {
                Log.i("Sample", "Interstitial failed to load.")
                updateUiStatus()
            }

            override fun onInterstitialAdShown(interstitialManager: SASInterstitialManager) {
                Log.i("Sample", "Interstitial was shown.")
                updateUiStatus()
            }

            override fun onInterstitialAdFailedToShow(
                interstitialManager: SASInterstitialManager,
                e: Exception
            ) {
                Log.i("Sample", "Interstitial failed to show: $e")
                updateUiStatus()
            }

            override fun onInterstitialAdDismissed(interstitialManager: SASInterstitialManager) {
                Log.i("Sample", "Interstitial was dismissed.")
            }

            override fun onInterstitialAdClicked(interstitialManager: SASInterstitialManager) {
                Log.i("Sample", "Interstitial was clicked.")
            }

            override fun onInterstitialAdVideoEvent(
                interstitialManager: SASInterstitialManager,
                event: Int
            ) {
                Log.i("Sample", "Video event $event was triggered on Interstitial.")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inapp_bidding_interstitial)

        // Setup loadAd button click listener
        loadButton.setOnClickListener { loadBiddingAd() }

        // Setup showAd button click listener
        showButton.setOnClickListener { showBiddingAd() }
    }

    override fun onDestroy() {
        interstitialManager?.interstitialListener = null
        super.onDestroy()
    }

    private fun loadBiddingAd() {
        if (!isBiddingManagerLoading) {
            isBiddingManagerLoading = true

            biddingManager.load()
            updateUiStatus()
        }
    }

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

            // Reset current interstitialManager if any
            interstitialManager?.interstitialListener = null
            interstitialManager = null

            // Create the interstitial manager
            interstitialManager = SASInterstitialManager(this, it)

            interstitialManager?.let { manager ->
                // Set interstitial listener
                manager.interstitialListener = interstitialListener

                // Load the bidding interstitial ad
                manager.loadAd()
            }

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