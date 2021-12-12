package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Activity_High_Scores extends AppCompatActivity implements OnMapReadyCallback {

    private Fragment_List fragment_List;

    private GoogleMap mMap;

    private ImageButton panel_BTN_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
        Screen_Utils.hideSystemUI(this);

        fragment_List = new Fragment_List();
        fragment_List.setActivity(this);
        fragment_List.setCallBackList(callBackList);
        getSupportFragmentManager().beginTransaction().add(R.id.frame1, fragment_List).commit();

        panel_BTN_back = findViewById(R.id.panel_BTN_back);
        panel_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMenu();
            }
        });

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame2, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng melbourne = new LatLng(-37.67073140377376, 144.84332141711963);
        mMap.addMarker(new MarkerOptions()
                .position(melbourne)
                .title("Flight Destination : Melbourne Airport, Vic, Australia"));
        //LatLng laLang = new LatLng(10, 30);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne, 14.0f));
    }

    CallBack_List callBackList = new CallBack_List() {
        @Override
        public void rowSelected(double longitude, double latitude, String playerName) {
            zoom(longitude, latitude, playerName);
        }

        @Override
        public void clearListClicked() {
            backToMenu();
        }
    };

    private void backToMenu() {
        this.finish();
    }

    private void zoom(double longitude, double latitude, String name) {
        LatLng point = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("* Crash Site * | Pilot Name: " + name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13.0f));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(getApplicationContext(), "Dead", Toast.LENGTH_LONG).show();

        this.finish();
    }
}