package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Activity_High_Scores extends AppCompatActivity implements OnMapReadyCallback {

    private Fragment_List fragmentList;
    private Fragment_Map fragmentMap;

    private GoogleMap mMap;

    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        fragmentList = new Fragment_List();
        fragmentList.setActivity(this);
        fragmentList.setCallBackList(callBackList);
        getSupportFragmentManager().beginTransaction().add(R.id.frame1, fragmentList).commit();

        info = findViewById(R.id.info);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame2, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);

//        fragmentMap = new Fragment_Map();
//        fragmentMap.setActivity(this);
//        fragmentMap.setCallBack_map(callBack_map);
//        getSupportFragmentManager().beginTransaction().add(R.id.frame2, fragmentMap).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-33.852, 151.211);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        //LatLng laLang = new LatLng(10, 30);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private String locateCity(double lat, double lon) {
        return "Tel-Aviv";
    }

    CallBack_Map callBack_map = new CallBack_Map() {
        @Override
        public void mapClicked(double lat, double lon) {
            String city = locateCity(lat, lon);
            fragmentList.setTitle(city);
        }
    };

    CallBack_List callBackList = new CallBack_List() {
        @Override
        public void rowSelected(double longitude, double latitude, String playerName) {
            zoom(longitude, latitude, playerName);
        }
    };

    private void zoom(double longitude, double latitude, String name) {
        LatLng point = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(name));
        //LatLng laLang = new LatLng(10, 30);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
    }


    @Override
    protected void onStart() {
        super.onStart();
        fragmentList.setTitle("iOS");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(getApplicationContext(), "Dead", Toast.LENGTH_LONG).show();

        this.finish();
    }
}