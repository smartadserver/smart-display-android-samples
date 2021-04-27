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

private const val adPosition = 10

class CoordinatorLayoutActivity : AppCompatActivity() {

    // If you are an inventory reseller, you must provide your Supply Chain Object information.
    // More info here: https://help.smartadserver.com/s/article/Sellers-json-and-SupplyChain-Object
    private val supplyChainObjectString: String? = null // "1.0,1!exchange1.com,1234,1,publisher,publisher.com";

    // Ad Placement constant
    private val adPlacement = SASAdPlacement(104808, 663531, 15140, "", supplyChainObjectString)

    private val bannerViewHolderWrapper by lazy { BannerViewHolderWrapper(this) }

    private var appBarOffset = 0
    private var actionBarHeight = 0

    private inner class ListLayoutAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewTypeText = 0
        private val viewTypeAd = 1

        override fun getItemViewType(position: Int): Int = if (position == adPosition) {
            viewTypeAd
        } else {
            viewTypeText
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == viewTypeText) {
                ListItemHolder(inflater, parent) // Create classic item holder.
            } else {
                BannerViewHolder(inflater, parent) // Create banner view holder.
            }
        }

        override fun getItemCount(): Int {
            return 30
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) == viewTypeText && viewHolder is ListItemHolder) {
                val title =
                    if (position == 0) "Parallax in coordinator layout integration." else "Lorem ipsum dolor sit amet"
                val subtitle =
                    if (position == 0) "See implementation in CoordinatorLayoutActivity. Please scroll down to see the ad." else "Phasellus in tellus eget arcu volutpat bibendum vulputate ac sapien. Vivamus enim elit, gravida vel consequat sit amet, scelerisque vitae ex."
                viewHolder.configureForItem(title, subtitle, position)
            } else if (getItemViewType(position) == viewTypeAd && viewHolder is BannerViewHolder) {
                if (bannerViewHolderWrapper.isAvailable()) {
                    bannerViewHolderWrapper.bannerViewHolder = viewHolder
                }
            }
        }

        override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
            super.onViewRecycled(holder)
            if (holder is BannerViewHolder) {
                bannerViewHolderWrapper.bannerViewHolder = holder
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinator_layout)

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

        // Uncomment for manually set the offset of the parallax
        //
        //val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        //actionBarHeight = styledAttributes.getDimension(0, 0F).toInt()
        //
        //appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        //    appBarOffset = verticalOffset + actionBarHeight
        //    updateParallaxOffset()
        //})
        //
        //recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        //        super.onScrolled(recyclerView, dx, dy)
        //        updateParallaxOffset()
        //    }
        //})

        bannerViewHolderWrapper.loadAd(adPlacement)
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
        bannerViewHolderWrapper.updateBannerSize(defaultHeight)
    }

    /**
     * Used to set the parallax offset manually.
     */
    private fun updateParallaxOffset() = bannerViewHolderWrapper.bannerViewHolder?.let {
        bannerViewHolderWrapper.bannerView.setParallaxOffset(it.itemView.y.toInt() + appBarOffset)
    }

}