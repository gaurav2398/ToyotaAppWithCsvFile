package impel.imhealthy.adminapp.HomeActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import impel.imhealthy.adminapp.Adapter.DoctorAdapter;
import impel.imhealthy.adminapp.Adapter.MemberAdapter;
import impel.imhealthy.adminapp.AddDoctorFormActivityNew;
import impel.imhealthy.adminapp.EditProfile;
import impel.imhealthy.adminapp.FamilyMemberAcitivtyNew;
import impel.imhealthy.adminapp.HomeScreen;
import impel.imhealthy.adminapp.Model.DoctorModel;
import impel.imhealthy.adminapp.Model.MemberModel;
import impel.imhealthy.adminapp.R;
import impel.imhealthy.adminapp.Timelines.TimeLineActivity;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;


import static android.Manifest.permission_group.CAMERA;

public class ProfileActivity extends AppCompatActivity {

    Button btn_profile;
    private File actualImage, compressedImage;
    String mCurrentPhotoPath;
    int Document = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Bitmap bitmap;
    String imgs;
    String imagePath;
    StringRequest stringRequest;
    String userid;
    RequestQueue mRequestQueue;
    SessionManager session;
    ImageView addmember, back, edit, adddoctor;
    RecyclerView mRecyclerView1;
    MemberAdapter adapter;
    CircularImageView image;
    ArrayList<MemberModel> dm = new ArrayList<MemberModel>();
    TextView name, dob, gender, address, pincode, state, district, establishmentname, establishmentaddress;
    LinearLayout home, history, profile, setting;
    RecyclerView mRecyclerView2;
    DoctorAdapter adapter2;

