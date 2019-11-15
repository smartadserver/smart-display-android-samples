package com.smartadserver.android.kotlinsample

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.smartadserver.android.library.util.SASConfiguration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val siteId = 1337 // Your site id
    private val baseUrlString = "https://mobile.smartadserver.com" // Your base URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // First of all, configure the SDK
        SASConfiguration.getSharedInstance().configure(this, siteId, baseUrlString)

        // Enable output to Android Logcat (optional)
        SASConfiguration.getSharedInstance().isLoggingEnabled = true

        // Enable debugging in Webview if available (optional)
        WebView.setWebContentsDebuggingEnabled(true)

        // Create the adapter for the list view
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.activity_main_implementations_array)
        )

        // Create header and footer
        val header = layoutInflater.inflate(R.layout.activity_main_header, listView, false)
        val footer = layoutInflater.inflate(R.layout.activity_main_footer, listView, false)

        // Add header, footer and adapter to the list view
        listView.run {
            addHeaderView(header)
            addFooterView(footer)
            setAdapter(adapter)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val activityClass = when (position) {
                1 -> BannerActivity::class
                2 -> InAppBiddingBannerActivity::class
                3 -> InterstitialActivity::class
                4 -> InAppBiddingInterstitialActivity::class
                5 -> RewardedVideoActivity::class
                6 -> RecyclerViewActivity::class
                7 -> CoordinatorLayoutActivity::class
                8 -> NativeAdActivity::class
                9 -> NativeAdActivity::class
                else -> BannerActivity::class // fallback on banner activity, should never happen.
            }

            val intent = Intent(this, activityClass.java)

            if (position == 9) {
                intent.putExtra("withMedia", true)
            }

            startActivity(intent)
        }

        /**
         * GDPR Consent String manual setting.
         *
         * By uncommenting the following code, you will set the GDPR consent string manually.
         * Note: the Smart Display SDK will retrieve the consent string from the SharedPreferences using the official IAB key "IABConsent_ConsentString".
         *
         * If you are using a CMP that does not store the consent string in the SharedPreferences using the official
         * IAB key, please store it yourself with the official key.
         */
        // PreferenceManager.getDefaultSharedPreferences(this).edit { putString("IABConsent_ConsentString", "YourConsentString") }


        /**
         * GDPR Binary Consent Status manual setting.
         *
         * Some third party mediation SDKs are not IAB compliant concerning the consent string. Those SDK use
         * most of the time a binary consent for the advertising purpose.
         * If you are using one or more of those SDK through Smart mediation, you can set this binary consent for
         * all adapters at once by setting the string '1' (if the consent is granted) or '0' (if the consent is denied)
         * in the SharedPreferences for the key 'Smart_advertisingConsentStatus'.
         */
        // PreferenceManager.getDefaultSharedPreferences(this).edit { putString("Smart_advertisingConsentStatus", "1" or "0") }
    }
}
