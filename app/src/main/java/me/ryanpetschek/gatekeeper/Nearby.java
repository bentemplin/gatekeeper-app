package me.ryanpetschek.gatekeeper;

import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;


public class Nearby extends FragmentActivity implements OnMapReadyCallback,
        nearbyFragment.OnFragmentInteractionListener {
    private final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private LocationManager mLocMan;
    private Location mLoc;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Nearby Buildings");

        mLocMan = (LocationManager) getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_nearby);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        getLocationPermission();
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_ASK_PERMISSIONS);
        mMap = googleMap;
        LatLng sydney = new LatLng(33.7563, -84.3893);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Alderhold"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng hopkins = new LatLng(33.7741, -84.3913);
        mMap.addMarker(new MarkerOptions().position(hopkins).title("Hopkins Hall"));
        LatLng anthem = new LatLng(33.7772, -84.3891);
        mMap.addMarker(new MarkerOptions().position(anthem).title("Anthem Innovation Studio"));
        LatLngBounds bounds = new LatLngBounds(new LatLng(33.75, -84.44), new LatLng(33.8, -84.34));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));

//        mLoc = mLocMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        mMap.setMyLocationEnabled(true);

//        LatLng me = new LatLng(mLoc.getLatitude(), mLoc.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(me).title("Me"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
    }


    public void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
        int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Log.d("LAT", String.valueOf(mLoc.getLatitude()));
                        Log.d("LONG", String.valueOf(mLoc.getLongitude()));

                        return;
                        //}
                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    mMap.setMyLocationEnabled(true);
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request
            }
        }}
    @Override
    public void onFragmentInteraction(Uri uri) {
        //empty b/c not implemented
    }
}
