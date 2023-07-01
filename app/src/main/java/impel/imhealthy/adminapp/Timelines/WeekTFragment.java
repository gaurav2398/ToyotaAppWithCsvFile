package impel.imhealthy.adminapp.Timelines;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import impel.imhealthy.adminapp.Excel.DatabaseHelper;
import impel.imhealthy.adminapp.Model.WeekSortingModel;
import impel.imhealthy.adminapp.R;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class WeekTFragment extends Fragment {

    BarChart barChart;
    BarDataSet bardataset;
    SessionManager session;
    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    String userid;
    Integer totalcount=0,totalvisitors=0,totalfailed=0;

    ArrayList<WeekSortingModel> dm = new ArrayList<WeekSortingModel>();
    DatabaseHelper mydb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_week_t, container, false);

        barChart = (BarChart) view.findViewById(R.id.barchart);


        session = new SessionManager(getActivity());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);



        mydb = new DatabaseHelper(getActivity());

        dm.clear();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        String lastWeekDate  = getCalculatedDate(currentDate, "dd-MM-yyyy", -7);  // It will gives you date after 10 days from given date

        dm.addAll(mydb.getAllDataHistoryWeekly(currentDate,lastWeekDate));
        for (int i = 0; i < dm.size(); i++) {
            totalcount++;

            int cel = Integer.parseInt(dm.get(i).getFever_c());
            int fev = Integer.parseInt(dm.get(i).getFever_f());
            int spo = Integer.parseInt(dm.get(i).getOxymetry_spo2());
            int bpn = Integer.parseInt(dm.get(i).getOxymetry_bpm());

            if (cel > 37 || fev > 100 || spo > 93 || bpn > 100) {
                totalfailed++;
            }
            Log.d("totoalcount", String.valueOf(totalcount));
            Log.d("totalvisitors", String.valueOf(totalvisitors));
            Log.d("totalfailed", String.valueOf(totalfailed));

            totalvisitors = totalcount - totalfailed;


            barChart.setScaleEnabled(false);
            barChart.setClickable(false);
            barChart.setEnabled(false);
            barChart.setFocusable(false);

            //for background grid lines hide
/*
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
*/
            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(totalcount, 0));
            entries.add(new BarEntry(totalvisitors, 1));
            entries.add(new BarEntry(totalfailed, 2));


            bardataset = new BarDataSet(entries, "Cells");

            ArrayList<String> labels = new ArrayList<String>();
            labels.add(" ");
            labels.add(" ");
            labels.add(" ");

            BarData data = new BarData(labels, bardataset);
            barChart.setData(data); // set the data and list of labels into chart
            barChart.setDescription(" ");  // set the description
            //bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
            bardataset.setColors(new int[]{Color.parseColor("#0FD6B7"), Color.parseColor("#1BA9F1"), Color.parseColor("#FB595D")});

            bardataset.setValueFormatter(new MyValueFormatter());
            bardataset.setValueTextColor(Color.BLACK);
            bardataset.setValueTextSize(16f);
            barChart.animateY(2000);
        }
