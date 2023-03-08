package com.smartadserver.android.kotlinsample

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.smartadserver.android.kotlinsample.databinding.ActivityRewardedVideoBinding
import com.smartadserver.android.library.model.SASAdElement
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.library.model.SASReward
import com.smartadserver.android.library.rewarded.SASRewardedVideoManager

/**
 * Simple class featuring a rewarded video ad.
 */
class RewardedVideoActivity : AppCompatActivity() {

    // If you are an inventory reseller, you must provide your Supply Chain Object information.
    // More info here: https://help.smartadserver.com/s/article/Sellers-json-and-SupplyChain-Object
    private val supplyChainObjectString: String? = null // "1.0,1!exchange1.com,1234,1,publisher,publisher.com";

    private val rewardedVideoManager by lazy {
        SASRewardedVideoManager(this, SASAdPlacement(104808, 795153, 12167, "rewardedvideo", supplyChainObjectString))
    }

    // Binding object to retrieve UI elements
    private val binding : ActivityRewardedVideoBinding by lazy {ActivityRewardedVideoBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Setup rewarded video manager listener
        rewardedVideoManager.rewardedVideoListener =
            object : SASRewardedVideoManager.RewardedVideoListener {
                override fun onRewardedVideoAdLoaded(
                    rewardedVideoManager: SASRewardedVideoManager,
                    adElement: SASAdElement
                ) {
                    Log.i("Sample", "Rewarded video ad loading completed.")
                    binding.showAdButton.post { binding.showAdButton.isEnabled = true }
                }

                override fun onRewardedVideoAdFailedToLoad(
                    rewardedVideoManager: SASRewardedVideoManager,
                    e: Exception
                ) {
                    Log.i("Sample", "Rewarded video failed to load: $e.")
                    binding.showAdButton.post { binding.showAdButton.isEnabled = false }
                }

                override fun onRewardedVideoAdShown(rewardedVideoManager: SASRewardedVideoManager) {
                    Log.i("Sample", "Rewarded video was shown.")
                    binding.showAdButton.post { binding.showAdButton.isEnabled = false }
                }

                override fun onRewardedVideoAdFailedToShow(
                    rewardedVideoManager: SASRewardedVideoManager,
                    e: Exception
                ) {
                    Log.i("Sample", "Rewarded video failed to show: $e.")
                    binding.showAdButton.post { binding.showAdButton.isEnabled = false }
                }

                override fun onRewardedVideoAdClicked(rewardedVideoManager: SASRewardedVideoManager) {
                    Log.i("Sample", "Rewarded video was clicked.")
                }

                override fun onRewardedVideoAdClosed(rewardedVideoManager: SASRewardedVideoManager) {
                    Log.i("Sample", "Rewarded video was closed.")
                }

                override fun onRewardReceived(
                    rewardedVideoManager: SASRewardedVideoManager,
                    reward: SASReward
                ) {
                    Log.i(
                        "Sample",
                        "Rewarded collected a reward. User should be rewarded with: ${reward.amount} ${reward.currency}."
                    )
                }

                override fun onRewardedVideoEvent(
                    rewardedVideoManager: SASRewardedVideoManager,
                    event: Int
                ) {
                    Log.i("Sample", "Video event $event was triggered on RewardedVideo.")
                }

                override fun onRewardedVideoEndCardDisplayed(
                    rewardedVideoManager: SASRewardedVideoManager,
                    viewGroup: ViewGroup
                ) {
                    Log.i("Sample", "Rewarde video HTML EndCard was displayed.")
                }
            }

        // Setup loadAd button click listener
        binding.loadAdButton.setOnClickListener { rewardedVideoManager.loadRewardedVideo() }

        // Setup showAd button click listener
        binding.showAdButton.setOnClickListener {
            if (rewardedVideoManager.hasRewardedVideo()) {
                rewardedVideoManager.showRewardedVideo()
            } else {
                Log.e("Sample", "No rewarded video ad currently loaded.")
            }
        }
    }

    override fun onDestroy() {
        rewardedVideoManager.rewardedVideoListener = null
        super.onDestroy()
    }
}