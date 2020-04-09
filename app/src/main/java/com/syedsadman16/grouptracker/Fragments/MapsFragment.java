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

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

//=============================================================================
// Displays the shared map
//
// Notes:
// The setCoordsCurrentUser() updates latlng of individual users which will
// eventually generate latlng for all the users who use the map
//=============================================================================

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    GoogleMap googleMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    LocationRequest locationRequest;
    Marker mCurrLocationMarker;
    private FusedLocationProviderClient fusedLocationClient;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());


    }


    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        // Set level of accuracy for location requests
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // time interval to receive location updates
        locationRequest.setFastestInterval(5000); // fastest at which device can handle updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // Enable blue dot on map with zoom effect
        // Suppressed permission check since its already handled above
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            map.setMyLocationEnabled(true);
            @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));
            startLocationUpdates();
            getMemberLocations();
        } catch(Exception ex) { ex.printStackTrace(); }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            map.setMyLocationEnabled(true);
            @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));
            startLocationUpdates();
            getMemberLocations();
        } catch(Exception ex) { ex.printStackTrace(); }

        // If access not granted, request user
        checkAccess(gps_enabled, network_enabled);

    }

    // Each time location is changed, update database
    LocationCallback locationCallback = new LocationCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null)
                return;

            for (final Location location : locationResult.getLocations()) {
               // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
               // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                setCoordsCurrentUser(location);
            }
        }
    };

    // Check location permissions and status
    public void checkAccess(boolean gps_enabled, boolean network_enabled){

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

        // If app location permission declined, ask to grant access
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Please grant access to location permissions.")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getContext().getPackageName()));
                            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                            myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(myAppSettings);
                        }
                    })
                    .setNegativeButton("Cancel",null)
                    .show();
        }
    }



    public void setCoordsCurrentUser(Location location){
        // Update coordinates for specific user
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
        reference.child(User.uid).child("Latitude").setValue(location.getLatitude());
        reference.child(User.uid).child("Longitude").setValue(location.getLongitude());

        // Update coordinate in Members list
        Firebase event_reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events/"+User.eventid);
        event_reference.child("Members").child(User.uid).child("Latitude").setValue(location.getLatitude());
        event_reference.child("Members").child(User.uid).child("Longitude").setValue(location.getLongitude());
    }


    // Get coordinates of all users IF user is in event AND user has coordinates
    public void getMemberLocations() {
        if(!User.eventid.equals("null")){

            Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events/"+User.eventid+"/Members");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren() ){
                        String longitude = child.child("Longitude").getValue().toString();
                        String latitude = child.child("Latitude").getValue().toString();
                        String name = child.child("name").getValue().toString();
                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        createMarker(latLng, name);
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });

        } else {
            Toast.makeText(getContext(), "Event error", Toast.LENGTH_SHORT).show();
        }
    }

    // Mark location on map
    public void createMarker(LatLng latLng, String username){
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(username);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = googleMap.addMarker(markerOptions);
    }


    private void startLocationUpdates() {
        // Looper tells it to repeat forever until thread is destroyed
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    // Lifecycle required for MapView
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        startLocationUpdates();
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
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
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
