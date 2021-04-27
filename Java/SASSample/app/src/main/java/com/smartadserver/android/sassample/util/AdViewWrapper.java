package com.smartadserver.android.sassample.util;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smartadserver.android.library.model.SASAdElement;
import com.smartadserver.android.library.model.SASAdPlacement;
import com.smartadserver.android.library.ui.SASAdView;
import com.smartadserver.android.library.ui.SASBannerView;
import com.smartadserver.android.sassample.R;


/**
 * Created by Thomas Geley on 03/06/2016.
 */

public class AdViewWrapper {

    /* --------------------------- */
    /* Const declaration
    /* --------------------------- */

    private final static int MAX_BANNER_HEIGHT = 1800;
    private final static int DEFAULT_BANNER_HEIGHT = 0;

    private final static String TAG = AdViewWrapper.class.getSimpleName();


    /* --------------------------- */
    /* Members declaration
    /* --------------------------- */
    public SASBannerView mBanner;
    public BannerViewHolder mHolder;
    private boolean mIsAdLoaded;


    public AdViewWrapper (@NonNull Context context) {
        mBanner = new SASBannerView(context); //Instantiate banner from layout.
        mBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));

        SASBannerView.BannerListener bannerListener = new SASBannerView.BannerListener() {
            @Override
            public void onBannerAdLoaded(@NonNull SASBannerView bannerView, @NonNull SASAdElement adElement) {
                Log.i("Sample", "Banner loading completed");
                Runnable resizeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        int defaultHeight = (int) (50 * bannerView.getResources().getDisplayMetrics().density);
                        updateBannerSize(defaultHeight);
                    }
                };

                mBanner.executeOnUIThread(resizeRunnable);

                //Comment this line if you want to be able to reload banner (every 2 minutes for example)
                mIsAdLoaded = true;
            }

            @Override
            public void onBannerAdFailedToLoad(@NonNull SASBannerView bannerView, @NonNull Exception e) {
                Log.i("Sample", "Banner loading failed: " + e.getMessage());
                mBanner.executeOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        resizeBannerCell(0);
                    }
                });
            }

            @Override
            public void onBannerAdClicked(@NonNull SASBannerView bannerView) {
                Log.i("Sample", "Banner was clicked");
            }

            @Override
            public void onBannerAdExpanded(@NonNull SASBannerView bannerView) {
                Log.i("Sample", "Banner was expanded");
            }

            @Override
            public void onBannerAdCollapsed(@NonNull SASBannerView bannerView) {
                Log.i("Sample", "Banner was collapsed");
            }

            @Override
            public void onBannerAdResized(@NonNull SASBannerView bannerView) {
                Log.i("Sample", "Banner was resized");
            }

            @Override
            public void onBannerAdClosed(@NonNull SASBannerView bannerView) {
                Log.i("Sample", "Banner was closed");
            }

            @Override
            public void onBannerAdVideoEvent(@NonNull SASBannerView bannerView, int videoEvent) {
                switch (videoEvent) {
                    case SASAdView.VideoEvents.VIDEO_START:
                        Log.i(TAG, "Video event : VIDEO_START");
                        break;
                    case SASAdView.VideoEvents.VIDEO_PAUSE:
                        Log.i(TAG, "Video event : VIDEO_PAUSE");
                        break;
                    case SASAdView.VideoEvents.VIDEO_RESUME:
                        Log.i(TAG, "Video event : VIDEO_RESUME");
                        break;
                    case SASAdView.VideoEvents.VIDEO_REWIND:
                        Log.i(TAG, "Video event : VIDEO_REWIND");
                        break;
                    case SASAdView.VideoEvents.VIDEO_FIRST_QUARTILE:
                        Log.i(TAG, "Video event : VIDEO_FIRST_QUARTILE");
                        break;
                    case SASAdView.VideoEvents.VIDEO_MIDPOINT:
                        Log.i(TAG, "Video event : VIDEO_MIDPOINT");
                        break;
                    case SASAdView.VideoEvents.VIDEO_THIRD_QUARTILE:
                        Log.i(TAG, "Video event : VIDEO_THIRD_QUARTILE");
                        break;
                    case SASAdView.VideoEvents.VIDEO_COMPLETE:
                        Log.i(TAG, "Video event : VIDEO_COMPLETE");
                        break;
                    case SASAdView.VideoEvents.VIDEO_SKIP:
                        Log.i(TAG, "Video event : VIDEO_SKIP");
                        break;
                    case SASAdView.VideoEvents.VIDEO_ENTER_FULLSCREEN:
                        Log.i(TAG, "Video event : VIDEO_ENTER_FULLSCREEN");
                        break;
                    case SASAdView.VideoEvents.VIDEO_EXIT_FULLSCREEN:
                        Log.i(TAG, "Video event : VIDEO_EXIT_FULLSCREEN");
                        break;
                }
            }
        };

        mBanner.setBannerListener(bannerListener);
    }


    public boolean isAvailable() {
        return mHolder == null;
    }

    /**
     * Set and unset ViewHolder
     * The banner will be added (remove) to the ViewHolder.itemView and displayed on screen
     */
    public void setHolder(BannerViewHolder aHolder) {

        if (mHolder == aHolder) {
            return;
        }

        //Set new holder. Note that holder passed can be null (when Recyclerview.ViewHolder is recycled for example)
        mHolder = aHolder;

        //Remove banner from it's parent if possible
        ViewGroup parent = ((ViewGroup) mBanner.getParent());
        // dismiss sticky mode if activated (mandatory before removing the banner or it will be messed up...)
        mBanner.dismissStickyMode(false);
        if (parent != null) { parent.removeView(mBanner); }

        //If holder is not null, add banner as a child
        //Then update banner size with a post (so it is executed when all layout has been updated)
        if (mHolder != null) {
            ViewGroup vg = (ViewGroup)mHolder.itemView.findViewById(R.id.banner_container);
            vg.removeAllViews();
            vg.addView(mBanner);
            mBanner.post(new Runnable() {
                @Override
                public void run() {
                    updateBannerSize(DEFAULT_BANNER_HEIGHT);
                }
            });
        }
    }

    /**
     * Resize banner cell.
     * Will update the height of the holder and of the banner.
     * @param height the target height
     */
    private void resizeBannerCell(final int height) {

        if (mHolder != null) {
            //Update holder itemview layout so it can display the banner
            final ViewGroup.LayoutParams holderLayout = mHolder.itemView.getLayoutParams();
            holderLayout.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mHolder.itemView.setLayoutParams(holderLayout);
            View tv = mHolder.itemView.findViewById(R.id.placeholder);
            tv.setVisibility(View.GONE);

            //Update banner layout to fit the proper height
            final ViewGroup.LayoutParams bannerLayout = mBanner.getLayoutParams();
            bannerLayout.height = height;
            mBanner.setLayoutParams(bannerLayout);
        }

    }


    public void updateBannerSize(final int defaultHeight) {
        //Only execute if banner exist, holder exists, ad is loaded and not expanded.
        if (mBanner != null && mHolder != null && mIsAdLoaded && !mBanner.isExpanded()) {

            mBanner.executeOnUIThread(new Runnable() {
                @Override
                public void run() {
                    // Use getRatio() convenient method to compute the banner height to best fit the creative aspect ratio
                    int height = defaultHeight;
                    double ratio = mBanner.getRatio();
                    if (ratio > 0) {

                        // retrieve screen width, as the banner stretches across the whole width
                        Display display = mBanner.getContext().getDisplay();

                        if (display != null) {
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            display.getRealMetrics(displayMetrics);

                            int width = displayMetrics.widthPixels;

                            height = (int)(width / ratio);
                        }
                    }

                    // Resize the table view cell with the height value
                    height = (int) Math.max(DEFAULT_BANNER_HEIGHT, Math.min(MAX_BANNER_HEIGHT, height));
                    resizeBannerCell(height);
                }
            });

        }
    }


    /**
     * Loading an ad.
     * @param adPlacement the ad placement
     */
    public void loadAd(@NonNull SASAdPlacement adPlacement) {
        //Checking for ad loaded is not mandatory here, it just prevents us from reloading the ad.
        if (!mIsAdLoaded) {
            mBanner.loadAd(adPlacement);
        }
    }
}
