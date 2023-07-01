package impel.imhealthy.adminapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
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
import impel.imhealthy.adminapp.Model.DoctorModel;
import impel.imhealthy.adminapp.Model.MemberModel;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;
import id.zelory.compressor.Compressor;

import static android.Manifest.permission_group.CAMERA;

public class ProfilePhotoActivity extends AppCompatActivity {

    Button btn_profile;
    private File actualImage, compressedImage;
    String mCurrentPhotoPath;
    int Document = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Bitmap bitmap;
    String imgs="null";
    String imagePath;
    StringRequest stringRequest;
    String userid;
    RequestQueue mRequestQueue;
    SessionManager session;
    ImageView addmember,adddoctor;
    RecyclerView mRecyclerView1;
    MemberAdapter adapter;

    RecyclerView mRecyclerView2;
    DoctorAdapter adapter2;

    ArrayList<MemberModel> dm = new ArrayList<MemberModel>();
    ArrayList<DoctorModel> docm = new ArrayList<DoctorModel>();
    CircularImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo);
        requestPermission();
        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }

        mRecyclerView1 = (RecyclerView) findViewById(R.id.memberrecycler);
        mRecyclerView1.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView1.setLayoutManager(mLayoutManager1);
        mRecyclerView1.setItemAnimator(new DefaultItemAnimator());
        adapter = new MemberAdapter(getApplicationContext(), dm);
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
                startActivity(new Intent(ProfilePhotoActivity.this, FamilyInformationActivity.class));
            }
        });
        adddoctor = findViewById(R.id.adddoctor);
        adddoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePhotoActivity.this, AddDoctorFormActvity.class));
            }
        });
        image = findViewById(R.id.ProfilePicture);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePhotoActivity.this);
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
                                    photoURI = FileProvider.getUriForFile(ProfilePhotoActivity.this,
                                            "impel.imhealthy.adminapp", actualImage);
                                } else {
                                    photoURI = Uri.fromFile(actualImage);
                                }
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(intent, 1);
                            }
                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        }
                    }
                });
                builder.show();
            }
        });
        btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgs.equals("null") || imgs.equals("http://iamhealthy.androidapps.online/public/-")) {
                    Log.d("imgs url ::: ",imgs);
                    Toast.makeText(ProfilePhotoActivity.this, "Select Profile Image", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("imgs url ::: ",imgs);
                    //startActivity(new Intent(ProfilePhotoActivity.this,FamilyInformationActivity.class));
                    startActivity(new Intent(ProfilePhotoActivity.this,HomeScreen.class));
                    session.profile("profile");
                }
            }
        });
    }

    private void update_user_photo(final String imgsurl) {

        final ProgressDialog showMe = new ProgressDialog(ProfilePhotoActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();
        String url = Config.update_user_photo;
        mRequestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        showMe.dismiss();

                        try {
                            JSONObject j = new JSONObject(ServerResponse);

                            Log.d("server response: ",ServerResponse);

                            String status = j.getString("status");
                            String msg = j.getString("msg");

                            if (status.equals("200")) {

                                //      Toast.makeText(ProfilePhotoActivity.this, msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(ProfilePhotoActivity.this, msg, Toast.LENGTH_LONG).show();
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
                        error.printStackTrace();
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                15000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                        );
                        Log.d("Error", String.valueOf(error));
                        Toast.makeText(ProfilePhotoActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
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

                session.imgs(imgsurl);

                params.put("api_key", "salkdha645456q21362dbasdbmsd");
                params.put("user_id", userid);
                params.put("profile_image", imgsurl);
                return params;
            }
        };
        stringRequest.setTag("TAG");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        mRequestQueue.add(stringRequest);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                       /* bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                        img.setImageBitmap(bitmap);*/
                    if (requestCode == 1) {
                        customdocCompressImage();
                        update_user_photos(imgs);
                    }
                    if (resultCode == 2) {
                        Uri selectedImage = imageReturnedIntent.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getApplicationContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int index = cursor.getColumnIndex(filePathColumn[0]);
                        imagePath = cursor.getString(index);
                        cursor.close();
                        image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                        bitmap = BitmapFactory.decodeFile(imagePath);
                        imgs = getStringImage(bitmap);
                        update_user_photo(imgs);
                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    if (imageReturnedIntent != null) {

                        Uri filePath = imageReturnedIntent.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
//                            profile.setImageBitmap(bitmap);
                            Glide
                                    .with(this)
                                    .load(bitmap)
                                    .apply(new RequestOptions()
                                            .placeholder(R.drawable.addcircle)
                                            .fitCenter())
                                    .into(image);

                            imgs = getStringImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                break;
        }
    }

    public void customdocCompressImage() {
        if (actualImage == null) {
        } else {
            try {
                compressedImage = new Compressor(getApplicationContext())
                        .setMaxWidth(540)
                        .setMaxHeight(500)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .compressToFile(actualImage);

                setdocCompressedImage();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void setdocCompressedImage() {
        bitmap = BitmapFactory.decodeFile(compressedImage.getAbsolutePath());
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(actualImage);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            //   profile.setImageBitmap(bitmap);
            Glide
                    .with(this)
                    .load(bitmap)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.addcircle)
                            .fitCenter())
                    .into(image);
            imgs = getStringImage(bitmap);
            update_user_photo(imgs);

            Log.d("image base64 : ", imgs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        update_user_photo(encodedImage);
        return encodedImage;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ProfilePhotoActivity.this, new String[]
                {CAMERA, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void memlist() {
        final ProgressDialog showMe = new ProgressDialog(ProfilePhotoActivity.this, AlertDialog.THEME_HOLO_LIGHT);
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
                                JSONArray applist = j.getJSONArray("data");

                                if (applist != null && applist.length() > 0) {
                                    for (int i = 0; i < applist.length(); i++) {
                                        MemberModel model = new MemberModel();

                                        JSONObject jsonObject = applist.getJSONObject(i);

                                        model.setMembername(jsonObject.getString("first_name"));
                                        model.setMemberpic(jsonObject.getString("profile_image"));
                                        model.setId(jsonObject.getString("id"));

                                        dm.add(model);
                                        adapter = new MemberAdapter(getApplicationContext(), dm);      //ds=model       d=Model
                                        mRecyclerView1.setAdapter(adapter);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void doclist() {
        final ProgressDialog showMe = new ProgressDialog(ProfilePhotoActivity.this, AlertDialog.THEME_HOLO_LIGHT);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void NetworkDialog() {
        final Dialog dialogs = new Dialog(ProfilePhotoActivity.this);
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
        final ProgressDialog showMe = new ProgressDialog(ProfilePhotoActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://imyudhi.com/dhani/api/get_user_details",
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

                                if (j.getString("profile_image").equals("http://imyudhi.com/dhani/public/-"))
                                {
                                    Log.d("pimg if:",j.getString("profile_image"));
                                    image.setImageResource(R.drawable.addcircle);
                                }
                                else {
                                    String url = j.getString("profile_image");
                                    Log.d("pimg else:",url);

                                    imgs = j.getString("profile_image");
                                    Glide.with(getApplicationContext()).load(url).into(image);
                                }
                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                //   Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            showMe.dismiss();
                            //     Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
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
                            //         NetworkDialog();
                            if (getFragmentManager().getBackStackEntryCount() != 0) {
                                getFragmentManager().popBackStack();
                            }

                        } else {
                            // Is thrown if there's no network connection or server is down
                            Log.d("eerror", String.valueOf(volleyError));
                            //         NetworkDialog();
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
    private void update_user_photos(final String imgsurl) {

        final ProgressDialog showMe = new ProgressDialog(ProfilePhotoActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();
        String url = Config.update_user_photo;
        mRequestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        showMe.dismiss();

                        try {
                            JSONObject j = new JSONObject(ServerResponse);

                            Log.d("server response: ",ServerResponse);

                            String status = j.getString("status");
                            String msg = j.getString("msg");

                            if (status.equals("200")) {

                                //            startActivity(new Intent(ProfilePhotoActivity.this,HomeScreen.class));
                                //      Toast.makeText(ProfilePhotoActivity.this, msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(ProfilePhotoActivity.this, msg, Toast.LENGTH_LONG).show();
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
                        error.printStackTrace();
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                15000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                        );
                        Log.d("Error", String.valueOf(error));
                        Toast.makeText(ProfilePhotoActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
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
                params.put("profile_image", imgsurl);
                return params;
            }
        };
        stringRequest.setTag("TAG");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        mRequestQueue.add(stringRequest);

    }
}
