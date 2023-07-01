package impel.imhealthy.adminapp.SortingFragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import impel.imhealthy.adminapp.Adapter.DayAdapter;
import impel.imhealthy.adminapp.Adapter.StudentListAdapter;
import impel.imhealthy.adminapp.BuildConfig;
import impel.imhealthy.adminapp.DatabaseHelperNew;
import impel.imhealthy.adminapp.Excel.CSVWriter;
import impel.imhealthy.adminapp.Excel.DatabaseHelper;
import impel.imhealthy.adminapp.HomeActivity.SymptomFormActivity;
import impel.imhealthy.adminapp.Model.DaySortingModel;
import impel.imhealthy.adminapp.R;
import impel.imhealthy.adminapp.StudentListActivity;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class DayFragment extends Fragment {

    RecyclerView mRecyclerView;
    public DayAdapter dayAdapter;

    String getpostid, hash_id, role;
    SessionManager session;
    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    ArrayList<DaySortingModel> dm = new ArrayList<DaySortingModel>();

    DatabaseHelper mydb;
    DatabaseHelperNew mydbNew;
    String userid;
    BottomSheetDialog bottomSheetDialog;
    FloatingActionButton fabshare;
    LinearLayout llinstagram, llwhatsapp, llmessage, llemail;
    TextView tvmessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        mydb = new DatabaseHelper(getActivity());
//        mydb.delete();
        session = new SessionManager(getActivity());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        Log.d("user id = ", userid);
        fabshare = view.findViewById(R.id.fabshare);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.reviewrecycler);
        mRecyclerView.setHasFixedSize(false);
        @SuppressLint("WrongConstant") RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


