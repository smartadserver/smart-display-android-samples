package com.smartadserver.android.kotlinsample

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.smartadserver.android.library.model.SASAdElement
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.library.model.SASReward
import com.smartadserver.android.library.rewarded.SASRewardedVideoManager
import kotlinx.android.synthetic.main.activity_interstitial.*

/**
 * Simple class featuring a rewarded video ad.
 */
class RewardedVideoActivity : AppCompatActivity() {

    private val rewardedVideoManager by lazy {
        SASRewardedVideoManager(this, SASAdPlacement(104808, "795153", 12167, "rewardedvideo"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewarded_video)

        // Setup rewarded video manager listener
        rewardedVideoManager.rewardedVideoListener =
            object : SASRewardedVideoManager.RewardedVideoListener {
                override fun onRewardedVideoAdLoaded(
                    rewardedVideoManager: SASRewardedVideoManager?,
                    adElement: SASAdElement?
                ) {
                    Log.i("Sample", "Rewarded video ad loading completed.")
                    showAdButton.post { showAdButton.isEnabled = true }
                }

                override fun onRewardedVideoAdFailedToLoad(
                    rewardedVideoManager: SASRewardedVideoManager?,
                    e: Exception?
                ) {
                    Log.i("Sample", "Rewarded video failed to load: $e.")
                    showAdButton.post { showAdButton.isEnabled = false }
                }

                override fun onRewardedVideoAdShown(rewardedVideoManager: SASRewardedVideoManager?) {
                    Log.i("Sample", "Rewarded video was shown.")
                    showAdButton.post { showAdButton.isEnabled = false }
                }

                override fun onRewardedVideoAdFailedToShow(
                    rewardedVideoManager: SASRewardedVideoManager?,
                    e: Exception?
                ) {
                    Log.i("Sample", "Rewarded video failed to show: $e.")
                    showAdButton.post { showAdButton.isEnabled = false }
                }

                override fun onRewardedVideoAdClicked(rewardedVideoManager: SASRewardedVideoManager?) {
                    Log.i("Sample", "Rewarded video was clicked.")
                }

                override fun onRewardedVideoAdClosed(rewardedVideoManager: SASRewardedVideoManager?) {
                    Log.i("Sample", "Rewarded video was closed.")
                }

                override fun onRewardReceived(
                    rewardedVideoManager: SASRewardedVideoManager?,
                    reward: SASReward?
                ) {
                    if (reward != null) {
                        Log.i(
                            "Sample",
                            "Rewarded collected a reward. User should be rewarded with: ${reward.amount} ${reward.currency}."
                        )
                    }
                }

                override fun onRewardedVideoEvent(
                    rewardedVideoManager: SASRewardedVideoManager?,
                    event: Int
                ) {
                    Log.i("Sample", "Video event $event was triggered on RewardedVideo.")
                }

                override fun onRewardedVideoEndCardDisplayed(
                    rewardedVideoManager: SASRewardedVideoManager?,
                    viewGroup: ViewGroup?
                ) {
                    Log.i("Sample", "Rewarde video HTML EndCard was displayed.")
                }
            }

        // Setup loadAd button click listener
        loadAdButton.setOnClickListener { rewardedVideoManager.loadRewardedVideo() }

        // Setup showAd button click listener
        showAdButton.setOnClickListener {
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