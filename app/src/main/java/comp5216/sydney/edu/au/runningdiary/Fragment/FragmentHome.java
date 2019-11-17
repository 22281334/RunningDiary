package comp5216.sydney.edu.au.runningdiary.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import comp5216.sydney.edu.au.runningdiary.R;
import comp5216.sydney.edu.au.runningdiary.Support.SDUtils;


public class FragmentHome extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    LocationRequest mLocationRequest;
    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;
    private LocationManager locationManager;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 1;

    private Boolean myLocationPermissionsGranted = false;
    private GoogleMap myMap;
    private FusedLocationProviderClient myFusedLocationProviderClient;

    private boolean isStart = false;
    LocationCallback locationCallback;
    Location currentLocation;


    Button startBtn;
    TextView displayInfo;
    //widgets
    private EditText mySearchText;
    private ImageView mGps;

    float totalDistance = 0;
    float speed = 0;
    float time = 0;
    float pace = 0;

    LatLng currentLatLng;
    List<LatLng> latLngList = new ArrayList<>();
    MapView mapView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_index, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.map);
        mySearchText = view.findViewById(R.id.input_search);
        mGps = view.findViewById(R.id.ic_gps);
        startBtn = view.findViewById(R.id.startBtn);
        displayInfo = view.findViewById(R.id.displayInfo);

        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mapView.onCreate(savedInstanceState);
        mapView.onResume();


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                myMap = googleMap;
                if (myLocationPermissionsGranted) {
                    getDeviceLocation();
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    myMap.setMyLocationEnabled(true);
                    myMap.getUiSettings().setMyLocationButtonEnabled(false);
                    init();

                }
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLocationPermission();
        init();
        setStartBtnListener();
    }


    private void getLocationPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                myLocationPermissionsGranted = true;
                //initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permission,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permission,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    /**
     * get distance between two location
     *
     */
    private float getDistance(LatLng latLng1, LatLng latLng2) {
        Location location1 = new Location("A");
        location1.setLatitude(latLng1.latitude);
        location1.setLongitude(latLng1.longitude);

        Location location2 = new Location("B");
        location2.setLatitude(latLng2.latitude);
        location2.setLongitude(latLng2.longitude);

        return location1.distanceTo(location2);
    }

    /**
     * set start button listener
     *
     */
    private void setStartBtnListener() {
        startBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (!isStart) {  // when start
                    startBtn.setBackgroundColor(Color.parseColor("#FF6152"));
                    startBtn.setText("Stop");
                    isStart = true;

                    getDeviceLocation();
                    onLocationChanged(currentLocation);

                    String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION};
                    if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                            FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                                COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            myLocationPermissionsGranted = true;
                            //initMap();
                        } else {
                            ActivityCompat.requestPermissions(getActivity(),
                                    permission,
                                    LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    } else {
                        ActivityCompat.requestPermissions(getActivity(),
                                permission,
                                LOCATION_PERMISSION_REQUEST_CODE);
                    }
                    // set location changed listener
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    time += 0.5;
                                    Double curLat = location.getLatitude();//current latitude
                                    Double curLong = location.getLongitude();//current longitude
                                    LatLng latLng = new LatLng(curLat, curLong);
                                    if (isStart) {
                                        latLngList.add(latLng);

                                        //draw ploy line
                                        if (latLngList.size() < 2) {
                                            Polyline mVirtureRoad = (Polyline) myMap.addPolyline(new PolylineOptions()
                                                    .add(latLngList.get(0), latLngList.get(0))
                                                    .width(5)
                                                    .color(Color.RED));
                                        } else {
                                            float getTotalDistanceAB = getDistance(latLngList.get(latLngList.size() - 1), latLngList.get(latLngList.size() - 2));
                                            totalDistance += Math.abs(getTotalDistanceAB);
                                            speed = totalDistance / time;
                                            pace = time / totalDistance;
                                            displayInfo.setText(
                                                    "Distance: " + totalDistance + "m" + "\n" +
                                                    "Speed: " + speed + "m/s" + "\n" +
                                                    "Pace: " + pace + "s/m");
                                            // draw poly line in map
                                            Polyline mVirtureRoad = (Polyline) myMap.addPolyline(new PolylineOptions()
                                                    .add(latLngList.get(latLngList.size() - 1), latLngList.get(latLngList.size() - 2))
                                                    .width(5)
                                                    .color(Color.RED));
                                        }
                                    }
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
                            }
                    );
                } else {
                    // change background color
                    startBtn.setBackgroundColor(Color.parseColor("#7CFC00"));
                    startBtn.setText("Start");
                    isStart = false;
                    displayInfo.setText("");
                    //save data
                    saveData();
                    totalDistance = 0;
                    //clear information
                    myMap.clear();
                    latLngList.clear();
                    latLngList.removeAll(latLngList);
                }

            }
        });
    }

    /**
     * Save new running data to local storage
     *
     */
    private void saveData() {

        String fileNmae = "runningData.txt";
        String currentData = "";
        String currentTime = getTime();
        currentData = currentTime + "," + totalDistance + "," + time + "," + speed + "," + pace;

        String loadData = loadData() + "%" + currentData;

        boolean isSaved = SDUtils.saveFileToExternalCacheDir(getContext(), loadData.getBytes(), fileNmae);
        if (isSaved) {
            Toast.makeText(getActivity(), "Running Data Saved", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Locd data from database
     *
     */
    private String loadData() {
        byte[] bytes = SDUtils.loadDataFromSDCard("/storage/emulated/0/Android/data/comp5216.sydney.edu.au.runningdiary/cache/runningData.txt");
        String data = new String(bytes);
        return data;
    }

    /**
     * Create location request
     *
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    /**
     * Get the current time
     * Return a String current time
     */
    public String getTime() {
        // get current time
        SimpleDateFormat forMatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String toAddString = forMatter.format(date);
        return toAddString;
    }

    /**
     * Init the map
     *
     */
    private void init() {
        mySearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }


        });
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
    }

    private void geoLocate() {
        String searchString = mySearchText.getText().toString();

        Locale locale=new Locale.Builder().setLanguage("en").setRegion("AU").build();
        Geocoder geocoder = new Geocoder(getActivity(),locale);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);

        } catch (IOException e) {
            Log.e("ERROR", "geo Locate:IOException" + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d("TAG", "geoLocation: found" + address.toString());
            Toast.makeText(getActivity(), address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        } else {
            Toast.makeText(getActivity(), "Address did not found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        myLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            myLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    myLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }

    }

    /**
     * init the map
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                myMap = googleMap;
            }
        });
    }

    /**
     * get my devices location
     */
    private void getDeviceLocation() {
        myFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        final LatLng[] latLng = new LatLng[1];
        try {
            if (myLocationPermissionsGranted) {
                final Task location = myFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            currentLocation = (Location) task.getResult();
                            latLng[0] = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            moveCamera(latLng[0], DEFAULT_ZOOM,
                                    "My Location");
                            currentLatLng = latLng[0];
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("ERROR", "SecurityException" + e.getMessage());
        }
    }

    /**
     * move camera to a location
     */
    private void moveCamera(LatLng latLng, float zoom, String title) {
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            myMap.addMarker(options);
        }
    }


    /**
     * check google map works.
     */
    public boolean checkGoogle() {
        int abli = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(getContext());
        if (abli == ConnectionResult.SUCCESS) {
            System.out.println("Google play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance()
                .isUserResolvableError(abli)) {
            System.out.println("error but can fix it");
            return true;
        } else {
            return true;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
