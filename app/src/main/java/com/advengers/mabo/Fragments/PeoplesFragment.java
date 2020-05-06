package com.advengers.mabo.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.advengers.mabo.Activity.App;
import com.advengers.mabo.Activity.DashboardActivity;
import com.advengers.mabo.Activity.LoginActivity;
import com.advengers.mabo.Adapter.InterestAdapter;
import com.advengers.mabo.Model.Interest;
import com.advengers.mabo.Model.Person;
import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.ServerCall.MyVolleyRequestManager;
import com.advengers.mabo.ServerCall.ServerParams;
import com.advengers.mabo.Tools.MultiDrawable;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Tools;
import com.advengers.mabo.Utils.Utils;
import com.advengers.mabo.databinding.FragmentPeopleBinding;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;
import com.stfalcon.multiimageview.MultiImageView;

import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.advengers.mabo.Activity.MainActivity.GETNEARBY;
import static com.advengers.mabo.Activity.MainActivity.LOADAVATAR;
import static com.advengers.mabo.Activity.MainActivity.LOGOUT;
import static com.advengers.mabo.Activity.MainActivity.SERVER_URL;
import static com.advengers.mabo.Interfaces.Keys.ID;
import static com.advengers.mabo.Interfaces.Keys.INTEREST;
import static com.advengers.mabo.Interfaces.Keys.RANGE;
import static com.advengers.mabo.Interfaces.Keys.STATUS_JSON;
import static com.advengers.mabo.Interfaces.Keys.USER;

public class PeoplesFragment extends MyFragment implements View.OnClickListener,OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<Person>,
        ClusterManager.OnClusterInfoWindowClickListener<Person>,
        ClusterManager.OnClusterItemClickListener<Person>, ClusterManager.OnClusterItemInfoWindowClickListener<Person>{

    FragmentPeopleBinding binding;
    GoogleMap mGoogleMaps;
    ArrayList<User> UserList = new ArrayList<>();
    private ClusterManager<Person> mClusterManager;
    private Random mRandom = new Random(1984);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_people, container, false);
        setHasOptionsMenu(true);
        binding.mToolbar.inflateMenu(R.menu.menu_peoplefragment);
        binding.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_findme:
                        LogUtils.e(getString(R.string.action_findme));
                        mGoogleMaps.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongitude()))));
                        mGoogleMaps.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongitude()))), 10000, null);
                        break;
                    case R.id.action_findpartner:
                        break;
                    case R.id.action_findfriends:
                        break;
                    case R.id.action_setrange:
                        ShowRangetAlert();
                        break;
                }
                return true;
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);

        return binding.getRoot();
    }
    private GoogleMap getMap() {
        return mGoogleMaps;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
       inflater.inflate(R.menu.menu_peoplefragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
      }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_findme).setVisible(true);
        super.onPrepareOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_findme:
                LogUtils.e(getString(R.string.action_findme));
                getMap().moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongitude()))));
                getMap().animateCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongitude()))), 10000, null);
                getMap().addMarker(new MarkerOptions()
                        .position(new LatLng(Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongitude())))
                        .title(user.getUsername()
                        ));
                break;
            case R.id.action_findpartner:
                break;
            case R.id.action_findfriends:
                break;
            case R.id.action_findnearme:
                getNearby();
                break;
            case R.id.action_setrange:
              ShowRangetAlert();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    void ShowRangetAlert()
    {
         final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.alert_range, null);
            dialogBuilder.setView(dialogView);
            final TextView txtseelvalue = (TextView)dialogView.findViewById(R.id.seekvalue);
            SeekBar seekBar=(SeekBar)dialogView.findViewById(R.id.seekrange);
            seekBar.setProgress(Utils.getInstance(getActivity()).getInt(RANGE));

            txtseelvalue.setText("Range "+Utils.getInstance(getActivity()).getInt(RANGE));

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
            //    Toast.makeText(getApplicationContext(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();
                int minvalue = 25;
                if(progress>minvalue)
                    minvalue = progress;
                Utils.getInstance(getActivity()).setInt(RANGE,minvalue);
                txtseelvalue.setText("Range "+minvalue);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
             //   Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               // Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
            }
        });

        Button btn_ok = (Button)dialogView.findViewById(R.id.btn_ok);

            final AlertDialog b = dialogBuilder.create();
            b.setCancelable(false);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getNearby();
                   b.dismiss();

                }
            });
            if(!b.isShowing())
            {
                b.show();
            }

    }
    private void getNearby() {
        String URL = SERVER_URL+GETNEARBY;
        URL = URL.replaceAll(" ", "%20");
        App.requestQueue.add(MyVolleyRequestManager.createStringRequest(Request.Method.POST,
                URL,new ServerParams().getNearby(user.getId(),user.getLatitude(),user.getLongitude(),Utils.getInstance(getActivity()).getInt(RANGE))
                , lister,error_listener));
    }
    Response.Listener lister = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            onLoadDismiss();

            LogUtils.e(response.toString());

            try {
                JSONObject login = new JSONObject(response.toString());

                if (login.has(STATUS_JSON)) {


                    if (login.getString(STATUS_JSON).equals("true")) {

                        JSONArray userarr = login.getJSONArray("data");
                        for(int i=0; i<userarr.length(); i++)
                        {
                            String p = userarr.getString(i);
                        //    LogUtils.e(i+" "+p);
                         //   User persons = new User();
                           User persons = gson.fromJson(p,User.class);

                          // LogUtils.e(persons.getUsername()+" "+persons.getLatitude()+" "+persons.getLongitude()+" "+persons.getprofile_imagename());
                           if(persons.getprofile_imagename().isEmpty())
                               persons.setprofile_imagename(LOADAVATAR);
                            if(Tools.findUser(persons.getId(), UserList)==null)
                            {
                                UserList.add(persons);
                                mClusterManager.addItem(new Person(new LatLng(Double.parseDouble(persons.getLatitude()),Double.parseDouble(persons.getLongitude())), persons.getUsername(),persons.getId(),persons.getprofile_imagename()));
                            }


                       }
                        mClusterManager.cluster();
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };



    Response.ErrorListener error_listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            onLoadDismiss();
            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    // Now you can use any deserializer to make sense of data
                    LogUtils.e(res);
                    JSONObject obj = new JSONObject(res);
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    e2.printStackTrace();
                }
            }
        }
    };
    public void setUser()
    {
        gson = new Gson();
        user = new User();
        User.setUser(gson.fromJson(Utils.getInstance(getActivity()).getString(USER),User.class));
        user = User.getUser();
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    public void onClick(View view) {

    }

    @Override
    public String getTagManager() {
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMaps = googleMap;
        mGoogleMaps.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mGoogleMaps.clear(); //clear old markers
        setUser();
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongitude())))
                .zoom(15)
                .bearing(0)
                .tilt(0)
                .build();
        mGoogleMaps.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongitude()))));
        mGoogleMaps.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 100, null);
