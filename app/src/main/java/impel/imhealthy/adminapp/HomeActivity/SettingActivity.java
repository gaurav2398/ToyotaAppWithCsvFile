package impel.imhealthy.adminapp.HomeActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.location.LocationManagerCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import impel.imhealthy.adminapp.FAQActivity;
import impel.imhealthy.adminapp.HomeScreen;
import impel.imhealthy.adminapp.PrivacyPolicyActivity;
import impel.imhealthy.adminapp.R;
import impel.imhealthy.adminapp.TermsOfUseActivity;
import impel.imhealthy.adminapp.Timelines.TimeLineActivity;

public class SettingActivity extends AppCompatActivity {

    LinearLayout home, history, profile, setting;
    LinearLayout llprivacypolicy,llshareapp, lltou, llfaq, llsupport;
    Switch wifi, location;
    private static final int PERMISSION_REQUEST_CODE_WIFI = 200;
    ImageView back;

    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent a = new Intent(SettingActivity.this, HomeScreen.class);
                startActivity(a);
                finish();
            }
        });
        llprivacypolicy = findViewById(R.id.llprivacypolicy);
        llprivacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent a = new Intent(SettingActivity.this, PrivacyPolicyActivity.class);
                startActivity(a);
            }
        });
        llshareapp = findViewById(R.id.llshareapp);
        llshareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Im Healthy");
                    String shareMessage= "\nI am using I'm Healthy Platform.\n\n https://play.google.com/store/apps/details?id="+getPackageName()+"\n\n";
                    //     shareMessage = shareMessage + "https://zene.page.link/Zi7X" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });
        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        profile = findViewById(R.id.profile);
        setting = findViewById(R.id.setting);

        wifi = findViewById(R.id.wifi);
        location = findViewById(R.id.location);

        WifiManager wifiManager1 = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager1.isWifiEnabled()) {
            wifi.setChecked(true);
            // wifi is enabled
        }
        else {
            wifi.setChecked(false);
        }

        final WifiManager[] wifiManager = new WifiManager[1];
        wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiManager[0] = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    boolean s = wifiManager[0].setWifiEnabled(true);
                    if (wifiManager[0].isWifiEnabled()) {

                        if (flag == 0){
                            wifi.setChecked(true);
                            flag=1;
                        }
                    }
                } else {
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    boolean s = wifiManager.setWifiEnabled(false);
                    if (s) {
                        if (flag==1){
                            wifi.setChecked(false);
                            flag=0;
                        }
                    }// The toggle is disabled
                }
            }
        });
        LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        if (LocationManagerCompat.isLocationEnabled(locationManager))
        {
                location.setChecked(true);
        }
        else {
            location.setChecked(false);
        }
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
                builder.setAlwaysShow(true);
                mLocationSettingsRequest = builder.build();

                mSettingsClient = LocationServices.getSettingsClient(SettingActivity.this);

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
                                            rae.startResolutionForResult(SettingActivity.this, REQUEST_CHECK_SETTINGS);
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
        /*
        location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                } else {

                }
            }
        });*/
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(SettingActivity.this, TimeLineActivity.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent =  new Intent(SettingActivity.this, SettingActivity.class);
                startActivity(intent);*/
            }
        });

        lltou = findViewById(R.id.lltou);
        llfaq = findViewById(R.id.llfaq);
        llsupport = findViewById(R.id.llsupport);
        llfaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(SettingActivity.this, FAQActivity.class);
                startActivity(intent);
            }
        });
        lltou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(SettingActivity.this, TermsOfUseActivity.class);
                startActivity(intent);
            }
        });
        llsupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailto = "mailto:info@impelinfotech.com";

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));

                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    //TODO: Handle case where no email app is available
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent a = new Intent(SettingActivity.this, HomeScreen.class);
        startActivity(a);

        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:

                    //Success Perform Task Here
                    break;
                case Activity.RESULT_CANCELED:
                    location.setChecked(false);
                    Log.e("GPS", "User denied to access location");
                    //   openGpsEnableSetting();
                    break;
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            //     LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //    boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

/*
            if (!isGpsEnabled) {
                openGpsEnableSetting();
            } else {
                //navigateToUser();
            }
*/
        }
    }
}
