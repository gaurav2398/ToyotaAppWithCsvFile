package impel.imhealthy.adminapp.QrCodeScanner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.nio.file.Path;
import java.nio.file.Paths;


import impel.imhealthy.adminapp.HomeScreen;
import impel.imhealthy.adminapp.R;
import impel.imhealthy.adminapp.TestScreenActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.CAMERA;

public class ScanActivity extends AppCompatActivity implements WiFiConnecter.ActionListener {
    private CodeScanner mCodeScanner;
    String codes;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private WifiManager mWifiManager;
    private WiFiConnecter mWiFiConnecter;

    private ProgressDialog mDialog;
    ProgressBar progressBar;
    TextView myTextProgress;


    private TextView tv_CurrentSsid;
    private EditText et_Ssid;
    private EditText et_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

/*
        WifiManager wManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wManager.setWifiEnabled(true); //true or false

*/
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Do whatever
        }
        else {
            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
        }


        progressBar = findViewById(R.id.progressBar);
        myTextProgress = findViewById(R.id.myTextProgress);
        progressBar.setVisibility(View.GONE);
        myTextProgress.setVisibility(View.GONE);


        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("NewApi")
                    @Override
                    public void run() {
                        codes = result.getText();
                        Log.d("qrcode", codes);


                        String result[] = codes.split(":");

                        String returnValue = result[result.length - 1]; //eq
                        Log.d("returnvalue", returnValue);

                        Path path = Paths.get(codes);
                        System.out.println(path.getFileName().toString());
            //            Toast.makeText(ScanActivity.this, codes, Toast.LENGTH_LONG).show();
                        //connect();
/*
                        Intent i= new Intent(ScanActivity.this, HomeScreen.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("code", codes);
                        startActivity(i);
                        ScanActivity.this.finish();
                        finish();
*/
                        String s = codes;
                        System.out.println("subString1=" + codes.substring(0, s.indexOf(":")));
                        System.out.println("subString2=" + s.substring(s.indexOf(":") + 1, s.length()));
                        System.out.println("subString3=" + s.substring(s.indexOf(":") + 2, s.length()));
                        System.out.println("subString4=" + s.substring(s.indexOf(";") + 3, s.length()));
                        System.out.println("subString5=" + s.substring(s.indexOf(":") + 4, s.length()));
                        System.out.println("subString6=" + s.substring(s.indexOf(";") + 5, s.length()));
                        //              System.out.println("subString7="+ s.substring(s.indexOf(";") + 6, s.length()));

                        String spl = codes;

                        String sub = spl.substring(7, codes.length());
                        Log.d("sub", sub);


                        String[] namesList = sub.split(";");
                        String name1 = namesList[0];
                        Log.d("sub2", name1);

                        String passw = s.substring(s.indexOf(";") + 6, s.length());
                        String[] namesList1 = passw.split(":");
                        String name11 = namesList1[0];
                        String name21 = namesList1[1];
                        Log.d("sub3", name21);

                        String[] namesList11 = name21.split(";");
                        String name111 = namesList11[0];
                        Log.d("sub4", name111);


                        et_Ssid.setText(name1);
                        et_Password.setText(name111);
                        Log.d("username:", name1);
                        Log.d("password:", name111);
                        //      Toast.makeText(ScanActivity.this, "Username:" + name1 + "\nPassword:" + name111, Toast.LENGTH_LONG).show();

                        connect();



/*
                        String str=sub;
                        StringBuilder revStr= new StringBuilder();
                        char[] str1= str.toCharArray();
                        for (char c : str1) {
                            revStr.insert(0, c);
                        }
                        System.out.println(revStr);

                        String sub2 = sub.substring(1, sub.length());
                        Log.d("sub2",sub2);
                        String names = sub2;
                        String[] namesList = names.split(":");
                        String name1 = namesList [0];
                        String name2 = namesList [1];

                        Log.d("sub3",name);
                        String remainder = spl.substring(10);
*/

                        connect();

                    }
                });

            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        requestPermission();

//        init();

    }

    private void init() {
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWiFiConnecter = new WiFiConnecter(this);


        setCurrentSsid();
    }

    private void setCurrentSsid() {
        WifiInfo info = mWifiManager.getConnectionInfo();
        String s = (info == null) ? "null" : info.getSSID();
        tv_CurrentSsid.setText(String.format(getString(R.string.current_ssid), s));
    }

    public void connect() {
        final String ssid = et_Ssid.getText().toString();
        final String password = et_Password.getText().toString();
/*
        String ssid = "JioFi4_12B38D";
        String password = "xa58aoetr1";*/

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + ssid + "\"";
        wifiConfiguration.preSharedKey = "\"" + password + "\"";

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int networkId = wifiManager.addNetwork(wifiConfiguration);
        if (networkId != -1) {
            wifiManager.enableNetwork(networkId, true);
            // Use this to permanently save this network
            // Otherwise, it will disappear after a reboot
            wifiManager.saveConfiguration();
        }
        progressBar.setVisibility(View.VISIBLE);
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Toast.makeText(ScanActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ScanActivity.this, TestScreenActivity.class));
                finish();
            }
        }, secondsDelayed * 5500);
     /*   final String ssid = et_Ssid;
        final String password = et_Password;
        mWiFiConnecter.connect(ssid, password, ScanActivity.this);*/
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ScanActivity.this, HomeScreen.class);
        startActivity(intent);
        finish();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ScanActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
        ;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {CAMERA}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{CAMERA},
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onStarted(String ssid) {
        System.out.println("------onStarted------");
        Toast.makeText(ScanActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSuccess(WifiInfo info) {
        System.out.println("------onSuccess------");
        Toast.makeText(ScanActivity.this, "Successfully Connected", Toast.LENGTH_SHORT).show();
        setCurrentSsid();
/*
        Intent i = new Intent(ScanActivity.this, HomeScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("code", codes);
        startActivity(i);
        ScanActivity.this.finish();
        finish();
*/

    }

    @Override
    public void onFailure() {
        System.out.println("------onFailure------");
        Toast.makeText(ScanActivity.this, "Fail To Connect", Toast.LENGTH_SHORT).show();
/*
        Intent i = new Intent(ScanActivity.this, HomeScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("code", codes);
        startActivity(i);
        ScanActivity.this.finish();
        finish();
*/

    }

    @Override
    public void onFinished(boolean isSuccessed) {
        System.out.println("------onFinished------");
//        mDialog.dismiss();
//        Toast.makeText(ScanActivity.this, "onFinished", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(ScanActivity.this, TestScreenActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("code", codes);
        startActivity(i);
        ScanActivity.this.finish();
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