    ArrayList<DoctorModel> docm = new ArrayList<DoctorModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(ProfileActivity.this, R.color.white));// set status backgrou
        }
        edit = findViewById(R.id.edit);
        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        profile = findViewById(R.id.profile);
        setting = findViewById(R.id.setting);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeScreen.class);
                startActivity(intent);
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, TimeLineActivity.class);
                startActivity(intent);
            }
        });
        requestPermission();
        session = new SessionManager(ProfileActivity.this);
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        establishmentname = findViewById(R.id.establishmentname);
        establishmentaddress = findViewById(R.id.establishmentaddress);
        establishmentname.setText(users.get(session.KEY_ENAME));
        establishmentaddress.setText(users.get(session.KEY_EADD));

        name = findViewById(R.id.name);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        state = findViewById(R.id.state);
        district = findViewById(R.id.district);
        image = findViewById(R.id.profileimg);

        mRecyclerView1 = (RecyclerView) findViewById(R.id.memberrecycler);
        mRecyclerView1.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView1.setLayoutManager(mLayoutManager1);
        mRecyclerView1.setItemAnimator(new DefaultItemAnimator());
        adapter = new MemberAdapter(ProfileActivity.this, dm);
        mRecyclerView1.setAdapter(adapter);

        mRecyclerView2 = (RecyclerView) findViewById(R.id.doctorrecycler);
        mRecyclerView2.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView2.setLayoutManager(mLayoutManager2);
        mRecyclerView2.setItemAnimator(new DefaultItemAnimator());
        adapter2 = new DoctorAdapter(getApplicationContext(), docm);
        mRecyclerView2.setAdapter(adapter2);


        memlist();
        doclist();
        getuserdetails();
        addmember = findViewById(R.id.addmember);
        addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, FamilyMemberAcitivtyNew.class));
            }
        });
        adddoctor = findViewById(R.id.adddoctor);
        adddoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AddDoctorFormActivityNew.class));
            }
        });
        image = findViewById(R.id.profileimg);
 /*       image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            try {
                                actualImage = createImageFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                                actualImage = null;
                                mCurrentPhotoPath = null;
                            }
                            if (actualImage != null) {
                                Uri photoURI = null;
                                if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)) {
                                    photoURI = FileProvider.getUriForFile(ProfileActivity.this,
                                            "gaurav.project.admindhani", actualImage);
                                } else {
                                    photoURI = Uri.fromFile(actualImage);
                                }
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(intent, 1);
                            }
                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });*/

    }

    private void memlist() {
        final ProgressDialog showMe = new ProgressDialog(ProfileActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.get_members,
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
                                JSONArray applist1 = j.getJSONArray("data");

                                for (int i = 0; i < applist1.length(); i++) {

                                    MemberModel model = new MemberModel();

                                    JSONObject jsonObject = applist1.getJSONObject(i);

                                    model.setMembername(jsonObject.getString("first_name") + " " + jsonObject.getString("middle_name") + " " + jsonObject.getString("last_name"));
                                    model.setMemberpic(jsonObject.getString("profile_image"));
                                    model.setId(jsonObject.getString("id"));

                                    dm.add(model);
                                    adapter = new MemberAdapter(ProfileActivity.this, dm);      //ds=model       d=Model
                                    mRecyclerView1.setAdapter(adapter);
                                }


                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            showMe.dismiss();
                            Toast.makeText(ProfileActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
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

                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void doclist() {
        final ProgressDialog showMe = new ProgressDialog(ProfileActivity.this, AlertDialog.THEME_HOLO_LIGHT);
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
                                docm.clear();
                                JSONArray applist = j.getJSONArray("data");

                                if (applist != null && applist.length() > 0) {
                                    for (int i = 0; i < applist.length(); i++) {
                                        DoctorModel model = new DoctorModel();

                                        JSONObject jsonObject = applist.getJSONObject(i);

                                        model.setMembername(jsonObject.getString("doctor_name"));
                                        model.setMemberpic(jsonObject.getString("image"));
                                        model.setId(jsonObject.getString("id"));

//                                        Log.d("dc image",jsonObject.getString("doctor_image"));


                                        docm.add(model);
                                        adapter2 = new DoctorAdapter(getApplicationContext(), docm);      //ds=model       d=Model
                                        mRecyclerView2.setAdapter(adapter2);
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

                        Log.d("eerror", String.valueOf(volleyError));
                        NetworkDialog();

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

    private void NetworkDialog() {
        final Dialog dialogs = new Dialog(ProfileActivity.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();

                memlist();
            }
        });
        dialogs.show();
    }

    private void getuserdetails() {
        final ProgressDialog showMe = new ProgressDialog(ProfileActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.get_user_details,
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

                                String namef = j.getString("first_name");
                                String namem = j.getString("middle_name");
                                String namel = j.getString("last_name");
                                String sdob = j.getString("dob");
                                String sgender = j.getString("gender");
                                String saddres = j.getString("address");
                                String spin = j.getString("pincode");
                                String sstate = j.getString("state");
                                String sdistrict = j.getString("district");
                                String url = j.getString("profile_image");

                                String input = namef + " " + namem + " " + namel;
                                input = input.replace("-", "");
                                name.setText(input);

                                dob.setText(sdob);
                                if (dob.getText().toString().equals("1970-01-01")) {
                                    dob.setText("");
                                }
                                gender.setText(sgender);
                                address.setText(saddres);
                                pincode.setText(spin);
                                state.setText(sstate);
                                district.setText(sdistrict);

                                Glide.with(ProfileActivity.this).load(url).into(image);

                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            showMe.dismiss();
                            Toast.makeText(ProfileActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
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

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void setdocCompressedImage() {
        bitmap = BitmapFactory.decodeFile(compressedImage.getAbsolutePath());
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(actualImage);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            //   profile.setImageBitmap(bitmap);
            Glide
                    .with(this)
                    .load(bitmap)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.addcircle)
                            .fitCenter())
                    .into(image);
            imgs = getStringImage(bitmap);

            Log.d("image base64 : ", imgs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = ProfileActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]
                {CAMERA, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {

        Intent a = new Intent(ProfileActivity.this, HomeScreen.class);
        startActivity(a);

        super.onBackPressed();
    }
}
