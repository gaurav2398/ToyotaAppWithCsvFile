package impel.imhealthy.adminapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import impel.imhealthy.adminapp.HomeActivity.SettingActivity;
import impel.imhealthy.adminapp.Timelines.TimeLineActivity;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class AddedDoctorProfileScreen extends AppCompatActivity {

    Button btn_pin_submit;
    ImageView image;
    ImageView back;
    String userid,id;
    RequestQueue mRequestQueue;
    SessionManager session;
    TextView name,contact,gender,hospital_name,address;

    LinearLayout home, history, profile, setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_doctor_profile_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }
        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        name = findViewById(R.id.name);
        contact = findViewById(R.id.contact);
        gender = findViewById(R.id.gender);
        hospital_name = findViewById(R.id.hospital_name);
        address = findViewById(R.id.address);
        image = findViewById(R.id.image);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        home = findViewById(R.id.home);
        history = findViewById(R.id.history);

        setting = findViewById(R.id.setting);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(AddedDoctorProfileScreen.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(AddedDoctorProfileScreen.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(AddedDoctorProfileScreen.this, TimeLineActivity.class);
                startActivity(intent);
            }
        });

        id = getIntent().getStringExtra("id");
        get_doctor();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void get_doctor() {
        final ProgressDialog showMe = new ProgressDialog(AddedDoctorProfileScreen.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.get_doctor,
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

                                name.setText(j.getString("name"));
                                contact.setText(j.getString("contact"));
                                gender.setText(j.getString("gender"));
                                hospital_name.setText(j.getString("hospital_name"));
                                address.setText(j.getString("address"));


                                Glide.with(AddedDoctorProfileScreen.this).load(j.getString("image")).into(image);


                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            showMe.dismiss();
                            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
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
                params.put("id", id);

                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void NetworkDialog() {
        final Dialog dialogs = new Dialog(AddedDoctorProfileScreen.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();

                get_doctor();
            }
        });
        dialogs.show();
    }

}
