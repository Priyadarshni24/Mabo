package screen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.cometchat.pro.uikit.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import screen.Location.LocationTrack;

public class MapActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, OnMapReadyCallback,
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
    public String TAG = "Mabo";
    private static final int REQUEST_PLACE_PICKER = 10;
    private static final int RC_LOCATION = 1 ;
    Location loc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mContext = MapActivity.this;
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        txt_location = (TextView) findViewById(R.id.txt_location);
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

                if (mMap != null)
                    mMap.setMyLocationEnabled(true);
            } else {
                //Request screen.Location Permission
                ///checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            if (mMap != null)
                mMap.setMyLocationEnabled(true);
        }
        RequiresPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_LOCATION)
    private void RequiresPermission() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // getLocation();
            GetLocation();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.str_location),
                    RC_LOCATION, perms);
        }
    }
    public void GetLocation()
    {
        LocationTrack locationTrack = new LocationTrack(MapActivity.this);
        Log.e("",locationTrack.canGetLocation()+"");
        if (locationTrack.canGetLocation()&&locationTrack.getLatitude()!=0.0&&locationTrack.getLongitude()!=0.0) {

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            Log.e("","Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude));//.show();
            loc = new Location(LocationManager.GPS_PROVIDER);
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
        } else {
            if(!locationTrack.isCheckGPS())
            {
                if(!locationTrack.isShowAlert())
                  locationTrack.showSettingsAlert();
            }
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
        } else {
            newPostPOIPick();
        }


        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        mMap = googleMap;
        //getUser();
        mMap.setMyLocationEnabled(true);
        if(loc!=null) {

            googlePlex = CameraPosition.builder()
                    .target(new LatLng(loc.getLatitude(), loc.getLongitude()))
                    .zoom(15)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            LatLng myplace = new LatLng(loc.getLatitude(), loc.getLongitude());
            latlng = myplace;
            mMap.addMarker(new MarkerOptions().position(myplace)
                    .title("ME"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myplace));
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);
            txt_location.setText(getfullAddress(MapActivity.this, myplace.latitude, myplace.longitude));
        }
      /*  LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        screen.Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
           // onLocationChanged(location);
        }
        googlePlex = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(),location.getLongitude()))
                .zoom(15)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

       LatLng myplace = new LatLng(location.getLatitude(),location.getLongitude());
        latlng = myplace;
        googleMap.addMarker(new MarkerOptions().position(myplace)
                .title("ME"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myplace));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);
       // googleMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).title("Near to me"));
        txt_location.setText(getfullAddress(MapActivity.this,myplace.latitude,myplace.longitude));*/
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
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(customMarker(getfullAddress(MapActivity.this,latLng.latitude,latLng.longitude),latLng)));
                txt_location.setText(getfullAddress(MapActivity.this,latLng.latitude,latLng.longitude));
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
    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
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
            Log.e(TAG,e.getMessage());
        }
    }
    public String getfullAddress(Context context,double latitude,double longtitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(latitude, longtitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            Log.e(TAG,addresses.toString());
            //address + "," +
            return (address);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
                Log.e(TAG, "Place: " + place.getName() + ", " + place.getId()+" "+place.getLatLng());
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
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(customMarker(getfullAddress(MapActivity.this,latlng.latitude,latlng.longitude),latlng)));
                marker = mMap.addMarker(markerOptions);
                txt_location.setText(place.getName()+" "+  getfullAddress(MapActivity.this,latlng.latitude,latlng.longitude));
              //  binding.pinloction.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG,status.getStatusMessage());
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
                Log.e(TAG,"I am clickable");
            }
        });
        markerRating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e(TAG,"I am touchable");
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
     //   Log.e(TAG,"Lat lng "+latlng.latitude+" "+latlng.longitude);
       /* Intent intent=new Intent();
        intent.putExtra("latitude",String.valueOf(latlng.latitude));
        intent.putExtra("longitude",String.valueOf(latlng.longitude));
        setResult(RESULT_OK,intent);
        finish();*/

        return false;
    }

    @Override
    public void onClick(View view) {
        if(latlng!=null) {
            Intent intent = new Intent();
            intent.putExtra("latitude", String.valueOf(latlng.latitude));
            intent.putExtra("longitude", String.valueOf(latlng.longitude));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        GetLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        RequiresPermission();

    }
}
