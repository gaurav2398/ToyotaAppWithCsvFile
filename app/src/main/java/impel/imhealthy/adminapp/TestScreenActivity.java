package impel.imhealthy.adminapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import impel.imhealthy.adminapp.Adapter.MemberAdapterNew;
import impel.imhealthy.adminapp.HomeActivity.ProfileActivity;
import impel.imhealthy.adminapp.HomeActivity.SettingActivity;
import impel.imhealthy.adminapp.Model.MemberModel;
import impel.imhealthy.adminapp.QrCodeScanner.ScanActivity;
import impel.imhealthy.adminapp.Timelines.TimeLineActivity;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class TestScreenActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    // AsyncTask object that manages the connection in a separate thread

    Button btnready, btn_manualtest;
    String ssid_name = "", ipAddress;
    Switch wifi, location;
    int flag = 0;
    TextView tvwifi_name, textStatus;
    ImageView imgcheck, back;
    String str;
    LinearLayout home, history, profile, setting;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;
    EditText edtcelcius, edtfarh, edtspo, edtbpm,visitor_name,visitor_mobile;
    private TextView txtLatLong, txtAddress;
    private ResultReceiver resultReceiver;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    String userid;
    SessionManager session;
    RecyclerView mRecyclerView1;
    MemberAdapterNew adapter;
    ArrayList<MemberModel> dm = new ArrayList<MemberModel>();
    int ip;
    ImageView imgwifi;
    String identity,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_screen);

        session = new SessionManager(TestScreenActivity.this);
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        imgwifi=findViewById(R.id.imgwifi);
        imgwifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestScreenActivity.this, ScanActivity.class));
            }
        });


        try {
            Intent i = getIntent();
            identity = i.getStringExtra("identity");
            name = i.getStringExtra("name");
        }
        catch (Exception e){}

        resultReceiver = new AddressResultReceiver(new Handler());

        mRecyclerView1 = (RecyclerView) findViewById(R.id.memberrecycler);
        mRecyclerView1.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(TestScreenActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView1.setLayoutManager(mLayoutManager1);
        mRecyclerView1.setItemAnimator(new DefaultItemAnimator());
        adapter = new MemberAdapterNew(TestScreenActivity.this, dm);
        mRecyclerView1.setAdapter(adapter);

//        memlist();

        visitor_mobile = findViewById(R.id.visitor_mobile);
        visitor_name = findViewById(R.id.visitor_name);
        btn_manualtest = findViewById(R.id.btn_manualtest);

        txtLatLong = findViewById(R.id.txtLatLong);
        txtAddress = findViewById(R.id.txtAddress);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TestScreenActivity.this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }

        btn_manualtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtAddress.getText().toString().isEmpty()) {
                    Toast.makeText(TestScreenActivity.this, "Please Enable Location", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (visitor_name.getText().toString().isEmpty()){
                        visitor_name.setText("-");
                    }
                    if (visitor_mobile.getText().toString().isEmpty()){
                        visitor_mobile.setText("-");
                    }
                    Intent i = new Intent(TestScreenActivity.this, TestResultActivity2.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("address", txtAddress.getText().toString());
                    i.putExtra("visitorname", visitor_name.getText().toString());
                    i.putExtra("visitormobile", visitor_mobile.getText().toString());
                    startActivity(i);

                }

/*
                if (txtAddress.getText().toString().isEmpty()) {
                    Toast.makeText(TestScreenActivity.this, "Please Enable Location", Toast.LENGTH_SHORT).show();

                }else if (visitor_name.getText().toString().isEmpty()){
                    Toast.makeText(TestScreenActivity.this, "Enter Visitor Name", Toast.LENGTH_SHORT).show();
                }else if (visitor_mobile.getText().toString().isEmpty()){
                    Toast.makeText(TestScreenActivity.this, "Enter Visitor Mobile Number", Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = new Intent(TestScreenActivity.this, TestResultActivity2.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("address", txtAddress.getText().toString());
                    i.putExtra("visitorname", visitor_name.getText().toString());
                    i.putExtra("visitormobile", visitor_mobile.getText().toString());
                    startActivity(i);
                }
*/
            }
        });


        back = findViewById(R.id.back);
        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        profile = findViewById(R.id.profile);

        setting = findViewById(R.id.setting);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestScreenActivity.this, HomeScreen.class));
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent =  new Intent(HomeScreen.this, HomeScreen.class);
                startActivity(intent);*/
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestScreenActivity.this, TimeLineActivity.class));
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestScreenActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestScreenActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }

        textStatus = findViewById(R.id.textStatus);
        tvwifi_name = findViewById(R.id.tvwifi_name);
        imgcheck = findViewById(R.id.imgcheck);
        wifi = findViewById(R.id.wifi);
        location = findViewById(R.id.location);


        LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        if (LocationManagerCompat.isLocationEnabled(locationManager)) {
            location.setChecked(true);

        } else {
            location.setChecked(false);

        }
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
                builder.setAlwaysShow(true);
                mLocationSettingsRequest = builder.build();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TestScreenActivity.this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    getCurrentLocation();
                }
                mSettingsClient = LocationServices.getSettingsClient(TestScreenActivity.this);

                mSettingsClient
                        .checkLocationSettings(mLocationSettingsRequest)
                        .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                            @Override
                            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                //Success Perform Task Here
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                int statusCode = ((ApiException) e).getStatusCode();
                                switch (statusCode) {
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        try {
                                            ResolvableApiException rae = (ResolvableApiException) e;
                                            rae.startResolutionForResult(TestScreenActivity.this, REQUEST_CHECK_SETTINGS);
                                        } catch (IntentSender.SendIntentException sie) {
                                            Log.e("GPS", "Unable to execute request.");
                                        }
                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        Log.e("GPS", "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                                }
                            }
                        })
                        .addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Log.e("GPS", "checkLocationSettings -> onCanceled");
                            }
                        });
            }
        });
        WifiManager wifiManager1 = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager1.isWifiEnabled()) {
            wifi.setChecked(true);
            // wifi is enabled
        } else {
            wifi.setChecked(false);
        }
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            ssid_name = info.getExtraInfo();
            Log.d("TAG", "WiFi SSID: " + ssid_name);
        }

        if (ssid_name == "") {
            tvwifi_name.setVisibility(View.GONE);
            imgcheck.setVisibility(View.GONE);
        } else {
            tvwifi_name.setVisibility(View.VISIBLE);
            imgcheck.setVisibility(View.VISIBLE);
            tvwifi_name.setText(ssid_name);
        }

        WifiInfo wifiInfo = wifiManager1.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        ipAddress = Formatter.formatIpAddress(ip);
        //  Toast.makeText(TestScreenActivity.this, ""+ipAddress, Toast.LENGTH_LONG).show();

        btnready = findViewById(R.id.connect);
        btnready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TestScreenActivity.this, TestResultActivity.class);
                i.putExtra("ipaddress", ipAddress);
                i.putExtra("name", name);
                i.putExtra("identity", identity);
                startActivity(i);
                finish();
