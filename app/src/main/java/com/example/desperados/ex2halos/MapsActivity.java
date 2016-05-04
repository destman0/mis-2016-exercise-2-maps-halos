package com.example.desperados.ex2halos;

        import android.Manifest;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.graphics.Point;
        import android.location.Location;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.FragmentActivity;
        import android.os.Bundle;
        import android.util.DisplayMetrics;
        import android.util.Log;
        import android.view.Display;
        import android.widget.EditText;


        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapView;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
        import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.CameraPosition;
        import com.google.android.gms.maps.model.Circle;
        import com.google.android.gms.maps.model.CircleOptions;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.Projection;


        import java.lang.Math;
        import java.text.DecimalFormat;
        import java.util.ArrayList;
        import java.util.Iterator;
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


// Testing after recommit

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
                        editor.putInt("Latitude", (int)loc.latitude);
                        editor.putInt("Longitude", (int)loc.longitude);
                        editor.putString("Name",et_marker.getText().toString());
                        editor.commit();

                        shape = drawCircle(loc);
                        mCircles.add(shape);

                    }
                }

        );

        // Add a marker in Sydney and move the camera
        //LatLng berlin = new LatLng(52, 13);
        // mMap.addMarker(new MarkerOptions().position(berlin).title("Marker in Berlin"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(berlin));


        mMap.setOnCameraChangeListener(
                new OnCameraChangeListener() {

                    public void onCameraChange(CameraPosition cameraPosition){
                        //shape = drawCircle(mMap.getCameraPosition().target);
                        //shape.setRadius(1000);

                        /*Log.i("How many circles", "" + mCircles.size());*/
                        for(int i=0; i < mCircles.size(); i++) {
                            Circle object = mCircles.get(i);

                            Location markerLoc = new Location("Marker");
                            markerLoc.setLatitude(object.getCenter().latitude);
                            markerLoc.setLongitude(object.getCenter().longitude);

                            Location centerLoc = new Location("Center");
                            centerLoc.setLatitude(mMap.getCameraPosition().target.latitude);
                            centerLoc.setLongitude(mMap.getCameraPosition().target.longitude);

                            Float distance = centerLoc.distanceTo(markerLoc);



                            Projection projection = mMap.getProjection();

                            Display mDisp = getWindowManager().getDefaultDisplay();
                            Point mDispSize = new Point();
                            mDisp.getSize(mDispSize);
                            float cent_screen_x = mDispSize.x/2;
                            float cent_screen_y = mDispSize.y/2;

                            Point mark_screen = projection.toScreenLocation(object.getCenter());
                            float mark_screen_x = mark_screen.x;
                            float mark_screen_y = mark_screen.y;

                            float dx = Math.abs(mark_screen_x - cent_screen_x);
                            float dy = Math.abs(mark_screen_y - cent_screen_y);

                            float ox = dx - ((mDispSize.x / 2) - 100);
                            float oy = dy - ((mDispSize.y / 2) - 100);

                            if (ox < 100) ox = 0;
                            if (oy < 100) oy = 0;

                            double radius = Math.sqrt((ox*ox) + (oy*oy));
                            float zoom = mMap.getCameraPosition().zoom;
                            //double scale = Math.pow(2, zoom);
                            double scale = 156543.03392 * Math.cos( mMap.getCameraPosition().target.latitude * Math.PI / 180) / Math.pow(2, zoom);
                            double alt_dist = radius * scale;









                            Log.i("Distance", ""  + distance);
                            Log.i("Radius", ""  + radius );
                            Log.i("Zoom", ""  + zoom );
                            Log.i("Alt", ""  + alt_dist );







/*                            Point cent_screen = projection.toScreenLocation(mMap.getCameraPosition());

                            dx = abs(float(object.getCenter().longitude) - mMap.getCameraPosition().target.longitude);
                            dy = abs(object.getCenter().latitude - mMap.getCameraPosition().target.latitude);

                            ox = dx - ((screenSize.x / 2) - padding);
                            oy = dy - ((screenSize.y / 2) - padding);

                            if (ox < 0) ox = 0;
                            if (oy < 0) oy = 0;

                            radius = sqrt((ox*ox) + (oy*oy));*/






                            object.setRadius(alt_dist);

                        }



                    }
                }

        );





    }

    private Circle drawCircle(LatLng loc) {

/*        Location markerLoc = new Location("Marker");
        markerLoc.setLatitude(loc.latitude);
        markerLoc.setLongitude(loc.longitude);

        Location centerLoc = new Location("Center");
        centerLoc.setLatitude(mMap.getCameraPosition().target.latitude);
        centerLoc.setLongitude(mMap.getCameraPosition().target.longitude);

        Float distance = centerLoc.distanceTo(markerLoc);*/


        CircleOptions options = new CircleOptions()
                .center(loc)
                .radius(0)
                .fillColor(0x33000FF)
                .strokeColor(Color.BLUE)
                .strokeWidth(3);

        return mMap.addCircle(options);
    }






    /*public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }*/


/*    public GoogleMap.OnCameraChangeListener getCameraChangeListener()
    {
        return new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition position)
            {
                //addItemsToMap(this.items);
                //shape = drawCircle(mMap.getCameraPosition().target);
                LatLng berlin = new LatLng(52, 13);
                shape = drawCircle(berlin);

            }
        };
    }*/



}
