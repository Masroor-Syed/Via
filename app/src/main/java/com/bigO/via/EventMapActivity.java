package com.bigO.via;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import io.indoorlocation.gps.GPSIndoorLocationProvider;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapOptions;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizeui.MapwizeFragment;

public class EventMapActivity extends AppCompatActivity implements MapwizeFragment.OnFragmentInteractionListener,
        MapwizeMap.OnVenueEnterListener, MapwizeMap.OnVenueExitListener {

    private MapwizeFragment mapwizeFragment;
    private MapwizeMap mapwizeMap;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 0;

    private static final LatLng BOUND_CORNER_NW = new LatLng(51.081066106290976, -114.13709700107574);
    private static final LatLng BOUND_CORNER_SE = new LatLng(51.079485532515136, -114.13504242897035);
    private static final LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
            .include(BOUND_CORNER_NW)
            .include(BOUND_CORNER_SE)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_map);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(bottomNavListener);

        MapOptions opts = new MapOptions.Builder()
            .language(Locale.getDefault().getLanguage())
            .centerOnVenue("5d86c0b3b2f753001620538d")
            .restrictContentToVenueId("5d86c0b3b2f753001620538d")
            .build();
        mapwizeFragment = MapwizeFragment.newInstance(opts);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapwizeFragment).commit();
            bottomNav.setSelectedItemId(R.id.map_view);
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment;
                    switch (item.getItemId()) {
                        case R.id.list_view:
                            selectedFragment = new EventListViewFragment();
                            break;
                        case R.id.schedule_view:
                            selectedFragment = new EventScheduleViewFragment();
                            break;
                        default:
                            selectedFragment = mapwizeFragment;
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public void onMenuButtonClick(){}

    @Override
    public void onInformationButtonClick(MapwizeObject mapwizeObject){
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mapwizeFragment)
                .commit();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new EventPlaceHighlightFragment(), "HIGHLIGHT")
                .commit();
    }

    @Override
    public void onBackPressed(){
        Fragment eventPlaceHighlightFragment = getSupportFragmentManager().findFragmentByTag("HIGHLIGHT");
        if (eventPlaceHighlightFragment != null && eventPlaceHighlightFragment.isVisible()) {
            getSupportFragmentManager()
                .beginTransaction()
                .remove(eventPlaceHighlightFragment)
                .commit();
            getSupportFragmentManager()
                .beginTransaction()
                .show(mapwizeFragment)
                .commit();
        }
        else {

            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentReady(MapwizeMap mapwizeMap) {

        Log.i("Debug", "indoor location provider");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        }

        this.mapwizeMap = mapwizeMap;
        GPSIndoorLocationProvider gpsIndoorLocationProvider = new GPSIndoorLocationProvider(this);
        gpsIndoorLocationProvider.start();

        this.mapwizeMap.setIndoorLocationProvider(gpsIndoorLocationProvider);
        MapboxMap mapboxMap = this.mapwizeMap.getMapboxMap();
        mapboxMap.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);
        mapboxMap.setMinZoomPreference(16);
        this.mapwizeMap.setFollowUserMode(FollowUserMode.FOLLOW_USER);

//        this.mapwizeMap.grantAccess("82a148cb703ba7fc2f0e50bbb3e31902", );

    }

    @Override
    public void onFollowUserButtonClickWithoutLocation(){
        Log.i("Debug", "onFollowUserButtonClickWithoutLocation event");
//
        this.mapwizeMap.setFollowUserMode(FollowUserMode.FOLLOW_USER);

    }

    @Override
    public boolean shouldDisplayInformationButton(MapwizeObject mapwizeObject) {
        return true;
    }

    @Override
    public boolean shouldDisplayFloorController(List<Floor> floors) {
        return false;
    }

    @Override
    public void onVenueEnter(@NonNull Venue venue) {

        Log.d("Debug","OnVenueEnter");

//        Intent eventMapIntent = new Intent(this, EventMapActivity.class);
//        this.startActivity(eventMapIntent);

    }

    @Override
    public void onVenueWillEnter(@NonNull Venue venue) {

        Log.d("Debug","OnVenueWillEnter");

//        Intent eventMapIntent = new Intent(mapwizeFragment.getContext(), EventMapActivity.class);
//        this.startActivity(eventMapIntent);

    }

    @Override
    public void onVenueExit(@NonNull Venue venue) {

        Log.d("Debug","OnVenueExit");

    }

}
