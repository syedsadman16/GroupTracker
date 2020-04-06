package com.syedsadman16.grouptracker.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }


    // After View has been inflated, reference all the methods that need to be called
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }


    // Handling the map events
    @Override
    public void onMapReady(GoogleMap map) {

        // Checking if location is enabled
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        // GPS Provider
        try {
            // Enable blue dot on map with ui control
            // Get animated Zooming effect
            // Suppressed permission check since its already handled above
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            map.setMyLocationEnabled(true);
            @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));
            setCoordsCurrentUser(location);
        } catch(Exception ex) { ex.printStackTrace(); }

        // Same but with Network Provider
        try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                map.setMyLocationEnabled(true);
                @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));
            setCoordsCurrentUser(location);
        } catch(Exception ex) { ex.printStackTrace(); }

        // If location is not enabled, prompt user with Alert Dialog
        if(!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Location is not enabled")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) { getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); }
                        })
                    .setNegativeButton("Cancel",null)
                    .show();
        }

    }



    // Update coordinates of user to Firebase
    public void setCoordsCurrentUser(Location location){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
        reference.child(User.uid).child("Latitude").setValue(location.getLatitude());
        reference.child(User.uid).child("Longitude").setValue(location.getLongitude());
    }



    // Lifecycle required for MapView
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        // Stop listener
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        // Stop listener
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
