package screen;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cometchat.pro.uikit.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.teliver.sdk.core.Teliver;
import com.teliver.sdk.core.TrackingListener;
import com.teliver.sdk.models.MarkerOption;
import com.teliver.sdk.models.TLocation;
import com.teliver.sdk.models.TrackingBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import screen.Location.LocationTrack;

import static constant.StringContract.IntentStrings.AVATAR;
import static constant.StringContract.IntentStrings.DURATION;
import static constant.StringContract.IntentStrings.TIME;
import static constant.StringContract.IntentStrings.UID;
import static constant.StringContract.IntentStrings.USER_NAME;

public class LivelocationActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {
    Context mContext;
    Toolbar mToolbar;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    LatLng latlng;
    CameraPosition googlePlex;
    Location loc;
    TextView golive;
    Marker marker = null;
    private static final int RC_LOCATION = 1 ;
   // FirebaseDatabase database;
  ///  DatabaseReference myRef;
    Timer t = new Timer();
    String uid;
    Handler handler = new Handler();
    Runnable runnable;
    int seconds,duration,minutes;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livelocation);

        mContext = LivelocationActivity.this;


        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.str_livelocation));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        uid= getIntent().getStringExtra(UID);
      //  database = FirebaseDatabase.getInstance();
     //   myRef = database.getReference("Mabo");
        golive = (TextView) findViewById(R.id.txt_golive);
      //  if(getIntent().getStringExtra("visibility").equals("false"))
        golive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent liveservice = new Intent(LivelocationActivity.this,MyLocationService.class);
                stopService(liveservice);*/
              //  Teliver.stopTrip("Tracking_"+uid);
            //    Teliver.stopTracking("Tracking_"+uid);
                //finish();
                onMoveLive();
            }
        });

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

    /*@Override
    protected void onResume() {
        if(mMap!=null)
        {
            handler.postDelayed(runnable,1000);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }*/

   /* void setHandler()
    {
        long delay = 1000;
        runnable = new Runnable() {
            @Override
            public void run() {
                getLiveLocation();
                long timerduration = (duration - minutes);// * 60 *1000 *1000;
                if(timerduration<=0)
                    handler.removeCallbacks(runnable);
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void getLiveLocation() {
        Log.d("timer", "timer ");
        myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
//                        Log.e("Mabo",user.uid+" "+user.avatar+" "+user.username+" "+user.latitude+" "+user.longitude);
                LatLng myplace = new LatLng(Double.parseDouble(user.latitude), Double.parseDouble(user.longitude));
                MarkerOptions markerOptions = new MarkerOptions();
                googlePlex = CameraPosition.builder()
                        .target(new LatLng(myplace.latitude, myplace.longitude))
                        .zoom(15)
                        .bearing(0)
                        .tilt(0)
                        .build();
                if(marker!=null)
                {
                    marker.remove();
                }
                markerOptions.position(myplace);
                markerOptions.draggable(true);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(customMarker(user.username,user.avatar)));
                marker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myplace));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void timerMethod()
    {
        Log.e("Mabo",""+duration+" "+minutes);
        long timerduration = (duration - minutes);// * 60 *1000 *1000;
        Log.e("Time Duration",""+timerduration);
        t.schedule(new TimerTask() {
            public void run() {


                Log.d("timer", "timer ");
                myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
//                        Log.e("Mabo",user.uid+" "+user.avatar+" "+user.username+" "+user.latitude+" "+user.longitude);
                        LatLng myplace = new LatLng(Double.parseDouble(user.latitude), Double.parseDouble(user.longitude));
                        MarkerOptions markerOptions = new MarkerOptions();
                        googlePlex = CameraPosition.builder()
                                .target(new LatLng(myplace.latitude, myplace.longitude))
                                .zoom(15)
                                .bearing(0)
                                .tilt(0)
                                .build();
                        if(marker!=null)
                        {
                            marker.remove();
                        }
                        markerOptions.position(myplace);
                        markerOptions.draggable(true);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(customMarker(user.username,user.avatar)));
                        marker = mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myplace));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        }, 1000, (timerduration*60000));

    }
*/
    @AfterPermissionGranted(RC_LOCATION)
    private void RequiresPermission() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.e("Mabo","Coming here Requires permission");
            GetLocation();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.str_location),
                    RC_LOCATION, perms);
        }
    }

    public void GetLocation()
    {
        LocationTrack locationTrack = new LocationTrack(LivelocationActivity.this);
        Log.e("Mabo",locationTrack.canGetLocation()+""+locationTrack.getLatitude()+" "+locationTrack.getLongitude());
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
   //     googleMap.setMyLocationEnabled(true);
        if(loc!=null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    long starttime = Long.parseLong(getIntent().getStringExtra(TIME));
                    long currenttime = System.currentTimeMillis();
                    long difference = currenttime - starttime;
                    minutes = (int) (difference / 1000)  / 60;
                    duration = Integer.parseInt(getIntent().getStringExtra(DURATION));
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                    Log.e("Mabo duration",""+duration+" "+sdf.format(starttime)+" "+sdf.format(currenttime)+ " "+minutes);
                    seconds = (int)((difference / 1000) % 60);
                    onMoveLive();



        }

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

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
 public void onMoveLive()
 {
     Log.e("Mabo "," "+minutes+" "+duration);
     if(minutes>=0&&minutes<=duration)
     {
         Log.e("Mabo ","Coming in OnMoveLive");
         MarkerOption option = new MarkerOption("Tracking_"+uid);
         option.setMarkerTitle(getIntent().getStringExtra(USER_NAME));

         Picasso.get().load(getIntent().getStringExtra(AVATAR)).transform(new CircleTransform()).into(new Target() {
             @Override
             public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                 option.setIconMarker(bitmap);
             }

             @Override
             public void onBitmapFailed(Exception e, Drawable errorDrawable) {

             }

             @Override
             public void onPrepareLoad(Drawable placeHolderDrawable) {

             }
         });
       //  option.setIconMarker(getBitmapFromURL(getIntent().getStringExtra(AVATAR)));
         Log.e("Mabo","Tracking_"+uid);
         Teliver.startTracking(new TrackingBuilder(option).withYourMap(mMap).withListener(new TrackingListener() {
             @Override
             public void onTrackingStarted(String trackingId) {
                 Log.e("Track started",trackingId);
             }

             @Override
             public void onLocationUpdate(String trackingId, TLocation location) {
                 LatLng myplace = new LatLng(location.getLatitude(), location.getLongitude());
                 googlePlex = CameraPosition.builder()
                         .target(new LatLng(myplace.latitude, myplace.longitude))
                         .zoom(20)
                         .bearing(0)
                         .tilt(0)
                         .build();

                 mMap.moveCamera(CameraUpdateFactory.newLatLng(myplace));
                 mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);
             }

             @Override
             public void onTrackingEnded(String trackingId) {
                 Log.e("Track Ended",trackingId);
             }

             @Override
             public void onTrackingError(String reason) {
                 Log.e("Track Error",reason);

             }
         }).build());

     }
     else
     {
         Toast.makeText(LivelocationActivity.this,"Live location sharing completed",Toast.LENGTH_SHORT).show();
         //stopsharing.setVisibility(View.GONE);
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
//            Log.e(TAG,addresses.toString());
            //address + "," +
            return (address);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Bitmap customMarker(String name, String avatar) {
        View markerLayout = getLayoutInflater().inflate(R.layout.custome_marker, null);

        ImageView markerImage = (ImageView) markerLayout.findViewById(R.id.marker_image);
        TextView markerRating = (TextView) markerLayout.findViewById(R.id.marker_text);
        //markerRating.setVisibility(View.GONE);
        //markerImage.setImageResource(R.drawable.ic_home_marker);
        Log.e("Mabo "," "+avatar);
        Glide.with(LivelocationActivity.this).load(avatar).apply(new RequestOptions().circleCrop()).into(markerImage).clearOnDetach();
        markerRating.setText(name);
        markerRating.setVisibility(View.GONE);
        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

       // Picasso.get().load(avatar).into(markerImage);

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
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
