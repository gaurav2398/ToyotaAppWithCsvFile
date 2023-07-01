package impel.imhealthy.adminapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import impel.imhealthy.adminapp.Excel.DatabaseHelper;
import impel.imhealthy.adminapp.HomeActivity.ProfileActivity;
import impel.imhealthy.adminapp.HomeActivity.SettingActivity;
import impel.imhealthy.adminapp.Timelines.TimeLineActivity;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class TestResultActivity extends AppCompatActivity {


    private final String TAG = getClass().getSimpleName();

    // AsyncTask object that manages the connection in a separate thread
    //WiFiSocketTask wifiTask;

    // UI elements
    TextView textStatus, textRX, textTX, txtcelcius, txtfahrenheit, txtbpm, txtpulse;
    EditText editTextAddress, editTextPort, editSend;
    Button buttonConnect, buttonSend;
    ImageView back;
    LinearLayout ftr;
    private TextView txtLatLong, txtAddress;
    private ResultReceiver resultReceiver;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    String userid, memberid;
    SessionManager session;


    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    private TCPClient mTcpClient = null;
    //  private connectTask conctTask = null;
    private String ipAddressOfServerDevice;
    int flag = 0;
    LinearLayout home, history, profile, setting;
    // String ipaddress;
    //String string = "";

    Thread Thread1 = null;
    private BufferedReader input;
    private TextToSpeech textToSpeech;
    String ipadd;
    String s44="yes",s46;

    class Thread1 implements Runnable {
        @Override
        public void run() {
            Socket socket;
            try {
                socket = new Socket(ipadd, 8080);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textStatus.setText("Connected\n");
                    }
                });
                new Thread(new Thread2()).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Thread2 implements Runnable {
        @Override
        public void run() {
            String message = "OK";
            while (true) {
                try {
                    while (message != "CLOSED") {
                        message = input.readLine();
                        if (message != null) {
                            final String finalMessage = message;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    s44 = "no";
                                    gotMessage(finalMessage);
                                    //                                   tvMessages.append(finalMessage + "\n");
                                }
                            });
                        }
                    }
                    input.close();
                    input = null;
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    String identity,name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        String ips="";
        try {
            Intent i = getIntent();
             ips = i.getStringExtra("ipaddress");
            identity = i.getStringExtra("identity");
            name = i.getStringExtra("name");
        }catch (Exception e){}

        mydb = new DatabaseHelper(getApplicationContext());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());



        ipAddressOfServerDevice = ips;
        arrayList = new ArrayList<String>();

        //relate the listView from java to the one created in xml
        mList = (ListView) findViewById(R.id.list);
        mAdapter = new MyCustomAdapter(this, arrayList);
        mList.setAdapter(mAdapter);


        ipadd = "192.168.43.155";
        /*ipadd = "192.168.225.44";*/

        Thread1 = new Thread(new Thread1());
        Thread1.start();
        mTcpClient = null;
        // connect to the server

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (s44 == "yes")
                        {

                            ipadd = "192.168.225.155";
                            /*ipadd = "192.168.225.46";*/
                            Thread1 = new Thread(new Thread1());
                            Thread1.start();
                        }
                    }
                },
                5000
        );
        session = new SessionManager(TestResultActivity.this);
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        resultReceiver = new AddressResultReceiver(new Handler());

        back = findViewById(R.id.back);
        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        profile = findViewById(R.id.profile);
        setting = findViewById(R.id.setting);
        txtLatLong = findViewById(R.id.txtLatLong);
        txtAddress = findViewById(R.id.txtAddress);

        Intent i1 = getIntent();
        memberid = i1.getStringExtra("id");

        if (memberid == "null") {
            memberid = userid;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestResultActivity.this, HomeScreen.class));
                finish();
            }
        });
        if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TestResultActivity.this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }

 /*       Intent i = getIntent();
        ipaddress = i.getStringExtra("ipaddress");*/
//        Log.d("ipaddress add2",ipaddress);
//        wifiTask.sendMessage("hello");
        txtcelcius = (TextView) findViewById(R.id.txtcelcius);
        txtfahrenheit = (TextView) findViewById(R.id.txtfahrenheit);
        txtbpm = (TextView) findViewById(R.id.txtbpm);
        txtpulse = (TextView) findViewById(R.id.txtpulse);

        textStatus = (TextView) findViewById(R.id.textStatus);
        textRX = (TextView) findViewById(R.id.textRX);
        textTX = (TextView) findViewById(R.id.textTX);
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        editSend = (EditText) findViewById(R.id.editSend);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        Thread1 = new Thread(new Thread1());
        Thread1.start();

