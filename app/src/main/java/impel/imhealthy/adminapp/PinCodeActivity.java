package impel.imhealthy.adminapp;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;


public class PinCodeActivity extends AppCompatActivity {

    Button btn_pin_submit;
    EditText edt_pincode, edt_state, edt_district;
    EditText phonenumber;
    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    SessionManager session;
    String state = "", district = "";
    TextView verify;
    LinearLayout lls,lld;
    String userid;
    RelativeLayout rlpincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);

        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        Log.d("user id",userid);

        if (session.isLoggedInLang() == true) {
            Intent i = new Intent(PinCodeActivity.this, HomeScreen.class);
            startActivity(i);
            finish();
        }

        rlpincode = findViewById(R.id.rlpincode);
        //rlpincode.setVisibility(View.GONE);
        //verify_user_state();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD4"));
        }


        lls = findViewById(R.id.lls);
        lld = findViewById(R.id.lld);
        edt_pincode = findViewById(R.id.edt_pincode);
        edt_state = findViewById(R.id.edt_state);
        edt_district = findViewById(R.id.edt_district);
        verify = findViewById(R.id.verify);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pincode();
                // startActivity(new Intent(PinCodeActivity.this,PersonalInformationActivity.class));
            }
        });

        btn_pin_submit = findViewById(R.id.btn_pin_submit);
        btn_pin_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_pin();
            }
        });
//        verify_user_state();
    }

    private void pincode() {

        final ProgressDialog showMe = new ProgressDialog(PinCodeActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();
        String url = Config.verify_pincode;
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


                            /*
                            if (status == "200")
                            {
                            Toast.makeText(PinCodeActivity.this, msg, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PinCodeActivity.this,PersonalInformationActivity.class));
*/

                            if (status.equals("200")) {
                                state = j.getString("state");
                                district = j.getString("district");

                                //   Toast.makeText(PinCodeActivity.this, state + " " + district, Toast.LENGTH_SHORT).show();
                                edt_state.setText(state);
                                edt_district.setText(district);


                                lls.setVisibility(View.VISIBLE);
                                lld.setVisibility(View.VISIBLE);
                                btn_pin_submit.setVisibility(View.VISIBLE);

                                if (state.equals("Andhra Pradesh"))
                                {
                                    session.savestatename("0866-2410978");
                                }

                                if (state.equals("Arunachal Pradesh"))
                                {
                                    session.savestatename("9436055743");
                                }
                                if (state.equals("Assam"))
                                {
                                    session.savestatename("6913347770");
                                }
                                if (state.equals("Bihar"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Chhattisgarh"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Goa"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Gujarat"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Haryana"))
                                {
                                    session.savestatename("8558893911");
                                }
                                if (state.equals("Himachal Pradesh"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Jharkhand"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Karnataka"))
                                {
                                    session.savestatename("8558893911");
                                }
                                if (state.equals("Kerala"))
                                {
                                    session.savestatename("0471-2552056");
                                }
                                if (state.equals("Madhya Pradesh"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Maharashtra"))
                                {
                                    session.savestatename("020-26127394");
                                }
                                if (state.equals("Manipur"))
                                {
                                    session.savestatename("3852411668");
                                }
                                if (state.equals("Meghalaya"))
                                {
                                    session.savestatename("108");
                                }
                                if (state.equals("Mizoram"))
                                {
                                    session.savestatename("102");
                                }
                                if (state.equals("Nagaland"))
                                {
                                    session.savestatename("7005539653");
                                }
                                if (state.equals("Odisha"))
                                {
                                    session.savestatename("9439994859");
                                }
                                if (state.equals("Punjab"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Rajasthan"))
                                {
                                    session.savestatename("0141-2225624");
                                }
                                if (state.equals("Sikkim"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Tamil Nadu"))
                                {
                                    session.savestatename("044-29510500");
                                }
                                if (state.equals("Telangana"))
                                {
                                    session.savestatename("104");
                                }

                                if (state.equals("Tripura"))
                                {
                                    session.savestatename("0381-2315879");
                                }
                                if (state.equals("Uttarakhand"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Uttar Pradesh"))
                                {
                                    session.savestatename("18001805145");
                                }
                                if (state.equals("West Bengal"))
                                {
                                    session.savestatename("1800313444222");
                                }
                                if (state.equals("Andaman and Nicobar Islands"))
                                {
                                    session.savestatename("03192-232102");
                                }
                                if (state.equals("Chandigarh"))
                                {
                                    session.savestatename("9779558282");
                                }
                                if (state.equals("Dadra and Nagar Haveli and Daman & Diu"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Delhi"))
                                {
                                    session.savestatename("011-22307145");
                                }
                                if (state.equals("Jammu & Kashmir"))
                                {
                                    session.savestatename("01912520982");
                                }
                                if (state.equals("Ladakh"))
                                {
                                    session.savestatename("01982256462");
                                }
                                if (state.equals("Lakshadweep"))
                                {
                                    session.savestatename("104");
                                }
                                if (state.equals("Puducherry"))
                                {
                                    session.savestatename("104");
                                }
                                //       startActivity(new Intent(PinCodeActivity.this, PersonalInformationActivity.class));

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(PinCodeActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                15000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        showMe.dismiss();
//                        NetworkDialog();
                        Log.d("Error", String.valueOf(error));
                        Toast.makeText(PinCodeActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
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
                params.put("pincode", edt_pincode.getText().toString());
                return params;
            }
        };
        stringRequest.setTag("TAG");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    private void update_pin() {

        final ProgressDialog showMe = new ProgressDialog(PinCodeActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();
        String url = Config.update_pin;
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
                            /*
                            if (status == "200")
                            {
                            Toast.makeText(PinCodeActivity.this, msg, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PinCodeActivity.this,PersonalInformationActivity.class));
*/

                            if (status.equals("200")) {

                                Intent intent = new Intent(PinCodeActivity.this, PersonalInformationActivity.class);
                                intent.putExtra("pincode", edt_pincode.getText().toString());
                                intent.putExtra("state", edt_state.getText().toString());
                                intent.putExtra("district", edt_district.getText().toString());
                                startActivity(intent);
                                //       startActivity(new Intent(PinCodeActivity.this, PersonalInformationActivity.class));

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(PinCodeActivity.this, msg, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(PinCodeActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
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
                params.put("user_id", userid);
                params.put("pincode", edt_pincode.getText().toString());
                params.put("state", edt_state.getText().toString());
                params.put("district", edt_district.getText().toString());
                return params;
            }
        };
        stringRequest.setTag("TAG");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(stringRequest);

    }
    private void verify_user_state() {
        final ProgressDialog showMe = new ProgressDialog(PinCodeActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);

        String url = Config.verify_user_state;
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

                            if (status.equals("500")) {
                                rlpincode.setVisibility(View.VISIBLE);
                                Intent i = new Intent(PinCodeActivity.this,PinCodeActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else if (status.equals("600")) {
                                rlpincode.setVisibility(View.VISIBLE);
                                Intent i = new Intent(PinCodeActivity.this,HomeScreen.class);
                                startActivity(i);
                                finish();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(PinCodeActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                15000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        showMe.dismiss();
//                        NetworkDialog();
                        Log.d("Error", String.valueOf(error));
                        Toast.makeText(PinCodeActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
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
                params.put("user_id", userid);
                return params;
            }
        };
        stringRequest.setTag("TAG");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(stringRequest);

    }

}