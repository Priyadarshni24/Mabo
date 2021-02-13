package screen.messagelist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cometchat.pro.uikit.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.models.TripBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import screen.Location.LocationTrack;

import static constant.StringContract.IntentStrings.AVATAR;
import static constant.StringContract.IntentStrings.DURATION;
import static constant.StringContract.IntentStrings.MYUID;
import static constant.StringContract.IntentStrings.TIME;
import static constant.StringContract.IntentStrings.UID;
import static constant.StringContract.IntentStrings.USER_NAME;

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
    TextView txt_location, txt_livelocation;
    CameraPosition googlePlex;
    public String TAG = "Mabo";
    private static final int REQUEST_PLACE_PICKER = 10;
    private static final int RC_LOCATION = 1;
    Location loc;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;
    String Uid, username, myuid, avatar, lat, lng, time;
    int selected = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mContext = MapActivity.this;

        Uid = getIntent().getExtras().getString(UID);
        myuid = getIntent().getExtras().getString(MYUID);
        username = getIntent().getExtras().getString(USER_NAME);
        avatar = getIntent().getExtras().getString(AVATAR);
        // Write a message to the database
       /* database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Mabo");*/


        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        txt_location = (TextView) findViewById(R.id.txt_location);
        txt_livelocation = (TextView) findViewById(R.id.txt_livelocation);
        txt_location.setOnClickListener(this);
        txt_livelocation.setOnClickListener(this);
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
            initLocation();
            GetLocation();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.str_location),
                    RC_LOCATION, perms);
        }
    }

    private void initLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetLocation() {
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
          //  myRef.child("users").child(Uid).child("latitude").setValue(String.valueOf(loc.getLatitude()));
          //  myRef.child("users").child(Uid).child("longitude").setValue(String.valueOf(loc.getLongitude()));
        }
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
        if(view.getId() == R.id.txt_location)
        {
            if (latlng != null) {
                Intent intent = new Intent();
                intent.putExtra("latitude", String.valueOf(latlng.latitude));
                intent.putExtra("longitude", String.valueOf(latlng.longitude));
                intent.putExtra("livelocation","0");
                intent.putExtra(TIME,"0");
                intent.putExtra(DURATION,"0");
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        else if(view.getId() == R.id.txt_livelocation)
        {
            selected = 0;
            showAlertDialog();
               /*AlertDialog builder = new AlertDialog.Builder(this).create();
               LayoutInflater inflater = getLayoutInflater();
               //builder.setTitle("With RatingBar");
               builder.setCancelable(false);
               View dialogLayout = inflater.inflate(R.layout.alert_livelocation, null);
               builder.setView(dialogLayout);
               RadioGroup radiogrp = dialogLayout.findViewById(R.id.rgrp_duration);
               Button ok = dialogLayout.findViewById(R.id.ok);
               Button cancel = dialogLayout.findViewById(R.id.cancel);
               radiogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(RadioGroup group, int checkedId) {
                       if(checkedId == R.id.fifteen)
                       {
                          selected = 15;
                           Log.e("",selected+"");
                       }else if(checkedId == R.id.thirty)
                       {
                           selected = 30;
                           Log.e("",selected+"");
                       }else if(checkedId == R.id.hour)
                       {
                           selected = 60;
                           Log.e("",selected+"");
                       }
                   }
               });

               builder.show();
               cancel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                     builder.dismiss();
                   }
               });
               ok.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if(selected == 0) {
                    *//*       Intent locationservice = new Intent(mContext, MyLocationService.class);
                           locationservice.putExtra("Duration", selected);
                           startService(locationservice);*//*
                           Intent intent = new Intent();
                           intent.putExtra("latitude", String.valueOf(latlng.latitude));
                           intent.putExtra("longitude", String.valueOf(latlng.longitude));
                           intent.putExtra("livelocation","1");
                           setResult(RESULT_OK, intent);
                           finish();
                       }else {
                           Toast.makeText(mContext,"Select the duration",Toast.LENGTH_SHORT).show();
                       }
                   }
               });*/
        }

    }
    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this);
        alertDialog.setTitle("Select Duration");
        String[] items = {"15 minutes","30 minutes","one hour"};
        int checkedItem = 0;
        selected = 15;


        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        selected = 15;
                        break;
                    case 1:
                        selected = 30;
                        break;
                    case 2:
                        selected = 60;
                        break;
                }
                Log.e("Mabo",selected+"");
            }
        });
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                if(selected != 0) {
                  //  writeNewUser(Uid,username,avatar,String.valueOf(latlng.latitude),String.valueOf(latlng.longitude));
                    Log.e("Mabo",myuid+" myuid");
                    time = String.valueOf(System.currentTimeMillis());
                    /*Intent locationservice = new Intent(mContext, MyLocationService.class);
                    locationservice.putExtra(DURATION, String.valueOf(selected));
                    locationservice.putExtra(UID,myuid);
                    locationservice.putExtra(USER_NAME,username);
                    locationservice.putExtra(AVATAR,avatar);
                    locationservice.putExtra(TIME,time);
                    startService(locationservice);*/
                    Log.e("Mabo","Tracking_"+myuid);
                    Teliver.startTrip(new TripBuilder("Tracking_"+myuid).build());
                    Intent intent = new Intent();
                    intent.putExtra("latitude", String.valueOf(latlng.latitude));
                    intent.putExtra("longitude", String.valueOf(latlng.longitude));
                    intent.putExtra("livelocation","1");
                    intent.putExtra(DURATION,String.valueOf(selected));
                    intent.putExtra(TIME,time);
                    setResult(RESULT_OK, intent);

                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                    //System.out.println(sdf.format(resultdate));

                    Log.e("Mabo start time",sdf.format(Long.parseLong(time)));

                    finish();
                }else if(selected == 0){
                    Toast.makeText(mContext,"Select the duration",Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
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