//        mGoogleMaps.setMyLocationEnabled(true);
        mGoogleMaps.getUiSettings().setMyLocationButtonEnabled(true);
       /* mGoogleMaps.addMarker(new MarkerOptions().position(
                new LatLng(Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongitude())))
                .title(user.getUsername()));*/
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongitude()), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getSubLocality();//.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            LogUtils.e(addresses.toString());
            binding.txtAddress.setText(city);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startDemo();

    }

    @Override
    public boolean onClusterClick(Cluster<Person> cluster) {
       /* String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();*/

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Person> cluster) {

    }

    @Override
    public void onClusterItemInfoWindowClick(Person person) {
        //   Utils.showToast(person.getId(),getContext());

        Tools.showUserProfile(R.style.Animation_Design_BottomSheetDialog, user.getId(),person.id, getContext(), getActivity());
    }
    @Override
    public boolean onClusterItemClick(Person person) {
        return false;
    }

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
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
                    LogUtils.e(person.profilePhoto);
                     Picasso.get().load(person.profilePhoto).error(R.drawable.ic_avatar).into(mImageView);
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

    protected void startDemo() {

        mClusterManager = new ClusterManager<Person>(getActivity(), getMap());
        mClusterManager.setRenderer(new PersonRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mClusterManager.cluster();
        getNearby();
    }

    private void addItems() {
     //   for(int i=0;i<5;i++)
         mClusterManager.addItem(new Person(new LatLng(Double.parseDouble(user.getLatitude()),Double.parseDouble(user.getLongitude())), user.getUsername(),user.getId(),user.getprofile_imagename()));
    }
    private LatLng position() {
        return new LatLng(random(Double.parseDouble(user.getLatitude()), Double.parseDouble(user.getLatitude())+2),random(Double.parseDouble(user.getLongitude()), Double.parseDouble(user.getLongitude())-2));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}