//        getusertemp();
//        getusertempNew();

        dm.clear();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        dm.addAll(mydb.getAllDataHistory(currentDate+""));

        dayAdapter = new DayAdapter(getActivity(), dm);      //ds=model       d=Model
        mRecyclerView.setAdapter(dayAdapter);


        fabshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.qrcode);
                bottomSheetDialog.setContentView(R.layout.bottomsheetsharingdetails_reports);
                bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.show();

                tvmessage = bottomSheetDialog.findViewById(R.id.tvmessage);
                tvmessage.setText("Mail");
                llinstagram = bottomSheetDialog.findViewById(R.id.llinstagram);
                llwhatsapp = bottomSheetDialog.findViewById(R.id.llwhatsapp);
                llmessage = bottomSheetDialog.findViewById(R.id.llmessage);
                llemail = bottomSheetDialog.findViewById(R.id.llemail);

                llinstagram.setVisibility(View.GONE);
                llinstagram.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isAppInstalled = appInstalledOrNot("com.instagram.android");
                        if (isAppInstalled) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.setType("image/*");
                            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            shareIntent.putExtra(Intent.EXTRA_TEXT, "Via I'm Healthy Application");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "DailyReport.csv"));
                            shareIntent.setPackage("com.instagram.android");
                            startActivity(shareIntent);

                        } else {

                            Toast.makeText(getActivity(), "Application is not currently installed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                llwhatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isAppInstalled = appInstalledOrNot("com.whatsapp");
                        if (isAppInstalled) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            shareIntent.setType("text/plain");
                            shareIntent.setType("image/*");
                            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "Via I'm Healthy Application");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "/Im Healthy Admin/DailyReport.csv"));
                            shareIntent.setPackage("com.whatsapp");
                            startActivity(shareIntent);
                            bottomSheetDialog.dismiss();
                        } else {
                            // Do whatever we want to do if application not installed
                            // For example, Redirect to play store

                            Toast.makeText(getActivity(), "Application is not currently installed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                llemail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(getActivity(), "Downloaded Successfully\nFolder : I'm Healthy Admin", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

                llmessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            File f = new File(Environment.getExternalStorageDirectory(), "Im Healthy Admin/DailyReport.csv");
                            final String[] TO = { "" };

                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, f.getName().replaceAll("(?i).pdf", ""));

                            if (!f.exists() || !f.canRead()) {
                                Toast.makeText(getActivity(), "Attachment Error", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                                return;
                            }

                            Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID, f);
                            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            emailIntent.setDataAndType(uri, getActivity().getContentResolver().getType(uri));
                            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

                            try {
                                startActivity(emailIntent);
                                bottomSheetDialog.dismiss();
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getActivity(),
                                        "There is no email client installed.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        exportDB();
        return view;
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private void exportDB() {


        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Log.d("services", String.valueOf(mydb.getResults()));
        DatabaseHelper dbhelper = new DatabaseHelper(getActivity());
        File exportDir = new File(Environment.getExternalStorageDirectory(), "Im Healthy Admin");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "DailyReport.csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM naturesafari_table", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                if (curCSV.getString(8).equals(currentDate) || curCSV.getString(8)==currentDate) {
                    String arrStr[] = {curCSV.getString(1), curCSV.getString(0), curCSV.getString(2)
                            , curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6)
                            , curCSV.getString(7), curCSV.getString(8)};
                    csvWrite.writeNext(arrStr);
                }
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    private void getusertemp() {

        final ProgressDialog showMe = new ProgressDialog(getActivity());
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();
        String url = Config.admin_get_temp;
        mRequestQueue = Volley.newRequestQueue(getActivity());
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
                                dm.clear();
                                JSONArray applist1 = j.getJSONArray("data");

                                for (int i = 0; i < applist1.length(); i++) {

                                    DaySortingModel model = new DaySortingModel();

                                    JSONObject jsonObject = applist1.getJSONObject(i);
                                    if (jsonObject.getString("user_id").equals(userid)) {

                                        model.setFever_c(jsonObject.getString("fever_c"));
                                        model.setFever_f(jsonObject.getString("fever_f"));
                                        model.setOxymetry_spo2(jsonObject.getString("oxymetry_spo2"));
                                        model.setOxymetry_bpm(jsonObject.getString("oxymetry_bpm"));
                                        model.setAddress(jsonObject.getString("address"));
                                        model.setTime(jsonObject.getString("time"));
                                        model.setMember_name(jsonObject.getString("member_name"));

                                        boolean isInserted = mydb.inertData(jsonObject.getString("id"),
                                                jsonObject.getString("member_name"),
                                                jsonObject.getString("fever_c"), jsonObject.getString("fever_f"),
                                                jsonObject.getString("oxymetry_spo2"), jsonObject.getString("oxymetry_bpm"),
                                                jsonObject.getString("address"), jsonObject.getString("time")
                                                , jsonObject.getString("created_at"));

                                        Log.d("inserted", String.valueOf(isInserted));

                                        dm.add(model);
                                        dayAdapter = new DayAdapter(getActivity(), dm);      //ds=model       d=Model
                                        mRecyclerView.setAdapter(dayAdapter);
                                    }
                                }
                                exportDB();
                                //   getusertempNew();
                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Not connected to internet", Toast.LENGTH_SHORT).show();
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
                params.put("value", "");
                //    params.put("user_id", userid);
                return params;
            }
        };
        stringRequest.setTag("TAG");

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(stringRequest);
    }

    private void getusertempNew() {

        final ProgressDialog showMe = new ProgressDialog(getActivity());
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        //       showMe.show();
        String url = Config.get_user_temp_by_admin;
        mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        showMe.dismiss();

                        try {
                            JSONObject j = new JSONObject(ServerResponse);

                            Log.d("resoposne new", ServerResponse);
                            String status = j.getString("status");
                            String msg = j.getString("msg");

                            if (status.equals("200")) {
                                JSONArray applist1 = j.getJSONArray("data");

                                for (int i = 0; i < applist1.length(); i++) {

                                    DaySortingModel model = new DaySortingModel();

                                    JSONObject jsonObject = applist1.getJSONObject(i);
                                    if (jsonObject.getString("admin_id").equals(userid)) {

                                        model.setFever_c(jsonObject.getString("fever_c"));
                                        model.setFever_f(jsonObject.getString("fever_f"));
                                        model.setOxymetry_spo2(jsonObject.getString("oxymetry_spo2"));
                                        model.setOxymetry_bpm(jsonObject.getString("oxymetry_bpm"));
                                        model.setAddress(jsonObject.getString("address"));
                                        model.setTime(jsonObject.getString("time"));
                                        model.setMember_name(jsonObject.getString("user_name"));


                                        dm.add(model);
                                        dayAdapter = new DayAdapter(getActivity(), dm);      //ds=model       d=Model
                                        mRecyclerView.setAdapter(dayAdapter);
                                    }
                                }
                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Not connected to internet", Toast.LENGTH_SHORT).show();
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

                Log.d("admin id", userid);

                params.put("api_key", "salkdha645456q21362dbasdbmsd");
                params.put("value", "");
                params.put("admin_id", userid);
                return params;
            }
        };
        stringRequest.setTag("TAG");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }
}
