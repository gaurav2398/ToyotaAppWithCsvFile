package impel.imhealthy.adminapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;


public class PersonalInformationActivity extends AppCompatActivity {

    EditText first_name,middle_name,last_name,dates,address;
    Button btn_submit;

    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    SessionManager session;
    String userid;
    String demodate = "";

    String gid, gname = "null", coid = "null", ciid = "null", Date;
    Spinner gender, spncountry, city;
    ArrayList<String> gendername = new ArrayList<String>();
    ArrayList<String> genderid = new ArrayList<String>();
    String pincode,state,district;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD4"));
        }

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        Intent intent = getIntent();

        pincode = intent.getStringExtra("pincode");
        state = intent.getStringExtra("state");
        district = intent.getStringExtra("district");

        first_name = findViewById(R.id.first_name);
        middle_name = findViewById(R.id.middle_name);
        last_name = findViewById(R.id.last_name);
        dates = findViewById(R.id.dob);
        address = findViewById(R.id.address);

        dates.setInputType(InputType.TYPE_NULL);
        dates.setTextIsSelectable(false);

        dates.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerFragmentDialog datePickerFragmentDialog = DatePickerFragmentDialog.newInstance(new DatePickerFragmentDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerFragmentDialog view, int year, int monthOfYear, int dayOfMonth) {
                            dates.setText(year + "/" + (monthOfYear) + "/" + dayOfMonth);

                            String monthName = "";
                            switch (monthOfYear) {
                                case 0:
                                    monthName = "01";
                                    break;
                                case 1:
                                    monthName = "02";
                                    break;
                                case 2:
                                    monthName = "03";
                                    break;
                                case 3:
                                    monthName = "04";
                                    break;
                                case 4:
                                    monthName = "05";
                                    break;
                                case 5:
                                    monthName = "06";
                                    break;
                                case 6:
                                    monthName = "07";
                                    break;
                                case 7:
                                    monthName = "08";
                                    break;
                                case 8:
                                    monthName = "09";
                                    break;
                                case 9:
                                    monthName = "10";
                                    break;
                                case 10:
                                    monthName = "11";
                                    break;
                                case 11:
                                    monthName = "12";
                                    break;
                                default:
                                    monthName = "Invalid month";
                                    break;

                            }
                            String dof = null;
                            if (dayOfMonth < 10) {
                                dof = "0" + dayOfMonth;
                            } else {
                                dof = String.valueOf(dayOfMonth);
                            }

                            dates.setText(dof + "/" + (monthName) + "/" + year);
                            demodate = dates.getText().toString();
                        }
                    }, mYear, mMonth, mDay);
                    datePickerFragmentDialog.show(getSupportFragmentManager(), null);
                    datePickerFragmentDialog.setMaxDate(System.currentTimeMillis());
                    datePickerFragmentDialog.setYearRange(1900, mYear);
                    datePickerFragmentDialog.setCancelColor(getResources().getColor(R.color.green));
                    datePickerFragmentDialog.setOkColor(getResources().getColor(R.color.colorAccent));
                    datePickerFragmentDialog.setAccentColor(getResources().getColor(R.color.colorAccent));
                    datePickerFragmentDialog.setOkText(getResources().getString(R.string.ok_dob));
                    datePickerFragmentDialog.setCancelText(getResources().getString(R.string.cancel_dob));

                } else {
                    // Hide your calender here
                }
            }
        });
        gender = findViewById(R.id.spngender);
        gendername.add("Select Gender");
        genderid.add("0");
        gendername.add("Male");
        genderid.add("1");
        gendername.add("Female");
        genderid.add("2");
        gendername.add("Other");
        genderid.add("3");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PersonalInformationActivity.this, android.R.layout.simple_list_item_1, gendername);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!gendername.get(position).equals("Select Gender")){
                    gid=genderid.get(position);
                    if (gid=="1")
                    {
                        gname="Male";
                    }
                    if (gid=="2")
                    {
                        gname="Female";
                    }
                    if (gid=="3")
                    {
                        gname="Other";
                    }

                    Log.d("cid",gid);

                } else {
                    Toast.makeText(PersonalInformationActivity.this,"Select your Gender",Toast.LENGTH_SHORT);
                }
//                if(gendername.get(position).equals("Select Gender")){
//
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (first_name.getText().toString().isEmpty())
                {
                    Toast.makeText(PersonalInformationActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                }
                else if (middle_name.getText().toString().isEmpty())
                {
                    Toast.makeText(PersonalInformationActivity.this, "Enter Middle Name", Toast.LENGTH_SHORT).show();

                }
                else if (last_name.getText().toString().isEmpty())
                {
                    Toast.makeText(PersonalInformationActivity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();

                }

             else if (dates.getText().toString().isEmpty())
                {
                    Toast.makeText(PersonalInformationActivity.this, "Enter DOB", Toast.LENGTH_SHORT).show();

                }

            else if (address.getText().toString().isEmpty())
                {
                    Toast.makeText(PersonalInformationActivity.this, "Enter Address", Toast.LENGTH_SHORT).show();

                }
                */

                {
                    Log.d("All Info :", first_name.getText().toString()+last_name.getText().toString()+dates+gname+address.getText().toString());
                    if (gname=="null")
                    {
                        gname="-";
                    }

                    if (first_name.getText().toString().isEmpty())
                    {
                        first_name.setText("-");
                    }
                    if (middle_name.getText().toString().isEmpty())
                    {
                        middle_name.setText("-");

                    }
                    if (last_name.getText().toString().isEmpty())
                    {
                        last_name.setText("-");
                    }

                    if (dates.getText().toString().isEmpty())
                    {
                        dates.setText("-");

                    }
                    if (address.getText().toString().isEmpty())
                    {
                        address.setText("-");

                    }
                    if (demodate.isEmpty())
                    {
                        demodate="-";

                    }
                    personalinfo();
                   // startActivity(new Intent(PersonalInformationActivity.this,ProfilePhotoActivity.class));

                }
            }
        });
    }
    private void personalinfo() {

        final ProgressDialog showMe = new ProgressDialog(PersonalInformationActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();
        String url = Config.update_details;
        mRequestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        showMe.dismiss();

                        Log.d("server personal info: ",ServerResponse);
                        try {
                            JSONObject j = new JSONObject(ServerResponse);

                            String status = j.getString("status");
                            String msg = j.getString("msg");

                            if (status.equals("200")) {

                                session.savename(first_name.getText().toString()+" "+middle_name.getText().toString()+" "+last_name.getText().toString());

                                startActivity(new Intent(PersonalInformationActivity.this,EstablishmenFormActivity.class));
                                Toast.makeText(PersonalInformationActivity.this, msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(PersonalInformationActivity.this, msg, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(PersonalInformationActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
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

                Log.d("Gener :",gname);
                params.put("api_key", "salkdha645456q21362dbasdbmsd");
                params.put("user_id", userid);
                params.put("first_name", first_name.getText().toString());
                params.put("middle_name", middle_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("dob", demodate);
                params.put("gender", gname);
                params.put("address", address.getText().toString());
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
