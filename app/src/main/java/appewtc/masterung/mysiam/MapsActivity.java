package appewtc.masterung.mysiam;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.os.ParcelableCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //การประกาศตัวแปรของแผนที่
    private GoogleMap mMap; //ประกาศแผนที่
    private LocationManager locationManager; //ทำหน้าที่ในการเปิดเซอวิสในการเปิดพิกัด
    private Criteria criteria; //การบอกรายละเอียดในการบอกพิกัด
    private double latADouble = 13.807208, lngADouble = 100.538210;   //ตัวแปรนี่มีเศษได้\
    private LatLng userLatLng;
    private int[] mkInts = new int[]{R.mipmap.mk_user, R.mipmap.mk_friend};
    private String[] userStrings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Sutup
        sutup();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        createFragment();


    } // Main Method

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.removeUpdates(locationListener);

        //For Network
        Location networdLocation = myFindLocation(LocationManager.NETWORK_PROVIDER);
        if (networdLocation != null) {
            latADouble = networdLocation.getLatitude();
            lngADouble = networdLocation.getLongitude();

        }

        // For GPS
        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation !=null) {
            latADouble = gpsLocation.getLatitude();
            lngADouble = gpsLocation.getLongitude();
        }

        Log.d("BangSun", "Lat ==>" + latADouble);
        Log.d("BangSun", "Lng ==>" + lngADouble);

        CheckAndEditLocation();

    }

    private void CheckAndEditLocation() {

        MyConstant myConstant = new MyConstant();
        String tag = "SiamV3";
        boolean b = true;

        try {

            //Check
            GetAllData getAllData = new GetAllData(MapsActivity.this);
            getAllData.execute(myConstant.getUrlGetAllLocation());
            String strJSON = getAllData.get();
            Log.d(tag, "JSON ==> " + strJSON);


            JSONArray jsonArray = new JSONArray(strJSON);
            for (int i=0; i<jsonArray.length();i+=1 ) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (userStrings[1].equals(jsonObject.getString("Name"))) {
                    b = false;
                }
            }

            if (b) {
                //No Name
                Log.d(tag, "No Name");
            } else {
                //Have Name
                Log.d(tag, "Have Name");
            }


        } catch (Exception e) {
            Log.d(tag, "e check ==> " + e.toString());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    public Location myFindLocation(String strProvider) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
        }

        return location;
    }

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) { //มือถือขยับตำแหน่งทำงานเลย
            latADouble = location.getLatitude();
            lngADouble = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { //ทำงานหาพิกัดได้หรือไม่ได้กรณีเน็ตเวอคใช้งานไม่ได้สั่งให้ทำไร

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void sutup() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        userStrings = getIntent().getStringArrayExtra("Login");


    }

    private void createFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Setup Center Map
        userLatLng = new LatLng(latADouble, lngADouble);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));

        myCreatMarker(userStrings[1], userLatLng, mkInts[0]);


    } //onMapReady

    private void myCreatMarker(String strName, LatLng latLng, int intImage) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(strName));


    }
} // Main Class
