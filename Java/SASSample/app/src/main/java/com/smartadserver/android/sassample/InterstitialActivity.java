package com.smartadserver.android.sassample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.smartadserver.android.library.model.SASAdElement;
import com.smartadserver.android.library.model.SASAdPlacement;
import com.smartadserver.android.library.model.SASAdStatus;
import com.smartadserver.android.library.ui.SASInterstitialManager;
import com.smartadserver.android.library.util.SASConfiguration;

public class InterstitialActivity extends AppCompatActivity {

    private final static String TAG = "InterstitialActivity";

    /*****************************************
     * Ad Constants
     *****************************************/
    private final static SASAdPlacement INTERSTITIAL_PLACEMENT = new SASAdPlacement(104808,
            "663264",
            12167,
            "",
            true);

    /*****************************************
     * Members declarations
     *****************************************/
    // Interstitial manager
    SASInterstitialManager mInterstitialManager;

    // Handler classe to be notified of ad loading outcome
    SASInterstitialManager.InterstitialListener interstitialListener;

    // Button declared in main.xml
    Button mLoadInterstitialButton;
    Button mShowInterstitialButton;

    /**
     * performs Activity initialization after creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        /*****************************************
         * Now perform Ad related code here
         *****************************************/
        // Enable output to Android Logcat (optional)
        SASConfiguration.getSharedInstance().setLoggingEnabled(true);

        // Enable debugging in Webview if available (optional)
        WebView.setWebContentsDebuggingEnabled(true);

        // Initialize SASInterstitialManager instance
        initInterstitialManager();

        // Create buttons to manually load and show interstitial Video Ads
        createButtons();

        // Set show button state depending of the availability of an ad on the placement
        enableShowButton(mInterstitialManager.getAdStatus() == SASAdStatus.READY);

    }


    /**
     * Create Load and Show buttons
     */
    private void createButtons() {


//        // get
//        mLoadInterstitialButton = (Button) this.findViewById(R.id.loadAd);
//        mLoadInterstitialButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                SASAdStatus adStatus = mInterstitialManager.getAdStatus();
//                if (adStatus == SASAdStatus.READY) {
//                    mInterstitialManager.show();
//                } else if (adStatus == SASAdStatus.NOT_AVAILABLE || adStatus == SASAdStatus.EXPIRED) {
//                    // Load interstitial
//                    mInterstitialManager.loadAd();
//                }
//            }
//        });
//
        mLoadInterstitialButton = (Button) this.findViewById(R.id.loadAd);
        mLoadInterstitialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadInterstitialAd();
            }
        });

        mShowInterstitialButton = (Button) this.findViewById(R.id.showAd);
        mShowInterstitialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showInterstitialAd();
            }
        });
    }

    /**
     * Loads an interstitial Video Ad for the current placement
     */
    private void loadInterstitialAd() {
        // Loads a Rewarded Video Ad for the current placement
        mInterstitialManager.loadAd();
    }


    /**
     * Show a Rewarded Video Ad for the current placement
     */
    private void showInterstitialAd() {
        if (mInterstitialManager.getAdStatus() == SASAdStatus.READY) {
            // Show the loaded interstitial ad.
            mInterstitialManager.show();
        } else {
            Log.e(TAG, "No interstitial ad currently loaded");
        }
    }

    /**
     * Overriden to clean up the resources used by the {@link SASInterstitialManager}
     */
    @Override
    protected void onDestroy() {
        mInterstitialManager.onDestroy();
        super.onDestroy();
    }


    /**
     * initialize the SASInterstitialView instance of this Activity
     */
    private void initInterstitialManager() {

        // Create SASInterstitialManager instance
        mInterstitialManager = new SASInterstitialManager(this, INTERSTITIAL_PLACEMENT);

        interstitialListener = new SASInterstitialManager.InterstitialListener() {
            @Override
            public void onInterstitialAdLoaded(SASInterstitialManager interstitialManager, SASAdElement adElement) {
                Log.i("Sample", "Interstitial loading completed");
                enableShowButton(true);
            }

            @Override
            public void onInterstitialAdFailedToLoad(SASInterstitialManager interstitialManager, Exception e) {
                Log.i("Sample", "Interstitial loading failed (" + e.getMessage() + ")");
                enableShowButton(false);
            }

            @Override
            public void onInterstitialAdShown(SASInterstitialManager interstitialManager) {
                Log.i("Sample", "Interstitial was shown");
                enableShowButton(false);
            }

            @Override
            public void onInterstitialAdFailedToShow(SASInterstitialManager interstitialManager, Exception e) {
                Log.i("Sample", "Interstitial failed to show (" + e.getMessage() + ")");
                enableShowButton(false);
            }

            @Override
            public void onInterstitialAdClicked(SASInterstitialManager interstitialManager) {
                Log.i("Sample", "Interstitial was clicked");
            }

            @Override
            public void onInterstitialAdDismissed(SASInterstitialManager interstitialManager) {
                Log.i("Sample", "Interstitial was dismissed");
            }

            @Override
            public void onInterstitialAdVideoEvent(SASInterstitialManager interstitialManager, int videoEvent) {
                Log.i("Sample", "Video event " + videoEvent + " was triggered on Interstitial");
            }
        };

        mInterstitialManager.setInterstitialListener(interstitialListener);
    }


    /**
     * Enable / Disable show button.
     */
    private void enableShowButton(final boolean enabled) {
        // enforce execution in main thread, as this method might be called from a different thread
        mShowInterstitialButton.post(new Runnable() {
            @Override
            public void run() {
                if (enabled) {
                    mShowInterstitialButton.setVisibility(View.VISIBLE);
                } else {
                    mShowInterstitialButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}
