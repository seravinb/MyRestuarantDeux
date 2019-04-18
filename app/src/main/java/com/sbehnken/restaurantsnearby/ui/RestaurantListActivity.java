package com.sbehnken.restaurantsnearby.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sbehnken.restaurantsnearby.Constants;
import com.sbehnken.restaurantsnearby.R;
import com.sbehnken.restaurantsnearby.adapters.RestaurantListAdapter;
import com.sbehnken.restaurantsnearby.models.Restaurant;
import com.sbehnken.restaurantsnearby.services.YelpService;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RestaurantListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RestaurantListAdapter mAdapter;

    public ArrayList<Restaurant> restaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        mRecyclerView = findViewById(R.id.recyclerView);

        //this keeps overriding user inputted zipcode
//        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String mRecentAddress = mSharedPreferences.getString(Constants.PREFERENCES_LOCATION_KEY, null);
//        if (mRecentAddress != null) {
//            getRestaurants(mRecentAddress);
//        }

        String location = getIntent().getStringExtra("zipcode");
        getRestaurants(location);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Restaurants Nearby");

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                    case  android.R.id.home:
                finish();

                case R.id.favorites_item:
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(android.R.id.content, new FavoritesListFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();

                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    private void getRestaurants(String location) {

       final YelpService yelpService = new YelpService();
        YelpService.findRestaurants(location, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                restaurants = yelpService.processResults(response);

                RestaurantListActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mAdapter = new RestaurantListAdapter(getApplicationContext(), restaurants);
                        mRecyclerView.setAdapter(mAdapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RestaurantListActivity.this);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setHasFixedSize(true);
                    }
                });
            }
        });
    }
}
