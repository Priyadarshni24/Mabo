package com.advengers.mabo.Cometchat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.advengers.mabo.R;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class MapActivity extends MyActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private Context mContext;
    private EditText searchPlaceEt;
    private Button saveLocationBtn;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    Toolbar mToolbar;
    Marker marker = null;
    LatLng latlng;
    TextView txt_location;
    CameraPosition googlePlex;
    private static final int REQUEST_PLACE_PICKER = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mContext = MapActivity.this;
        mToolbar = (Toolbar)findViewById(R.id.mToolbar);
        txt_location = (TextView)findViewById(R.id.txt_location);
        txt_location.setOnClickListener(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.str_sendlocation));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setHasOptionsMenu(true);
        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initialize(getApplicationContext(), getString(R.string.PLACE_API_KEY));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //screen.Location Permission already granted
                buildGoogleApiClient();

                if(mMap != null)
                    mMap.setMyLocationEnabled(true);
            } else {
                //Request screen.Location Permission
                ///checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            if(mMap != null)
                mMap.setMyLocationEnabled(true);
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else {
            newPostPOIPick();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        mMap = googleMap;
        getUser();
        googlePlex = CameraPosition.builder()
                .target(new LatLng(Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongitude())))
                .zoom(15)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
       LatLng myplace = new LatLng(Double.parseDouble(user.getLatitude()),Double.parseDouble(user.getLongitude()));
        latlng = myplace;
        googleMap.addMarker(new MarkerOptions().position(myplace)
                .title("ME"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myplace));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);
       // googleMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).title("Near to me"));
        txt_location.setText(Tools.getfullAddress(MapActivity.this,myplace.latitude,myplace.longitude));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                if(marker!=null)
                {
                    marker.remove();
                }
                latlng = latLng;
                 markerOptions.position(latLng);
                markerOptions.draggable(true);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(customMarker(Tools.getfullAddress(MapActivity.this,latLng.latitude,latLng.longitude),latLng)));
                txt_location.setText(Tools.getfullAddress(MapActivity.this,latLng.latitude,latLng.longitude));
                marker = mMap.addMarker(markerOptions);
            }
        });

        /*setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                ///mHazardsMarker = mMap.addMarker(markerOptions);
            }
        });*/
/*
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng objLoc = marker.getPosition();
                return false;
            }
        });*/
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng objLoc = marker.getPosition();
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
        });
    }
    void newPostPOIPick() {
        // Construct an intent for the place picker
        try {

            // Set the fields to specify which types of place data to return.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,fields )
                    .build(this);
            startActivityForResult(intent,REQUEST_PLACE_PICKER );


        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_map, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String tagplace = place.getName();
                String tagplaceid = place.getId();
                latlng = place.getLatLng();
                String address = place.getAddress();
                LogUtils.e( "Place: " + place.getName() + ", " + place.getId()+" "+place.getLatLng());
                MarkerOptions markerOptions = new MarkerOptions();
                if(marker!=null)
                {
                    marker.remove();
                }
               // latlng = latLng;

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                googlePlex = CameraPosition.builder()
                        .target(latlng)
                        .zoom(15)
                        .bearing(0)
                        .tilt(0)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);
                markerOptions.position(latlng);
                markerOptions.draggable(true);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(customMarker(Tools.getfullAddress(MapActivity.this,latlng.latitude,latlng.longitude),latlng)));
                marker = mMap.addMarker(markerOptions);
                txt_location.setText(place.getName()+" "+  Tools.getfullAddress(MapActivity.this,latlng.latitude,latlng.longitude));
              //  binding.pinloction.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                LogUtils.e(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap customMarker(String address, LatLng latLng) {
        View markerLayout = getLayoutInflater().inflate(R.layout.custome_marker, null);

        ImageView markerImage = (ImageView) markerLayout.findViewById(R.id.marker_image);
        TextView markerRating = (TextView) markerLayout.findViewById(R.id.marker_text);
        //markerImage.setImageResource(R.drawable.ic_home_marker);
        markerRating.setText(address);
        markerRating.setVisibility(View.GONE);
        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());


        markerRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.e("I am clickable");
            }
        });
        markerRating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                LogUtils.e("I am touchable");
                return false;
            }
        });
        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker markr) {

     //   LatLng latLng = marker.getPosition();
     //   LogUtils.e("Lat lng "+latlng.latitude+" "+latlng.longitude);
       /* Intent intent=new Intent();
        intent.putExtra("latitude",String.valueOf(latlng.latitude));
        intent.putExtra("longitude",String.valueOf(latlng.longitude));
        setResult(RESULT_OK,intent);
        finish();*/

        return false;
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        intent.putExtra("latitude",String.valueOf(latlng.latitude));
        intent.putExtra("longitude",String.valueOf(latlng.longitude));
        setResult(RESULT_OK,intent);
        finish();
    }
}
