package com.smartadserver.android.kotlinsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.smartadserver.android.library.model.SASAdElement
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.library.ui.SASInterstitialManager
import kotlinx.android.synthetic.main.activity_interstitial.*

/**
 * Simple activity featuring an interstitial ad.
 */
class InterstitialActivity : AppCompatActivity() {

    // The SASInterstitialManager instance
    private val interstitialManager by lazy {
        SASInterstitialManager(this, SASAdPlacement(104808, "663264", 12167, ""))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interstitial)

        // Setup the interstitial manager listener
        interstitialManager.interstitialListener =
            object : SASInterstitialManager.InterstitialListener {
                override fun onInterstitialAdLoaded(
                    interstitialManager: SASInterstitialManager?,
                    adElement: SASAdElement?
                ) {
                    Log.i("Sample", "Interstitial loading completed.")
                    showAdButton.post { showAdButton.isEnabled = true }
                }

                override fun onInterstitialAdFailedToLoad(
                    interstitialManager: SASInterstitialManager?,
                    e: Exception?
                ) {
                    Log.i("Sample", "Interstitial failed to load.")
                    showAdButton.post { showAdButton.isEnabled = false }
                }

                override fun onInterstitialAdShown(interstitialManager: SASInterstitialManager?) {
                    Log.i("Sample", "Interstitial was shown.")
                    showAdButton.post { showAdButton.isEnabled = false }
                }

                override fun onInterstitialAdFailedToShow(
                    interstitialManager: SASInterstitialManager?,
                    e: Exception?
                ) {
                    Log.i("Sample", "Interstitial failed to show: $e")
                    showAdButton.post { showAdButton.isEnabled = false }
                }

                override fun onInterstitialAdDismissed(interstitialManager: SASInterstitialManager?) {
                    Log.i("Sample", "Interstitial was dismissed.")
                }

                override fun onInterstitialAdClicked(interstitialManager: SASInterstitialManager?) {
                    Log.i("Sample", "Interstitial was clicked.")
                }

                override fun onInterstitialAdVideoEvent(
                    interstitialManager: SASInterstitialManager?,
                    event: Int
                ) {
                    Log.i("Sample", "Video event $event was triggered on Interstitial.")
                }
            }

        // Setup loadAd button click listener
        loadAdButton.setOnClickListener { interstitialManager.loadAd() }

        // Setup showAd button click listener
        showAdButton.setOnClickListener {
            if (interstitialManager.isShowable) {
                interstitialManager.show()
            } else {
                Log.e("Sample", "No interstitial ad currently loaded.")
            }
        }
    }

    override fun onDestroy() {
        interstitialManager.interstitialListener = null
        super.onDestroy()
    }
}