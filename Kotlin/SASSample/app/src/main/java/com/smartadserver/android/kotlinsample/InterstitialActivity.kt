package com.smartadserver.android.kotlinsample

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.smartadserver.android.kotlinsample.databinding.ActivityInterstitialBinding
import com.smartadserver.android.library.model.SASAdElement
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.library.ui.SASInterstitialManager

/**
 * Simple activity featuring an interstitial ad.
 */
class InterstitialActivity : AppCompatActivity() {

    // If you are an inventory reseller, you must provide your Supply Chain Object information.
    // More info here: https://help.smartadserver.com/s/article/Sellers-json-and-SupplyChain-Object
    private val supplyChainObjectString: String? = null // "1.0,1!exchange1.com,1234,1,publisher,publisher.com";

    // The SASInterstitialManager instance
    private val interstitialManager by lazy {
        SASInterstitialManager(this, SASAdPlacement(104808, 663264, 12167, "", supplyChainObjectString))
    }

    // Binding object to retrieve UI elements
    private val binding : ActivityInterstitialBinding by lazy { ActivityInterstitialBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // set content view from binding
        setContentView(binding.root)

        // Setup the interstitial manager listener
        interstitialManager.interstitialListener =
            object : SASInterstitialManager.InterstitialListener {
                override fun onInterstitialAdLoaded(
                    interstitialManager: SASInterstitialManager,
                    adElement: SASAdElement
                ) {
                    Log.i("Sample", "Interstitial loading completed.")
                    binding.showAdButton.post { binding.showAdButton.isEnabled = true }
                }

                override fun onInterstitialAdFailedToLoad(
                    interstitialManager: SASInterstitialManager,
                    e: Exception
                ) {
                    Log.i("Sample", "Interstitial failed to load.")
                    binding.showAdButton.post { binding.showAdButton.isEnabled = false }
                }

                override fun onInterstitialAdShown(interstitialManager: SASInterstitialManager) {
                    Log.i("Sample", "Interstitial was shown.")
                    binding.showAdButton.post { binding.showAdButton.isEnabled = false }
                }

                override fun onInterstitialAdFailedToShow(
                    interstitialManager: SASInterstitialManager,
                    e: Exception
                ) {
                    Log.i("Sample", "Interstitial failed to show: $e")
                    binding.showAdButton.post { binding.showAdButton.isEnabled = false }
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

        // Setup loadAd button click listener
        binding.loadAdButton.setOnClickListener { interstitialManager.loadAd() }

        // Setup showAd button click listener
        binding.showAdButton.setOnClickListener {
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