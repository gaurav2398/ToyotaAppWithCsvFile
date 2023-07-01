package impel.imhealthy.adminapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class LoginActivity extends AppCompatActivity {

    Button signin;
    EditText phonenumber;
    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    SessionManager session;
    TextView terms;
    String no;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn() == true) {
            Intent i = new Intent(LoginActivity.this, PinCodeActivity.class);
            startActivity(i);
            finish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD4"));
        }
        terms = findViewById(R.id.terms);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
        phonenumber = findViewById(R.id.phonenumber);

        signin = findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phonenumber.length()==10)
                {
                    cine_signup();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Enter your 10 digit number", Toast.LENGTH_SHORT).show();
                }
               // cine_signup();
              // startActivity(new Intent(LoginActivity.this,OtpActivity.class));
            }
        });
/*

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Config1.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config1.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config1.PUSH_NOTIFICATION)) {

                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
        displayFirebaseRegId();*/
    }
    private void cine_signup() {

        final ProgressDialog showMe = new ProgressDialog(LoginActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();
        String url = Config.user_signup;
        mRequestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        showMe.dismiss();

                        try {
                            JSONObject j = new JSONObject(ServerResponse);

                            String status = j.getString("status");
                            String msg = j.getString("msg");

                            if (status.equals("200")) {

                                no = phonenumber.getText().toString();
                                validNo(no);
                                Intent intent = new Intent(LoginActivity.this,OtpActivity.class);
                                intent.putExtra("mobile",no);
                                startActivity(intent);

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showMe.dismiss();
//                        NetworkDialog();
                        Log.d("Error", String.valueOf(error));
                        Toast.makeText(LoginActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                return headers;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("api_key", "salkdha645456q21362dbasdbmsd");
                params.put("mobile", phonenumber.getText().toString());
                return params;
            }
        };
        stringRequest.setTag("TAG");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);

    }
    private void validNo(String no){
        if(no.isEmpty() || no.length() < 10){
            phonenumber.setError("Enter a valid mobile");
            phonenumber.requestFocus();
            return;
        }
    }
/*
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config1.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("TAG", "Firebase reg id: " + regId);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
                Log.e("get token", newToken);
//                Toast.makeText(LoginActivity.this, newToken, Toast.LENGTH_SHORT).show();
            }
        });


    }*/

}
