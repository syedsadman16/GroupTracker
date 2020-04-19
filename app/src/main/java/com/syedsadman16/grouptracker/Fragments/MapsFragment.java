package com.syedsadman16.grouptracker.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.syedsadman16.grouptracker.Activities.MainActivity;
import com.syedsadman16.grouptracker.Adapters.UserListAdapter;
import com.syedsadman16.grouptracker.Models.Members;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

//=============================================================================
// Displays the shared map
//
// Notes:
// - The setCoordsCurrentUser() updates latlng of individual users which will
// eventually generate latlng for all the users who use the map
// - Fused location chooses the best location provider and optimizes
// devices use of battery
// - Implemented interface from UserListAdapter to handle list clicks
//=============================================================================

public class MapsFragment extends Fragment implements OnMapReadyCallback, UserListAdapter.UserListClickListener{

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    Button destinationButton, helpButton;
    GoogleMap googleMap;
    LocationRequest locationRequest;
    Marker mCurrLocationMarker;
    RecyclerView memberRecyclerView;
    ArrayList<Members> membersArrayList = new ArrayList<>();
    UserListAdapter adapter;
    MarkerOptions markerOptions;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }


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

        memberRecyclerView = view.findViewById(R.id.member_list);
        memberRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        destinationButton = view.findViewById(R.id.destBtn);
        helpButton = view.findViewById(R.id.helpButton);

        createNotificationChannel();
    }


    @Override
    public void onMapReady(final GoogleMap map) {

        googleMap = map;
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        // Set level of accuracy for location requests
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // time interval to receive location updates
        locationRequest.setFastestInterval(5000); // fastest at which device can handle updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Check if network or gps is enabled
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        checkAccess(gps_enabled, network_enabled);

        // Locate user with blue dot and display other members on map
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            map.setMyLocationEnabled(true);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));
                            if(User.eventid != "null") {
                                notificationListener();
                                startLocationUpdates();
                                getMemberLocations();
                            }
                        } else {
                            Toast.makeText(getContext(), "Location Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        destinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEventLocation(map);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFirebaseNotificationStatus(User.fullName+ " needs help!");
            }
        });
    }


    // Notification status
    // If user requests notification, update event field in firebase
    // Set a listener. Each time data changes create notification. Reset it to null after notification is over.
    public void notificationListener(){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String notificationStatus = dataSnapshot.child(User.eventid).child("notificationStatus").getValue().toString();
                Log.i("Maps", notificationStatus);
                if(!notificationStatus.equals("null")){
                  createNotification(1, "GroupTracker *ALERT*", notificationStatus);
                }
                // Change it back to null so the next time app launches, notification isn't registered
                setFirebaseNotificationStatus("null");
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }

    public void setFirebaseNotificationStatus(String message){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
        reference.child(User.eventid).child("notificationStatus").setValue(message);
    }

    public void createNotification(int id, String title, String content){
        Intent intent = new Intent(getContext(), MainActivity.class);
        // Set flags to preserve users back button
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "MapsNotification")
                .setSmallIcon(R.drawable.chat_icon_24dp)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, builder.build());
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "GroupTracker";
            String description = "Emergency location notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("MapsNotification", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void getEventLocation(final GoogleMap map){
         Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events");
         reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String location = dataSnapshot.child(User.eventid).child("eventLocation").getValue().toString();
                LatLng latlng = getLocationFromAddress(getContext(), location);
                createMarker(latlng, "Destination", 2);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18.0f));
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });

    }

    // Each time location of current user changes, update database
    LocationCallback locationCallback = new LocationCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null)
                return;
            for (Location location : locationResult.getLocations()) { ;
                setCoordsCurrentUser(location);
            }
        }
    };


    // Update coordinates in firebase
    public void setCoordsCurrentUser(Location location){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users");
        reference.child(User.uid).child("Latitude").setValue(location.getLatitude());
        reference.child(User.uid).child("Longitude").setValue(location.getLongitude());
    }


    // Retrieves userid for each member and pass it to getMemberLocationInformation()
    // Populate user list under map
    public void getMemberLocations() {
        if(!User.eventid.equals("null")){
            UserListAdapter temp = new UserListAdapter(getContext(), membersArrayList, this );
            adapter = temp;
            memberRecyclerView.setAdapter(adapter);
            Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/events/"+User.eventid+"/Members");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    adapter.clear();
                    for(DataSnapshot child : dataSnapshot.getChildren() ){
                        String userid = child.child("uid").getValue().toString();
                        String full_name = child.child("fullName").getValue().toString();
                        getMemberLocationInformation(userid);
                        Members member = new Members(full_name, userid);
                        membersArrayList.add(member);
                        adapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) { adapter.clear(); firebaseError.getDetails(); }
            });
        }
    }


    // Using the UserID, marks user and destination markers to map for each user
    public void getMemberLocationInformation(String userid){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users/" + userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String longitude = dataSnapshot.child("Longitude").getValue().toString();
                String latitude = dataSnapshot.child("Latitude").getValue().toString();
                String name = dataSnapshot.child("First Name").getValue().toString();
                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                createMarker(latLng, name, 1);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }



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

    // Move to user location on map when list clicked on
    @Override
    public void onItemClicked(int position) {
        String uid = membersArrayList.get(position).getUserid();
        Toast.makeText(getContext(), "Clicked on " +membersArrayList.get(position).getName(), Toast.LENGTH_SHORT).show();
        moveCameratoUsersLatLng(uid, googleMap);
    }

    public void moveCameratoUsersLatLng(String uid, final GoogleMap map){
        Firebase reference = new Firebase("https://grouptracker-ef84c.firebaseio.com/users/" + uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String longitude = dataSnapshot.child("Longitude").getValue().toString();
                String latitude = dataSnapshot.child("Latitude").getValue().toString();
                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }


    // Mark location on map
    public void createMarker(LatLng latLng, String username, int type ){
        markerOptions = new MarkerOptions();
        if(type==1) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN)); // Users
        } else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(HUE_RED)); // Locations
        }
        markerOptions.position(latLng);
        markerOptions.title(username);
        mCurrLocationMarker = googleMap.addMarker(markerOptions);
    }

    // Retrive location from string - used to map event destination
    public LatLng getLocationFromAddress(Context context,String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (IOException ex) { ex.printStackTrace(); }
        return p1;
    }


    // Calling fusedLocation to update coordinates of users movements
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
        stopLocationUpdates();
        adapter.clear(); // Stop recycler data overlapping
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