//                WifiInfo wifiInfo = wifiManager1.getConnectionInfo();
//                int ip = wifiInfo.getIpAddress();
//                String ipAddress = Formatter.formatIpAddress(ip);
//
//                if (ipAddress.equals("0.0.0.0")) {
//                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
//
//                } else {
//                    if (location.isChecked() == true) {
//                        //        Toast.makeText(TestScreenActivity.this, "Please Connect to Wifi !! \n", Toast.LENGTH_LONG).show();
////                        startActivity(new Intent(TestScreenActivity.this, TestResultActivity.class));
//
//                        Intent i = new Intent(TestScreenActivity.this, TestResultActivity.class);
//                        i.putExtra("ipaddress", ipAddress);
//                        i.putExtra("name", name);
//                        i.putExtra("identity", identity);
//                        startActivity(i);
//                finish();
//                    } else {
//                        Toast.makeText(TestScreenActivity.this, "Please Enabled Location !! \n", Toast.LENGTH_LONG).show();
//                    }
//                }
            }
        });

        Log.d("ip address", ipAddress);
        final WifiManager[] wifiManager = new WifiManager[1];
        wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiManager[0] = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    @SuppressLint("MissingPermission") boolean s = wifiManager[0].setWifiEnabled(true);
                    if (wifiManager[0].isWifiEnabled()) {

                        if (flag == 0) {
                            wifi.setChecked(true);
                            flag = 1;
                        }
                    }
                } else {
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    boolean s = wifiManager.setWifiEnabled(false);
                    if (s) {
                        if (flag == 1) {
                            wifi.setChecked(false);
                            flag = 0;
                        }
                    }// The toggle is disabled
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {


        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.getFusedLocationProviderClient(TestScreenActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(TestScreenActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            txtLatLong.setText(
                                    String.format(
                                            "Latitude: %s\n Longitude: %s",
                                            latitude,
                                            longitude
                                    )
                            );
                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchAdderessfromLocation(location);
                        } else {
                            // progressBar.setVisibility(View.GONE);
                        }

                    }
                }, Looper.getMainLooper());
    }

    private void fetchAdderessfromLocation(Location location) {
        Intent intent = new Intent(this, fetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Constants.SUCCESS_RESULT) {
                txtAddress.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            } else {
                Toast.makeText(TestScreenActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            ssid_name = info.getExtraInfo();
            Log.d("TAG", "WiFi SSID: " + ssid_name);
        }

        if (ssid_name == "") {
            tvwifi_name.setVisibility(View.GONE);
            imgcheck.setVisibility(View.GONE);
        } else {
            tvwifi_name.setVisibility(View.VISIBLE);
            imgcheck.setVisibility(View.VISIBLE);
            tvwifi_name.setText(ssid_name);
        }

        super.onResume();
    }

    void setStatus(String s) {
        Log.v(TAG, s);
        textStatus.setText(s);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:

                    break;
                case Activity.RESULT_CANCELED:
                    location.setChecked(false);
                    Log.e("GPS", "User denied to access location");

                    break;
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {

        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TestScreenActivity.this, HomeScreen.class));
        finish();
    }


    private void memlist() {
        final ProgressDialog showMe = new ProgressDialog(TestScreenActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.get_members,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        showMe.dismiss();

                        Log.d("server response : ", ServerResponse);

                        try {
                            JSONObject j = new JSONObject(ServerResponse);

                            String status = j.getString("status");
                            String msg = j.getString("msg");

                            if (status.equals("200")) {
                                dm.clear();
                                JSONArray applist1 = j.getJSONArray("data");

                                for (int i = 0; i < applist1.length(); i++) {

                                    MemberModel model = new MemberModel();

                                    JSONObject jsonObject = applist1.getJSONObject(i);

                                    model.setMembername(jsonObject.getString("first_name"));
                                    model.setMemberpic(jsonObject.getString("profile_image"));
                                    model.setId(jsonObject.getString("id"));

                                    dm.add(model);
                                    adapter = new MemberAdapterNew(TestScreenActivity.this, dm);      //ds=model       d=Model
                                    mRecyclerView1.setAdapter(adapter);
                                }


                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(TestScreenActivity.this, msg, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            showMe.dismiss();
                            Toast.makeText(TestScreenActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                            // Is thrown if there's no network connection or server is down
                            // We return to the last fragment
                            Log.d("eerror", String.valueOf(volleyError));
                            NetworkDialog();
                            if (getFragmentManager().getBackStackEntryCount() != 0) {
                                getFragmentManager().popBackStack();
                            }

                        } else {
                            // Is thrown if there's no network connection or server is down
                            Log.d("eerror", String.valueOf(volleyError));
                            NetworkDialog();
                            // We return to the last fragment
                            if (getFragmentManager().getBackStackEntryCount() != 0) {
                                getFragmentManager().popBackStack();
                            }
                        }
                        showMe.dismiss();
                        // Hiding the progress dialog after all task complete.
                        // Showing error message if something goes wrong.
                        // Toast.makeText(StampOfferActivity.this, "No Connection", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();

                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("api_key", "salkdha645456q21362dbasdbmsd");
                params.put("user_id", userid);

                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(TestScreenActivity.this);
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void NetworkDialog() {
        final Dialog dialogs = new Dialog(TestScreenActivity.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();

                memlist();
            }
        });
        dialogs.show();
    }
}
