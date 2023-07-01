package impel.imhealthy.adminapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class SplashActivity extends AppCompatActivity {


    public View decorView;

    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    SessionManager session;
    String userid;
    private Handler handler = new Handler();

    public static  String baseurl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

      //  verify_user_state();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        decorView = getWindow().getDecorView();
        hideUI();
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashActivity.this, FirstActivity.class));
//                                startActivity(new Intent(SplashActivity.this, HomeScreen.class));
                finish();
            }
        }, secondsDelayed * 1700);


//        getBaseUrl();
//        handler = new Handler() {
//            public void handleMessage(android.os.Message msg) {
//                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://wifidemo-1015f.firebaseio.com/");
//                DatabaseReference mDbRef = mDatabase.getReference("SQLLITE");
//
//                mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(final DataSnapshot dataSnapshot) {
//
//                        String value = (String) dataSnapshot.getValue();
//
//                        if (value.equals("yes") || value=="Yes")
//                        {
//                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
////                                startActivity(new Intent(SplashActivity.this, HomeScreen.class));
//                                finish();
//                        }
//                        else if (value.equals("maintenance") || value=="Maintenance")
//                        {
//                            Toast.makeText(SplashActivity.this, "Server is in Maintenance\nKindly contact your developer", Toast.LENGTH_LONG).show();
//                        }
//
//                        else if (value.equals("developercharges") || value=="Developercharges")
//                        {
//                            getdata();
//         }
//                        else if (value.equals("server") || value=="Server")
//                        {
//                            Toast.makeText(SplashActivity.this, "Server Plan is Expired\nKindly renew your server", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        };
//        handler.sendEmptyMessage(0);
//        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected() == true) {
//
//        } else {
//            NetworkDialog();
//        }
    }
    public void hideUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
        );
    }

    private void getdata() {

        // calling add value event listener method
        // for getting the values from database.
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://wifidemo-1015f.firebaseio.com/");
        DatabaseReference mDbRef = mDatabase.getReference("msg");

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);

                Toast.makeText(SplashActivity.this, ""+value, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SplashActivity.this, "Fail to get data.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getBaseUrl() {

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://wifidemo-1015f.firebaseio.com/");
        DatabaseReference mDbRef = mDatabase.getReference("baseurl");

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                baseurl =value;
                Log.d("burl1",baseurl);
                Log.d("burl2", Config.add_doctor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SplashActivity.this, "Fail to get data.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void NetworkDialog() {
        final Dialog dialogs = new Dialog(SplashActivity.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this,SplashActivity.class);
                startActivity(intent);
                finish();
                dialogs.dismiss();
            }
        });
        dialogs.show();
    }

}