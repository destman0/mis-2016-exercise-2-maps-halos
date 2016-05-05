//This solution for the exercise #2 MIS SS 2016 has been made using the following sources:
//https://developers.google.com/maps/documentation/android-api/marker
//https://developer.android.com/training/basics/data-storage/shared-preferences.html
//http://stackoverflow.com/questions/9526592/calculating-radius-for-off-screen-map-locations
//http://stackoverflow.com/questions/14394366/find-distance-between-two-points-on-map-using-google-map-api-v2
//http://www.programcreek.com/java-api-examples/index.php?api=com.google.android.gms.maps.GoogleMap.OnCameraChangeListener
//https://developers.google.com/maps/documentation/android-api/shapes



package com.example.desperados.ex2halos;

        import android.Manifest;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.location.Location;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.FragmentActivity;
        import android.os.Bundle;
        import android.widget.EditText;


        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.CameraPosition;
        import com.google.android.gms.maps.model.Circle;
        import com.google.android.gms.maps.model.CircleOptions;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.LatLngBounds;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.model.VisibleRegion;

        import java.util.ArrayList;
        import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static EditText et_marker;
    public Circle shape;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final List<Circle> mCircles = new ArrayList<Circle>();






        //Added automatically
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        et_marker = (EditText)findViewById(R.id.et_marker);




        mMap.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener(){
                    public void onMapLongClick(LatLng loc) {

                        mMap.addMarker(new MarkerOptions()
                                .position(loc)
                                .title(et_marker.getText().toString()));
                        SharedPreferences sharedPref = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        //Saving the name and the location of the maker locally
                        editor.putInt("Latitude", (int)loc.latitude);
                        editor.putInt("Longitude", (int)loc.longitude);
                        editor.putString("Name",et_marker.getText().toString());
                        editor.commit();

                        shape = drawCircle(loc);
                        //Adding the circle object to the array of the circles
                        mCircles.add(shape);

                    }
                }

        );


        mMap.setOnCameraChangeListener(
                new OnCameraChangeListener() {

                    public void onCameraChange(CameraPosition cameraPosition){

                        //Reading our array of the circles
                        for(int i=0; i < mCircles.size(); i++) {
                            Circle object = mCircles.get(i);

                            //Getting the projection of the visible area of the sreen
                            VisibleRegion visreg = mMap.getProjection().getVisibleRegion();
                            LatLngBounds regbound = visreg.latLngBounds;

                            //Location of the center point on the right screen boundary
                            Location rightCent = new Location("rightCent");
                            rightCent.setLatitude(mMap.getCameraPosition().target.latitude);
                            rightCent.setLongitude(regbound.northeast.longitude);

                            //Location of the marker & the circle
                            Location markerLoc = new Location("Marker");
                            markerLoc.setLatitude(object.getCenter().latitude);
                            markerLoc.setLongitude(object.getCenter().longitude);

                            //The center of the screen
                            Location centerLoc = new Location("Center");
                            centerLoc.setLatitude(mMap.getCameraPosition().target.latitude);
                            centerLoc.setLongitude(mMap.getCameraPosition().target.longitude);



                            //Check if the maker is within the visible area of the screen
                            //And set the radius to zero
                            if (regbound.contains(object.getCenter())) {
                                 object.setRadius(0);
                                continue;
                            }

                            //Distance in memters between the marker and the center of the screen
                            Float distance = centerLoc.distanceTo(markerLoc);
                            //Distance in meters between the center of the screen and horizontal right position
                            Float boundrad = rightCent.distanceTo(centerLoc);
                            //Distance between the center and the right margin (1/10 of the screen)
                            Float bounddistpad = boundrad*9/10;
                            //Distance between the marker and the right margin
                            Float alt_dist = distance - bounddistpad;
                            //Draw a circle with a proper radius
                            object.setRadius(alt_dist);

                        }
                    }
                }
        );
    }

    //Drawing a circle at the specified location
    private Circle drawCircle(LatLng loc) {

        CircleOptions options = new CircleOptions()
                .center(loc)
                .radius(0)
                .fillColor(0x33000FF)
                .strokeColor(Color.BLUE)
                .strokeWidth(3);

        return mMap.addCircle(options);
    }

}
