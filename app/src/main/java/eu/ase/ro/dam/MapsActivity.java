package eu.ase.ro.dam;

import androidx.fragment.app.FragmentActivity;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eu.ase.ro.dam.util.BankLocation;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Random random;
    private List<BankLocation> bankList = new ArrayList<>();

    private static final int MAP_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        mMap = googleMap;
        random = new Random();

        getBanksLocations();
        for(int i=0; i<bankList.size(); i++) {
            BankLocation location = bankList.get(i);
            if (i == 0) {
                CameraPosition camPos = new CameraPosition(location.getCoordinates(),
                        MAP_ZOOM, 0, 0);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
            }
            mMap.addMarker(new MarkerOptions()
                    .position(location.getCoordinates())
                    .title(location.getAddress())
                    .snippet(location.getCountry())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        }

    }

    private void getBanksLocations() {
        bankList.add(new BankLocation(new LatLng(44.447750,26.096797), "Romania", "Piata Romana 6 B"));
        bankList.add(new BankLocation(new LatLng(44.446200,26.087816), "Romania", "Strada Mihail Moxa B"));
        bankList.add(new BankLocation(new LatLng(46.773431, 23.630890), "Romania", "Strada T. Mihali CJ"));
        bankList.add(new BankLocation(new LatLng(51.627957, -0.753153), "UK", "Queen Alexandra Rd"));
    }
}
