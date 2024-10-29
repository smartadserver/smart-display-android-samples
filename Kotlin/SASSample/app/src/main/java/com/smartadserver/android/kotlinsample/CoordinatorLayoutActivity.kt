package com.smartadserver.android.kotlinsample

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartadserver.android.kotlinsample.databinding.ActivityCoordinatorLayoutBinding
import com.smartadserver.android.kotlinsample.databinding.ListBannerHolderBinding
import com.smartadserver.android.kotlinsample.databinding.ListItemBinding
import com.smartadserver.android.library.model.SASAdPlacement
import com.smartadserver.android.kotlinsample.holder.BannerViewHolder
import com.smartadserver.android.kotlinsample.holder.BannerViewHolderWrapper
import com.smartadserver.android.kotlinsample.holder.ListItemHolder

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

    // Binding object to retrieve UI elements
    private val binding : ActivityCoordinatorLayoutBinding by lazy { ActivityCoordinatorLayoutBinding.inflate(layoutInflater) }

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
                ListItemHolder(ListItemBinding.inflate(inflater, parent, false)) // Create classic item holder.
            } else {
                BannerViewHolder(ListBannerHolderBinding.inflate(inflater, parent, false)) // Create banner view holder.
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
        enableEdgeToEdge()

        // set content view from binding
        setContentView(binding.root)

        // apply edge-to-edge specific insets to preserve coordinator layout behavior compatible with
        // transparent status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->

            val cutoutInsets = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            val leftInsets = cutoutInsets.left + navBarInsets.left + if (isLandscape) { systemBarsInsets.left } else { 0 }
            val topInsets = cutoutInsets.top + navBarInsets.top + if (isLandscape) { systemBarsInsets.top } else { 0 }
            val rightInsets = cutoutInsets.right + navBarInsets.right + if (isLandscape) { systemBarsInsets.right } else { 0 }
            val bottomInsets = cutoutInsets.bottom + navBarInsets.bottom + if (isLandscape) { systemBarsInsets.bottom } else { 0 }

            v.setPadding(leftInsets, topInsets, rightInsets, bottomInsets)
            insets
        }

        // Set up recycler view
        binding.recyclerView.run {
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
        binding.recyclerView.post {
            binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    binding.recyclerView.post {
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