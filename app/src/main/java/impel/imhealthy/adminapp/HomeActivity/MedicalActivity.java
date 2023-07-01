package impel.imhealthy.adminapp.HomeActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import impel.imhealthy.adminapp.Adapter.DoctorListAdapter;
import impel.imhealthy.adminapp.Adapter.DoctorListMessageAdapter;
import impel.imhealthy.adminapp.Adapter.DoctorListWhatsappAdapter;
import impel.imhealthy.adminapp.AddDoctorFormActivityNew;
import impel.imhealthy.adminapp.HomeScreen;
import impel.imhealthy.adminapp.Model.DoctorListDialogModel;
import impel.imhealthy.adminapp.R;
import impel.imhealthy.adminapp.Timelines.TimeLineActivity;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class MedicalActivity extends AppCompatActivity {


    LinearLayout home, history, profile, setting;
    LinearLayout ll_callDoctor, ll_callCovid, ll_callH1, ll_callH2;
    LinearLayout ll_msgDoctor, ll_msgCovid;
    LinearLayout ll_whatsappDoctor, ll_whatsappCovid;
    TextView tvCmobile,tvSmobile,help1,help2;
    RelativeLayout llcentral,llstate;
    Button btnadddoctor;

    SessionManager session;
    RecyclerView recyclerView;
    DoctorListAdapter aAdapter;
    DoctorListMessageAdapter aAdapter2;
    DoctorListWhatsappAdapter aAdapter3;
    String userid,statename="";
    ImageView back;
    ArrayList<DoctorListDialogModel> dm = new ArrayList<DoctorListDialogModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }

        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);
        statename = users.get(session.KEY_STATENAME);

        help1 = findViewById(R.id.help1);
        help2 = findViewById(R.id.help2);
        help1.setText("+91-11-23978046");
        help2.setText(statename);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        checkConnection();

        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        profile = findViewById(R.id.profile);
        setting = findViewById(R.id.setting);
        ll_callDoctor = findViewById(R.id.ll_callDoctor);
        ll_callCovid = findViewById(R.id.ll_callCovid);
        ll_callH1 = findViewById(R.id.ll_callH1);
        ll_callH2 = findViewById(R.id.ll_callH2);
        ll_msgDoctor = findViewById(R.id.ll_msgDoctor);
        ll_msgCovid = findViewById(R.id.ll_msgCovid);
        ll_whatsappDoctor = findViewById(R.id.ll_whatsappDoctor);
        ll_whatsappCovid = findViewById(R.id.ll_whatsappCovid);
        btnadddoctor = findViewById(R.id.btnadddoctor);

        btnadddoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicalActivity.this, AddDoctorFormActivityNew.class);
                startActivity(intent);
            }
        });

        ll_callDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkDialog();
            }

        });
        ll_callCovid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callclick =1;
                NetworkDialogCentral();
           }
        });
        ll_callH1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "+91-11-23978046"));
                startActivity(intent);
            }
        });
        ll_callH2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + statename));
                startActivity(intent);
            }
        });
        ll_msgDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkDialog2();

            }
        });
        ll_msgCovid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callclick=2;
                NetworkDialogCentral();

            }
        });
        ll_whatsappDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkDialog3();
            }
        });
        ll_whatsappCovid.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {

                callclick=3;
                NetworkDialogCentral();

            }

        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicalActivity.this, TimeLineActivity.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicalActivity.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicalActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicalActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    @Override
    public void onBackPressed() {

        Intent a = new Intent(MedicalActivity.this, HomeScreen.class);
        startActivity(a);

        super.onBackPressed();
    }

    private void NetworkDialog() {

        final Dialog dialogs = new Dialog(MedicalActivity.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.dialogdoctordetails);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        final ProgressBar pp = (ProgressBar) dialogs.findViewById(R.id.progressBar);
        pp.setVisibility(View.VISIBLE);

        memlist();

        recyclerView = (RecyclerView) dialogs.findViewById(R.id.recycler_view2);

        recyclerView.setHasFixedSize(false);
        @SuppressLint("WrongConstant") RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(dialogs.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(aAdapter);

        aAdapter = new DoctorListAdapter(getApplicationContext(), dm);      //ds=model       d=Model
        recyclerView.setAdapter(aAdapter);
        recyclerView.setVisibility(View.GONE);
        Timer t1 = new Timer(false);
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        recyclerView.setVisibility(View.VISIBLE);
                        pp.setVisibility(View.GONE);
                    }
                });
            }
        }, 2000);

        dialogs.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialogs.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogs.getWindow().setAttributes(layoutParams);

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void checkConnection() {
        if (isOnline()) {

        } else {
            ndialog();
        }
    }

    private void NetworkDialog2() {

        final Dialog dialogs = new Dialog(MedicalActivity.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.dialogdoctordetails);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        final ProgressBar pp = (ProgressBar) dialogs.findViewById(R.id.progressBar);
        pp.setVisibility(View.VISIBLE);

        memlist2();

        recyclerView = (RecyclerView) dialogs.findViewById(R.id.recycler_view2);

        recyclerView.setHasFixedSize(false);
        @SuppressLint("WrongConstant") RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(dialogs.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(aAdapter2);

        aAdapter2 = new DoctorListMessageAdapter(getApplicationContext(), dm);      //ds=model       d=Model
        recyclerView.setAdapter(aAdapter2);
        recyclerView.setVisibility(View.GONE);
        Timer t1 = new Timer(false);
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        recyclerView.setVisibility(View.VISIBLE);
                        pp.setVisibility(View.GONE);
                    }
                });
            }
        }, 2000);

        dialogs.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialogs.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogs.getWindow().setAttributes(layoutParams);

    }

    private void NetworkDialog3() {

        final Dialog dialogs = new Dialog(MedicalActivity.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.dialogdoctordetails);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        final ProgressBar pp = (ProgressBar) dialogs.findViewById(R.id.progressBar);
        pp.setVisibility(View.VISIBLE);

        memlist3();

        recyclerView = (RecyclerView) dialogs.findViewById(R.id.recycler_view2);

        recyclerView.setHasFixedSize(false);
        @SuppressLint("WrongConstant") RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(dialogs.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(aAdapter3);

        aAdapter3 = new DoctorListWhatsappAdapter(getApplicationContext(), dm);      //ds=model       d=Model
        recyclerView.setAdapter(aAdapter3);
        recyclerView.setVisibility(View.GONE);
        Timer t1 = new Timer(false);
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        recyclerView.setVisibility(View.VISIBLE);
                        pp.setVisibility(View.GONE);
                    }
                });
            }
        }, 2000);

        dialogs.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialogs.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogs.getWindow().setAttributes(layoutParams);

    }

    private void ndialog() {
        final Dialog dialogs = new Dialog(MedicalActivity.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();
                checkConnection();
            }
        });
        dialogs.show();
    }
    int callclick = 0;
    private void NetworkDialogCentral() {

        //calling = 1; messaging=2; whatsapp=3;
        //Calling
        if (callclick == 1) {
            final Dialog dialogs = new Dialog(MedicalActivity.this);
            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogs.setContentView(R.layout.state_central_no_details);
            dialogs.setCanceledOnTouchOutside(false);
            tvCmobile = dialogs.findViewById(R.id.tvCmobile);
            tvSmobile = dialogs.findViewById(R.id.tvSmobile);

            llcentral = dialogs.findViewById(R.id.llcentral);
            llstate = dialogs.findViewById(R.id.llstate);

            tvSmobile.setText(statename);
            tvCmobile.setText("+91-11-23978046");

            llcentral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + "+91-11-23978046"));
                    startActivity(intent);
                }
            });
            llstate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + statename));
                    startActivity(intent);
                }
            });

            dialogs.show();

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialogs.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

            dialogs.getWindow().setAttributes(layoutParams);
        }

        //Messaging
        if (callclick == 2) {
            final Dialog dialogs = new Dialog(MedicalActivity.this);
            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogs.setContentView(R.layout.state_central_no_details);
            dialogs.setCanceledOnTouchOutside(false);
            tvCmobile= dialogs.findViewById(R.id.tvCmobile);
            tvSmobile= dialogs.findViewById(R.id.tvSmobile);

            llcentral = dialogs.findViewById(R.id.llcentral);
            llstate = dialogs.findViewById(R.id.llstate);

            tvSmobile.setText(statename);
            tvCmobile.setText("+91-11-23978046");
            tvSmobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("smsto:"));
                        i.setType("vnd.android-dir/mms-sms");
                        i.putExtra("address", new String(statename));
                        i.putExtra("sms_body", "");
                        startActivity(Intent.createChooser(i, "Send sms via:"));
                    } catch (Exception e) {
                        Toast.makeText(MedicalActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            llcentral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("smsto:"));
                        i.setType("vnd.android-dir/mms-sms");
                        i.putExtra("address", new String("+91-11-23978046"));
                        i.putExtra("sms_body", "");
                        startActivity(Intent.createChooser(i, "Send sms via:"));
                    } catch (Exception e) {
                        Toast.makeText(MedicalActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                    }

                }
            });


            dialogs.show();

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialogs.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

            dialogs.getWindow().setAttributes(layoutParams);

        }


        //Whatsapp
        if (callclick == 3) {
            final Dialog dialogs = new Dialog(MedicalActivity.this);
            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogs.setContentView(R.layout.state_central_no_details);
            dialogs.setCanceledOnTouchOutside(false);
            tvCmobile= dialogs.findViewById(R.id.tvCmobile);
            tvSmobile= dialogs.findViewById(R.id.tvSmobile);

            llcentral = dialogs.findViewById(R.id.llcentral);
            llstate = dialogs.findViewById(R.id.llstate);

            tvSmobile.setText(statename);
            tvCmobile.setText("+91-11-23978046");


            tvSmobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isAppInstalled = appInstalledOrNot("com.whatsapp");
                    if (isAppInstalled) {

                        String url = "https://api.whatsapp.com/send?phone=" + statename;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

                        try {
                            MedicalActivity.this.startActivity(i);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MedicalActivity.this,
                                    "Whatsapp have not been installed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Do whatever we want to do if application not installed
                        // For example, Redirect to play store

                        Toast.makeText(MedicalActivity.this, "Application is not currently installed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            llcentral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isAppInstalled = appInstalledOrNot("com.whatsapp");
                    if (isAppInstalled) {

                        String url = "https://api.whatsapp.com/send?phone=" + "+91-11-23978046";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

                        try {
                            MedicalActivity.this.startActivity(i);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MedicalActivity.this,
                                    "Whatsapp have not been installed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(MedicalActivity.this, "Application is not currently installed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialogs.show();

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialogs.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

            dialogs.getWindow().setAttributes(layoutParams);
        }
    }

    private void memlist() {
        final ProgressDialog showMe = new ProgressDialog(MedicalActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.doctor_list,
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
                                JSONArray applist = j.getJSONArray("data");

                                if (applist != null && applist.length() > 0) {
                                    for (int i = 0; i < applist.length(); i++) {
                                        DoctorListDialogModel model = new DoctorListDialogModel();

                                        JSONObject jsonObject = applist.getJSONObject(i);

                                        model.setDoctor_name(jsonObject.getString("doctor_name"));
                                        model.setMobile(jsonObject.getString("mobile"));

                                        dm.add(model);
                                        aAdapter = new DoctorListAdapter(getApplicationContext(), dm);      //ds=model       d=Model
                                        recyclerView.setAdapter(aAdapter);
                                    }
                                }

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
                            ndialog();
                            if (getFragmentManager().getBackStackEntryCount() != 0) {
                                getFragmentManager().popBackStack();
                            }

                        } else {
                            // Is thrown if there's no network connection or server is down
                            Log.d("eerror", String.valueOf(volleyError));
                            ndialog();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void memlist2() {
        final ProgressDialog showMe = new ProgressDialog(MedicalActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.doctor_list,
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
                                JSONArray applist = j.getJSONArray("data");

                                if (applist != null && applist.length() > 0) {
                                    for (int i = 0; i < applist.length(); i++) {
                                        DoctorListDialogModel model = new DoctorListDialogModel();

                                        JSONObject jsonObject = applist.getJSONObject(i);

                                        model.setDoctor_name(jsonObject.getString("doctor_name"));
                                        model.setMobile(jsonObject.getString("mobile"));

                                        dm.add(model);
                                        aAdapter2 = new DoctorListMessageAdapter(getApplicationContext(), dm);      //ds=model       d=Model
                                        recyclerView.setAdapter(aAdapter2);
                                    }
                                }

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
                            ndialog();
                            if (getFragmentManager().getBackStackEntryCount() != 0) {
                                getFragmentManager().popBackStack();
                            }

                        } else {
                            // Is thrown if there's no network connection or server is down
                            Log.d("eerror", String.valueOf(volleyError));
                            ndialog();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void memlist3() {
        final ProgressDialog showMe = new ProgressDialog(MedicalActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.doctor_list,
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
                                JSONArray applist = j.getJSONArray("data");

                                if (applist != null && applist.length() > 0) {
                                    for (int i = 0; i < applist.length(); i++) {
                                        DoctorListDialogModel model = new DoctorListDialogModel();

                                        JSONObject jsonObject = applist.getJSONObject(i);

                                        model.setDoctor_name(jsonObject.getString("doctor_name"));
                                        model.setMobile(jsonObject.getString("mobile"));

                                        dm.add(model);
                                        aAdapter3 = new DoctorListWhatsappAdapter(getApplicationContext(), dm);      //ds=model       d=Model
                                        recyclerView.setAdapter(aAdapter3);
                                    }
                                }

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
                            ndialog();
                            if (getFragmentManager().getBackStackEntryCount() != 0) {
                                getFragmentManager().popBackStack();
                            }

                        } else {
                            // Is thrown if there's no network connection or server is down
                            Log.d("eerror", String.valueOf(volleyError));
                            ndialog();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}