//        For testing with dummy static data
//        int secondsDelayed = 1;
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                boolean isInserted = mydb.inertData(identity,
//                        name,
//                        "39", "95",
//                        "82", "90",
//                        txtAddress.getText().toString(), currentTime
//                        , currentDate);
//
//                if (isInserted)
//                {
//                    Toast.makeText(TestResultActivity.this, "Your Test Done Successfully", Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(TestResultActivity.this,StudentListActivity.class);
//                    startActivity(i);
//                    finish();
//                }
//            }
//        }, secondsDelayed * 2000);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestResultActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestResultActivity.this, TimeLineActivity.class);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestResultActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
   /*     txtcelcius.setText(string.substring(0, 5));
        txtfahrenheit.setText(string.substring(5, 10));
        txtbpm.setText(string.substring(10, 13));
        txtspo.setText(string.substring(13, 16));*/


        ftr = findViewById(R.id.ftr);
        ftr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//        usertemp();

    }


    private void getCurrentLocation() {


        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
        LocationServices.getFusedLocationProviderClient(TestResultActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(TestResultActivity.this)
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
                Log.d("your address", txtAddress.getText().toString());
//                Toast.makeText(TestResultActivity.this, "" + txtAddress.getText().toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TestResultActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
//            progressBar.setVisibility(View.GONE);
        }
    }

    void setStatus(String s) {
        Log.v(TAG, s);
        textStatus.setText(s);
    }

    public void connectButtonPressed(View v) {

/*
        if (wifiTask != null) {
            setStatus("Already connected!");
            return;
        }
*/

        try {
            String host = editTextAddress.getText().toString();
            int port = Integer.parseInt(editTextPort.getText().toString());

            setStatus("Attempting to connect...");
            //        wifiTask = new WiFiSocketTask(host, port);
            //        wifiTask.execute();


        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Invalid address/port!");
        }
        Timer t1 = new Timer(false);
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        usertemp();
                    }
                });
            }
        }, 7000);
    }

    public void disconnectButtonPressed(View v) {

      /*  if (wifiTask == null) {
            setStatus("Already disconnected!");
            return;
        }*/

        //     wifiTask.disconnect();
        setStatus("Disconnecting...");
    }

    private void connected() {
        setStatus("Connected.");

    }

    private void disconnected() {
        setStatus("Disconnected.");

        textRX.setText("");
        textTX.setText("");
//        wifiTask = null;
    }

    DatabaseHelper mydb;
    private void gotMessage(String msg) {
        textRX.setText(msg);

        if (msg.equals("CLOSED")) {
            textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int arg0) {
                    if(arg0 == TextToSpeech.SUCCESS)
                    {
                        textToSpeech.setLanguage(Locale.US);
                        textToSpeech.setSpeechRate((float) 0.8);
                        textToSpeech.speak("Your body temperature is "+ txtcelcius.getText().toString()+" degree celcius and "+
                                txtfahrenheit.getText().toString()+" fahrenheit and pulse rate is "+txtpulse.getText().toString()+ " per minute and "+
                                "oxygen saturation level in percent is "+txtbpm.getText().toString(), TextToSpeech.SUCCESS, null);
                    }
                }
            });
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            boolean isInserted = mydb.inertData(identity,
                    name,
                    txtcelcius.getText().toString(), txtfahrenheit.getText().toString(),
                    txtpulse.getText().toString(), txtbpm.getText().toString(),
                    txtAddress.getText().toString(), currentTime
                    , currentDate);

            if (isInserted)
            {
                Toast.makeText(this, "Your Test Done Successfully", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(TestResultActivity.this,StudentListActivity.class);
                startActivity(i);
                finish();
            }
//            usertemp();
        } else {
            String string = textRX.getText().toString();

            txtcelcius.setText(string.substring(0, 5));
        /*    Integer len = Integer.valueOf(txtcelcius.getText().toString());

            if (len >= 37) {
                txtcelcius.setTextColor(Color.parseColor("#E53935"));
            } else {
                txtcelcius.setTextColor(Color.parseColor("#00897B"));
            }
*/
            txtfahrenheit.setText(string.substring(5, 10));
        /*    Integer len1 = Integer.valueOf(txtcelcius.getText().toString());

            if (len1 >= 100) {
                txtfahrenheit.setTextColor(Color.parseColor("#E53935"));
            } else {
                txtfahrenheit.setTextColor(Color.parseColor("#00897B"));
            }*/

            txtbpm.setText(string.substring(10, 13));
       /*     Integer len3 = Integer.valueOf(txtcelcius.getText().toString());

            if (len3 >= 85) {
                txtbpm.setTextColor(Color.parseColor("#E53935"));
            } else {
                txtbpm.setTextColor(Color.parseColor("#00897B"));
            }*/
            txtpulse.setText(string.substring(13, 16));
      /*      Integer len4 = Integer.valueOf(txtcelcius.getText().toString());

            if (len4 >= 96) {
                txtpulse.setTextColor(Color.parseColor("#E53935"));
            } else {
                txtpulse.setTextColor(Color.parseColor("#00897B"));
            }*/

            Log.v(TAG, "[RX] " + msg);
        }
    }

    public void sendButtonPressed(View v) {

  /*      if (wifiTask == null) return;

        String msg = editSend.getText().toString();
        if (msg.length() == 0) return;

        wifiTask.sendMessage(msg);
        wifiTask.sendMessage(msg);
        editSend.setText("");

        textTX.setText(msg);
        Log.v(TAG, "[TX] " + msg);*/
    }

  /*  public class WiFiSocketTask extends AsyncTask<Void, String, Void> {

        // Location of the remote host
        String address;
        int port;

        // Special messages denoting connection status
        private static final String PING_MSG = "SOCKET_PING";
        private static final String CONNECTED_MSG = "SOCKET_CONNECTED";
        private static final String DISCONNECTED_MSG = "SOCKET_DISCONNECTED";

        Socket socket = null;
        BufferedReader inStream = null;
        OutputStream outStream = null;

        // Signal to disconnect from the socket
        private boolean disconnectSignal = false;

        // Socket timeout - close if no messages received (ms)
        private int timeout = 5000;

        // Constructor
        WiFiSocketTask(String address, int port) {
            this.address = address;
            this.port = port;
        }

        @Override
        protected Void doInBackground(Void... arg) {

            try {

                // Open the socket and connect to it
                socket = new Socket();
                socket.connect(new InetSocketAddress(address, port), timeout);

                // Get the input and output streams
                inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outStream = socket.getOutputStream();

                // Confirm that the socket opened
                if (socket.isConnected()) {

                    // Make sure the input stream becomes ready, or timeout
                    long start = System.currentTimeMillis();

                    while (!inStream.ready()) {
                        long now = System.currentTimeMillis();
                        if (now - start > timeout) {
                            Log.e(TAG, "Input stream timeout, disconnecting!");
                            disconnectSignal = true;
                            break;
                        }
                    }
                } else {
                    Log.e(TAG, "Socket did not connect!");
                    disconnectSignal = true;
                }

                // Read messages in a loop until disconnected
                while (!disconnectSignal) {

                    // Parse a message with a newline character
                    String msg = inStream.readLine();

                    // Send it to the UI thread
                    publishProgress(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in socket thread!");
            }

            // Send a disconnect message
//            publishProgress(DISCONNECTED_MSG);

            // Once disconnected, try to close the streams
            try {
                if (socket != null) socket.close();
                if (inStream != null) inStream.close();
                if (outStream != null) outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            String msg = values[0];
            if (msg == null) return;

            // Handle meta-messages
            if (msg.equals(CONNECTED_MSG)) {
      ;          connected();
            }

            else if (msg.equals(PING_MSG)) {
            }

            // Invoke the gotMessage callback for all other messages
            else
                gotMessage(msg);

            super.onProgressUpdate(values);
        }

        public void sendMessage(String data) {

            try {
                outStream.write(data.getBytes());
                outStream.write('\n');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void disconnect() {
            disconnectSignal = true;
        }
    }*/

    @Override
    public void onBackPressed() {

        startActivity(new Intent(TestResultActivity.this, HomeScreen.class));
        finish();
    }

    private void usertemp() {
        final ProgressDialog showMe = new ProgressDialog(TestResultActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
//        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.user_temp,
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

                                //   wifiTask.disconnect();
                                disconnected();
                                textStatus.setText("Your Test Done Successfully");
                                buttonConnect.setVisibility(View.GONE);
                                Toast.makeText(TestResultActivity.this, msg, Toast.LENGTH_LONG).show();

  /*                              wifiTask.disconnect();
                                if (wifiTask == null) {
                                    setStatus("Already disconnected!");
                                    return;
                                }
                                setStatus("Disconnecting...");-
*/
                                //                    Toast.makeText(TestResultActivity.this, msg, Toast.LENGTH_LONG).show();

                            } else {
//                                wifiTask.disconnect();
//                                disconnected();
//                                wifiTask.disconnect();
//                                wifiTask.disconnect();
//                                setStatus("Disconnecting...");


                                // Showing Echo Response Message Coming From Server.
//                                Toast.makeText(TestResultActivity.this, msg, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            showMe.dismiss();
                            Toast.makeText(TestResultActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {


                        NetworkDialog();
                        showMe.dismiss();

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
//                params.put("fever_c", "36");
//                params.put("fever_f", "110");
//                params.put("oxymetry_spo2", "95");
//                params.put("oxymetry_bpm", "85");
//                params.put("address", "dhule");

                params.put("fever_c", txtcelcius.getText().toString());
                params.put("fever_f", txtfahrenheit.getText().toString());
                params.put("oxymetry_spo2", txtpulse.getText().toString());
                params.put("oxymetry_bpm", txtbpm.getText().toString());
                params.put("address", txtAddress.getText().toString());
                if (memberid == null) {
                    params.put("member_id", userid);
                    Log.d("mem id", userid);
                } else {
                    params.put("member_id", memberid);
                    Log.d("mem id2", memberid);
                }
                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(TestResultActivity.this);
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(stringRequest);
    }

    private void NetworkDialog() {
        final Dialog dialogs = new Dialog(TestResultActivity.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();

                usertemp();
            }
        });
        dialogs.show();
    }
}