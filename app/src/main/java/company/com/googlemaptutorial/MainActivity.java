package company.com.googlemaptutorial;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;

// For location services
// https://developer.android.com/training/location/index.html
// For Google Map
// https://developers.google.com/maps/documentation/android-api
// For permissions
// https://developer.android.com/training/permissions/index.html

/**
 * Make sure you have the following done!
 * Get an API key for google maps at https://developers.google.com/maps/documentation/android-api/signup
 * Add the meta data part in your AndroidManifest.xml
 * <meta-data
 *      android:name="com.google.android.geo.API_KEY"
 *      android:value="@string/google_maps_key" />
 * Add permissions in AndroidManifest.xml
 *  <uses-permission android:name="android.permission.INTERNET" />
 *  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 * You actually need not to add the internet permission,
 *  as its automatically added because of google play services during compiled time
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //google map related
    private GoogleMap map;

    //location service related
    private GoogleApiClient mGoogleApiClient;
    // retrieved in location service, used in google map
    private Location mCurrentLocation;

    //for snackbar in requesting permissions
    private View rootLayout;
    private static final String TAG = "Main Activity";

    //requesting permissions related
    private static final int REQUEST_LOCATION_PERMISSIONS = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout = (View) findViewById(R.id.root_layout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
    //Google map
    public void setUpGoogleMap() {
        LatLng curLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(curLocation)
                .title("You are here"));
//        This one moves the camera to the specific location
        map.moveCamera(CameraUpdateFactory.newLatLng(curLocation));
//        This one moves the camera to the specific location and sets the zoom
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 18));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.addMarker(new MarkerOptions().position(latLng)
                        .title("Clicked Me"));
            }
        });
    }
    //Google map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (mCurrentLocation != null) {
            setUpGoogleMap();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    //Location service
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //check for permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i(TAG,
                        "Displaying camera permission rationale to provide additional context.");
                Snackbar.make(rootLayout, "This is some information!!!!",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok!", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION_PERMISSIONS);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSIONS);
            }
            return;
        }
        locationReceived();
    }
    // called when permission is granted
    // discard the errors
    public void locationReceived(){
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (map!=null){
            setUpGoogleMap();
        }
    }
    // handle user granting permission results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission is granted.");
                    locationReceived();
                } else {
                    Log.i(TAG, "Permission is not granted.");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
