package com.smartadserver.android.sassample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.smartadserver.android.library.model.SASAdElement;
import com.smartadserver.android.library.model.SASAdPlacement;
import com.smartadserver.android.library.ui.SASBannerView;
import com.smartadserver.android.library.ui.SASRotatingImageLoader;
import com.smartadserver.android.library.util.SASConfiguration;

/**
 * Simple activity featuring a banner ad.
 */

public class BannerActivity extends AppCompatActivity {

    /*****************************************
     * Ad Constants
     *****************************************/
    private final static SASAdPlacement BANNER_PLACEMENT = new SASAdPlacement(104808,
            "663262",
            15140,
            "",
            true);


    /*****************************************
     * Members declarations
     *****************************************/
    // Banner view (as declared in the main.xml layout file, in res/layout)
    SASBannerView mBannerView;

    // Handler class to be notified of ad loading outcome
    SASBannerView.BannerListener bannerListener;

    // Button declared in main.xml
    Button mRefreshBannerButton;


    /**
     * performs Activity initialization after creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        //Set Title
        setTitle(R.string.title_activity_banner);

        /*****************************************
         * now perform Ad related code here
         *****************************************/

        // Enable output to Android Logcat (optional)
        SASConfiguration.getSharedInstance().setLoggingEnabled(true);

        // Enable debugging in Webview if available (optional)
        WebView.setWebContentsDebuggingEnabled(true);

        // Initialize SASBannerView
        initBannerView();

        // Create button to manually refresh the ad
        mRefreshBannerButton = this.findViewById(R.id.reloadButton);
        mRefreshBannerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadBannerAd();
            }
        });

        // Load Banner ad
        loadBannerAd();

    }

    /**
     * Overriden to clean up SASAdView instances. This must be done to avoid IntentReceiver leak.
     */
    @Override
    protected void onDestroy() {
        mBannerView.onDestroy();
        super.onDestroy();
    }

    /**
     * Initialize the SASBannerView instance of this Activity
     */
    private void initBannerView() {
        // Fetch the SASBannerView inflated from the main.xml layout file
        mBannerView = this.findViewById(R.id.banner);

        // Add a loader view on the banner. This view covers the banner placement, to indicate progress, whenever the banner is loading an ad.
        // This is optional
        View loader = new SASRotatingImageLoader(this);
        loader.setBackgroundColor(getResources().getColor(R.color.colorLoaderBackground));
        mBannerView.setLoaderView(loader);

        bannerListener = new SASBannerView.BannerListener() {
            @Override
            public void onBannerAdLoaded(SASBannerView bannerView, SASAdElement adElement) {
                Log.i("Sample", "Banner loading completed");
            }

            @Override
            public void onBannerAdFailedToLoad(SASBannerView bannerView, Exception e) {
                Log.i("Sample", "Banner loading failed: " + e.getMessage());
            }

            @Override
            public void onBannerAdClicked(SASBannerView bannerView) {
                Log.i("Sample", "Banner was clicked");
            }

            @Override
            public void onBannerAdExpanded(SASBannerView bannerView) {
                Log.i("Sample", "Banner was expanded");
            }

            @Override
            public void onBannerAdCollapsed(SASBannerView bannerView) {
                Log.i("Sample", "Banner was collapsed");
            }

            @Override
            public void onBannerAdResized(SASBannerView bannerView) {
                Log.i("Sample", "Banner was resized");
            }

            @Override
            public void onBannerAdClosed(SASBannerView bannerView) {
                Log.i("Sample", "Banner was closed");
            }

            @Override
            public void onBannerAdVideoEvent(SASBannerView bannerView, int videoEvent) {
                Log.i("Sample", "Video event " + videoEvent + " was triggered on Banner");
            }
        };

        mBannerView.setBannerListener(bannerListener);
    }

    /**
     * Loads an ad on the banner
     */
    private void loadBannerAd() {
        // Load banner ad with appropriate parameters (siteID,pageID,formatID,master,targeting,adResponseHandler)
        mBannerView.loadAd(BANNER_PLACEMENT);
    }

}
