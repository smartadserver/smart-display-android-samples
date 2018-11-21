package com.smartadserver.android.sassample;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;

import com.smartadserver.android.library.util.SASConfiguration;

public class MainActivity extends AppCompatActivity {

    static private final int SITE_ID = 1337; // Your SITE_ID
    static private final String BASE_URL = "https://mobile.smartadserver.com"; // Your base url

    private ListView mListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // First of all, configure the SDK
        try {
            SASConfiguration.getSharedInstance().configure(this, SITE_ID, BASE_URL);
        } catch (SASConfiguration.ConfigurationException e) {
            Log.w("Sample", "Smart SDK configuration failed: " + e.getMessage());
        }

        //Set Title
        setTitle(R.string.title_activity_main);

        //Prepare listView
        mListView = createListView();

        //Setup clickListener
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedIndex = position;

                switch (selectedIndex) {
                    case 1:
                        startBannerActivity();
                        break;
                    case 2:
                        startInterstitialActivity();
                        break;
                    case 3:
                        startRewardedVideoActivity();
                        break;
                    case 4:
                        startRecyclerActivity();
                        break;
                    case 5:
                        startCoordinatorLayoutActivity();
                        break;
                    case 6:
                        startNativeActivity(false);
                        break;
                    case 7:
                        startNativeActivity(true);
                        break;

                    default:
                        break;
                }
            }

        });

        /**
         * GDPR Consent String manual setting.
         *
         * By uncommenting the following code, you will set the GDPR consent string manually.
         * Note: the Smart Display SDK will use retrieve the consent string from the SharedPreferences using the official IAB key "IABConsent_ConsentString".
         * If using the SmartCMP SDK, you will not have to do this because the SmartCMP already stores the consent string
         * using the official key.
         * If you are using any other CMP that do not store the consent string in the SharedPreferences using the official
         * IAB key, please store it yourself with the official key.
         */
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // SharedPreferences.Editor editor = prefs.edit();
        // editor.putString("IABConsent_ConsentString", "YourConsentString");
        // editor.apply();
    }

    //////////////////////////
    // ListView Init
    //////////////////////////

    private ListView createListView() {

        //Create data to display
        Resources res = getResources();
        String[] itemList = res.getStringArray(R.array.activity_main_implementations_array);

        //Find listView and set Adapter
        ListView listView = (ListView) findViewById(R.id.list_view);

        //Create adapter
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList);

        //Create header and footer
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.activity_main_header, mListView, false);
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.activity_main_footer, mListView, false);

        //Add Header and footer
        listView.addHeaderView(header, null, false);
        listView.addFooterView(footer, null, false);

        //Set ListView Adapter
        listView.setAdapter(adapter);

        return listView;
    }

    //////////////////////////
    // Starting Activities
    //////////////////////////

    private void startBannerActivity() {
        Intent intent = new Intent(this, BannerActivity.class);
        startActivity(intent);
    }

    private void startInterstitialActivity() {
        Intent intent = new Intent(this, InterstitialActivity.class);
        startActivity(intent);
    }

    private void startRewardedVideoActivity() {
        Intent intent = new Intent(this, RewardedVideoActivity.class);
        startActivity(intent);
    }

    private void startRecyclerActivity() {
        Intent intent = new Intent(this, RecyclerActivity.class);
        startActivity(intent);
    }

    private void startCoordinatorLayoutActivity() {
        Intent intent = new Intent(this, CoordinatorLayoutActivity.class);
        startActivity(intent);
    }

    private void startNativeActivity(boolean withMedia) {
        Intent intent = new Intent(this, NativeActivity.class);

        if (withMedia) {
            intent.putExtra("withMedia", true);
        }

        startActivity(intent);
    }


}