//        getusertemp();
//        getusertempNew();

        dm.clear();
        return view;
    }

    public static String getCalculatedDate(String date, String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        try {
            return s.format(new Date(s.parse(date).getTime()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            Log.e("TAG", "Error in Parsing Date : " + e.getMessage());
        }
        return null;
    }
    public class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("#");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
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

                            totalfailed=0;
                            totalcount=0;
                            totalvisitors=0;
                            if (status.equals("200")) {

                                JSONArray applist1 = j.getJSONArray("data");

                                for (int i = 0; i < applist1.length(); i++) {

                                    JSONObject jsonObject = applist1.getJSONObject(i);
                                    if (jsonObject.getString("user_id").equals(userid)) {

                                    if (jsonObject.getString("fever_c").isEmpty())
                                    {
                                        Toast.makeText(getActivity(), "No Data Found For Today", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Integer cel = jsonObject.getInt("fever_c");
                                        Integer fev = jsonObject.getInt("fever_f");
                                        Integer spo = jsonObject.getInt("oxymetry_spo2");
                                        Integer bpn = jsonObject.getInt("oxymetry_bpm");

                                        totalcount++;

                                        if (cel > 37 || fev > 100 || spo > 93 || bpn > 100) {
                                            totalfailed++;
                                        }
                                        Log.d("totoalcount", String.valueOf(totalcount));
                                        Log.d("totalvisitors", String.valueOf(totalvisitors));
                                        Log.d("totalfailed", String.valueOf(totalfailed));

                                        totalvisitors = totalcount-totalfailed;


                                        barChart.setScaleEnabled(false);
                                        barChart.setClickable(false);
                                        barChart.setEnabled(false);
                                        barChart.setFocusable(false);

                                        //for background grid lines hide
/*
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
*/
                                        ArrayList<BarEntry> entries = new ArrayList<>();
                                        entries.add(new BarEntry(totalcount, 0));
                                        entries.add(new BarEntry(totalvisitors, 1));
                                        entries.add(new BarEntry(totalfailed, 2));


                                        bardataset = new BarDataSet(entries, "Cells");

                                        ArrayList<String> labels = new ArrayList<String>();
                                        labels.add(" ");
                                        labels.add(" ");
                                        labels.add(" ");

                                        BarData data = new BarData(labels, bardataset);
                                        barChart.setData(data); // set the data and list of labels into chart
                                        barChart.setDescription(" ");  // set the description
                                        //bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                                        bardataset.setColors(new int[] {Color.parseColor("#0FD6B7"), Color.parseColor("#1BA9F1"), Color.parseColor("#FB595D")});

                                        bardataset.setValueFormatter(new MyValueFormatter());
                                        bardataset.setValueTextColor(Color.BLACK);
                                        bardataset.setValueTextSize(16f);
                                        barChart.animateY(2000);

                                    }
                                }

                               // getusertempNew();

                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } }else {

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
                params.put("value", "1");
                //         params.put("user_id", userid);
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
        //   showMe.show();
        String url = Config.get_user_temp_by_admin;
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

                                JSONArray applist1 = j.getJSONArray("data");

                                for (int i = 0; i < applist1.length(); i++) {

                                    JSONObject jsonObject = applist1.getJSONObject(i);
                                    if (jsonObject.getString("admin_id").equals(userid)) {

                                    if (jsonObject.getString("fever_c").isEmpty())
                                    {
                                        Toast.makeText(getActivity(), "No Data Found For Today", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Integer cel = jsonObject.getInt("fever_c");
                                        Integer fev = jsonObject.getInt("fever_f");
                                        Integer spo = jsonObject.getInt("oxymetry_spo2");
                                        Integer bpn = jsonObject.getInt("oxymetry_bpm");

                                        totalcount++;

                                        if (cel > 37 || fev > 100 || spo > 93 || bpn > 100) {
                                            totalfailed++;
                                        }
                                        Log.d("totoalcount", String.valueOf(totalcount));
                                        Log.d("totalvisitors", String.valueOf(totalvisitors));
                                        Log.d("totalfailed", String.valueOf(totalfailed));

                                        totalvisitors = totalcount-totalfailed;


                                        barChart.setScaleEnabled(false);
                                        barChart.setClickable(false);
                                        barChart.setEnabled(false);
                                        barChart.setFocusable(false);

                                        //for background grid lines hide
/*
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
*/
                                        ArrayList<BarEntry> entries = new ArrayList<>();
                                        entries.add(new BarEntry(totalcount, 0));
                                        entries.add(new BarEntry(totalvisitors, 1));
                                        entries.add(new BarEntry(totalfailed, 2));


                                        bardataset = new BarDataSet(entries, "Cells");

                                        ArrayList<String> labels = new ArrayList<String>();
                                        labels.add(" ");
                                        labels.add(" ");
                                        labels.add(" ");

                                        BarData data = new BarData(labels, bardataset);
                                        barChart.setData(data); // set the data and list of labels into chart
                                        barChart.setDescription(" ");  // set the description
                                        //bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                                        bardataset.setColors(new int[] {Color.parseColor("#0FD6B7"), Color.parseColor("#1BA9F1"), Color.parseColor("#FB595D")});

                                        bardataset.setValueFormatter(new MyValueFormatter());
                                        bardataset.setValueTextColor(Color.BLACK);
                                        bardataset.setValueTextSize(16f);
                                        barChart.animateY(2000);

                                    }
                                }


                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } }else {

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
                params.put("value", "1");
                params.put("admin_id", userid);
                return params;
            }
        };
        stringRequest.setTag("TAG");

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(150000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(stringRequest);
    }
}
