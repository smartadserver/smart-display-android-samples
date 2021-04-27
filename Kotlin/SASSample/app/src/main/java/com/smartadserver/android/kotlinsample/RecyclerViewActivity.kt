package com.smartadserver.android.kotlinsample

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.kotlinsample.holder.BannerViewHolder
import com.smartadserver.android.kotlinsample.holder.BannerViewHolderWrapper
import com.smartadserver.android.kotlinsample.holder.ListItemHolder
import kotlinx.android.synthetic.main.activity_recycler.*

class RecyclerViewActivity : AppCompatActivity() {

    // If you are an inventory reseller, you must provide your Supply Chain Object information.
    // More info here: https://help.smartadserver.com/s/article/Sellers-json-and-SupplyChain-Object
    private val supplyChainObjectString: String? = null // "1.0,1!exchange1.com,1234,1,publisher,publisher.com";

    // Needed placements
    private val bannerPlacement = SASAdPlacement(104808, 663262, 15140, "", supplyChainObjectString)
    private val parallaxPlacement = SASAdPlacement(104808, 663531, 15140, "", supplyChainObjectString)
    private val videoReadPlacement = SASAdPlacement(104808, 663530, 15140, "", supplyChainObjectString)

    private val adSpacing = 6

    private val bannerViewHolderWrappers = ArrayList<BannerViewHolderWrapper>()

    private val actionBarHeight by lazy {
        val styledAttributes =
            theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        styledAttributes.getDimension(0, 0F)
    }

    /**
     * Inner class representing the adapter responsible for creating RecyclerCiew.ViewHolder instances for different cells.
     */
    private inner class ListLayoutAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewTypeText = 0
        private val viewTypeAd = 1

        override fun getItemViewType(position: Int): Int =
            if (position == 0 || position % adSpacing != 0) {
                viewTypeText
            } else {
                viewTypeAd
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == viewTypeText) {
                ListItemHolder(inflater, parent) // Create classic item holder.
            } else {
                BannerViewHolder(inflater, parent) // Create banner view holder.
            }
        }

        override fun getItemCount(): Int = 400

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) == viewTypeText && viewHolder is ListItemHolder) {
                val title =
                    if (position == 0) "Multiple banners in RecyclerView integration" else "Lorem ipsum dolor sit amet"
                val subtitle =
                    if (position == 0) "See implementation in RecyclerActivity. Please scroll down to see the ads." else "Phasellus in tellus eget arcu volutpat bibendum vulputate ac sapien. Vivamus enim elit, gravida vel consequat sit amet, scelerisque vitae ex."
                viewHolder.configureForItem(title, subtitle, position)
            } else if (getItemViewType(position) == viewTypeAd && viewHolder is BannerViewHolder) {
                wrapperForPosition(position)?.run {
                    if (isAvailable()) {
                        bannerViewHolder = viewHolder
                    }
                }
            }
        }

        override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
            super.onViewRecycled(holder)
            if (holder is BannerViewHolder) {
                wrapperForHolder(holder)?.run {
                    bannerViewHolder = null
                }
            }
        }

        /**
         * Get the correct BannerViewHolderWrapper for a given position.
         */
        private fun wrapperForPosition(position: Int): BannerViewHolderWrapper? {
            val tmpPosition = (position / adSpacing) - 1
            val index = tmpPosition % bannerViewHolderWrappers.size
            return if (index < bannerViewHolderWrappers.size) {
                bannerViewHolderWrappers[index]
            } else {
                null
            }
        }

        /**
         * Find the correct BannerViewHolderWrapper for the given BannerViewHolder.
         */
        private fun wrapperForHolder(holder: BannerViewHolder): BannerViewHolderWrapper? {
            if (bannerViewHolderWrappers.size > 0) {
                for (wrapper in bannerViewHolderWrappers) {
                    if (wrapper.bannerViewHolder == holder) {
                        return wrapper
                    }
                }
            }

            return null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)

        // Set up recycler view
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            adapter = ListLayoutAdapter()
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        // Create and load ads
        bannerViewHolderWrappers.add(createBannerViewHolderWrapper(bannerPlacement))
        bannerViewHolderWrappers.add(createBannerViewHolderWrapper(parallaxPlacement))
        bannerViewHolderWrappers.add(createBannerViewHolderWrapper(videoReadPlacement))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // add a OnGlobalLayoutListener to execute adaptBannerHeight method once the activity has its new size set
        // (otherwise it uses previous orientation's size which is not what we want).
        recyclerView.post {
            recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    recyclerView.post {
                        updateBannersHeight()
                    }
                }
            })
        }
    }

    /**
     * Update banners height
     */
    private fun updateBannersHeight() {
        val defaultHeight = (50 * resources.displayMetrics.density).toInt()
        for (wrapper in bannerViewHolderWrappers) {
            wrapper.updateBannerSize(defaultHeight)
        }
    }

    /**
     * Create a bannerView holder wrapper ad load the given SASAdPlacement.
     */
    private fun createBannerViewHolderWrapper(adPlacement: SASAdPlacement): BannerViewHolderWrapper {
        val bannerViewHolderWrapper = BannerViewHolderWrapper(this)

        // Load the ad
        bannerViewHolderWrapper.loadAd(adPlacement)

        // To avoid having the parallax going below the actionBar, we have to set the parallax margin top, which is its top limit.
        // To do so, we set the action bar height as the parallaxMarginTop of the banner. Note that you can do the same with the bottom margin by using the setParallaxMarginBottom() method.
        bannerViewHolderWrapper.bannerView.setParallaxMarginTop(actionBarHeight.toInt())

        return bannerViewHolderWrapper
    }


}