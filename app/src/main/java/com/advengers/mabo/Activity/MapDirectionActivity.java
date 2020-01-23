package com.advengers.mabo.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.advengers.mabo.Fragments.PeoplesFragment;
import com.advengers.mabo.Interfaces.Keys;
import com.advengers.mabo.Model.Person;
import com.advengers.mabo.R;
import com.advengers.mabo.Tools.DirectionsJSONParser;
import com.advengers.mabo.Tools.MyActivity;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.databinding.ActivityMapdirectionBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.advengers.mabo.Interfaces.Keys.ID;
import static com.advengers.mabo.Interfaces.Keys.IMAGENAME;
import static com.advengers.mabo.Interfaces.Keys.LATITUDE;
import static com.advengers.mabo.Interfaces.Keys.LONGITUDE;
import static com.advengers.mabo.Interfaces.Keys.USERNAME;

public class MapDirectionActivity extends MyActivity implements OnMapReadyCallback , ClusterManager.OnClusterItemInfoWindowClickListener<Person>{
    ActivityMapdirectionBinding binding;
    GoogleMap mMap;
    private ClusterManager<Person> mClusterManager;
    ArrayList markerPoints= new ArrayList();
    String EstimatedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MapDirectionActivity.this,R.layout.activity_mapdirection);
        setSupportActionBar(binding.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.str_mapdirection));
        binding.mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        getUser();
        LatLng mylocation = new LatLng(Double.parseDouble(user.getLatitude()),Double.parseDouble(user.getLongitude()));
        LatLng Destination = new LatLng(Double.parseDouble(getIntent().getStringExtra(LATITUDE)),Double.parseDouble(getIntent().getStringExtra(Keys.LONGITUDE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 18));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        markerPoints.add(mylocation);
        markerPoints.add(Destination);
        MarkerOptions options = new MarkerOptions();
        options.position(mylocation);
        options.position(Destination);
        String url =   getDirectionsUrl(mylocation, Destination);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
        startDemo();

    }
    protected void startDemo() {

        mClusterManager = new ClusterManager<Person>(MapDirectionActivity.this, getMap());
        mClusterManager.setRenderer(new PersonRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
       /* mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);*/

        addItems();
        mClusterManager.cluster();

        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
    }

    @Override
    public void onClusterItemInfoWindowClick(Person person) {
        Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog, user.getId(),person.id, MapDirectionActivity.this,MapDirectionActivity.this);
    }

    private class PersonRenderer extends DefaultClusterRenderer<Person> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PersonRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Person person, MarkerOptions markerOptions) {
            if(person.profilePhoto!=null) {
                if (!person.profilePhoto.isEmpty()) {
                    Picasso.get().load(person.profilePhoto).error(R.drawable.ic_avatar).placeholder(R.drawable.ic_avatar).into(mImageView);
                    Bitmap icon = mIconGenerator.makeIcon();
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
                }
            }else
            {
                Picasso.get().load(person.profilePhoto).placeholder(R.drawable.ic_avatar).error(R.drawable.ic_avatar).into(mImageView);
                Bitmap icon = mIconGenerator.makeIcon();
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);

            }
        }



        @Override
        protected void onBeforeClusterRendered(Cluster<Person> cluster, MarkerOptions markerOptions) {
            List<String> profilePhotos = new ArrayList<String>(Math.min(4, cluster.getSize()));

            for (Person p : cluster.getItems()) {
                if(p.profilePhoto!=null)
                {
                    if(!p.profilePhoto.isEmpty()) {
                        if (profilePhotos.size() == 4) break;

                        Picasso.get().load(p.profilePhoto).error(R.drawable.ic_avatar).into(mClusterImageView);
                        profilePhotos.add(p.profilePhoto);
                    }
                }else
                {

                    Picasso.get().load(p.profilePhoto).error(R.drawable.ic_avatar).into(mClusterImageView);
                }
                Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            }
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }
    private void addItems() {
        mClusterManager.addItem(new Person(new LatLng(Double.parseDouble(user.getLatitude()),Double.parseDouble(user.getLongitude())), user.getUsername(),user.getId(),user.getprofile_imagename()));
        mClusterManager.addItem(new Person(new LatLng(Double.parseDouble(getIntent().getStringExtra(LATITUDE)),Double.parseDouble(getIntent().getStringExtra(LONGITUDE))), getIntent().getStringExtra(USERNAME),getIntent().getStringExtra(ID),getIntent().getStringExtra(IMAGENAME)));
    }
    public GoogleMap getMap()
    {
        return mMap;
    }
    public class DownloadTask extends AsyncTask<String,String,String> {


        public void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
            binding.strEstimatedtime.setText(getString(R.string.str_estimated)+" "+EstimatedTime);
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = downloadUrl(url[0]);
                JSONObject legs = new JSONObject(data);
                String duration = new JSONObject(legs.getJSONArray("routes").get(0).toString()).getJSONArray("legs").get(0).toString();
                EstimatedTime = new JSONObject(duration).getJSONObject("duration").getString("text");

                LogUtils.e("Routes "+ EstimatedTime);
            } catch (Exception e) {
                LogUtils.e("Background Task "+ e.toString());
            }
            return data;
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String,String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat").toString());
                    double lng = Double.parseDouble(point.get("lng").toString());
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(getResources().getColor(R.color.apptheme));
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=WALKING";
        String APIKEY = "key="+getString(R.string.PLACE_API_KEY);

        // Building the parameters to the web service//
        String parameters = str_origin + "&" + str_dest + "&" + sensor +"&" + mode +  "&"+APIKEY;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            LogUtils.e("URL "+strUrl);
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
           LogUtils.e( e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
