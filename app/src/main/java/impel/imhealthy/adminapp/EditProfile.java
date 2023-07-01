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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import impel.imhealthy.adminapp.HomeActivity.ProfileActivity;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;
import id.zelory.compressor.Compressor;

import static android.Manifest.permission_group.CAMERA;

public class EditProfile extends AppCompatActivity {

    ImageView back,image;
    TextView first_name,middle_name,last_name,dob,address,pincode,state,district;
    String imgs="";
    String imagePath;
    String gender;
    String userid;
    RequestQueue mRequestQueue;
    private static final int PERMISSION_REQUEST_CODE = 200;
    SessionManager session;
    Button btnupdate;
    private File actualImage, compressedImage;
    Bitmap bitmap;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }
        requestPermission();
        session = new SessionManager(EditProfile.this);
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        btnupdate = findViewById(R.id.btn_update);
        image = findViewById(R.id.image);
        first_name = findViewById(R.id.first_name);
        middle_name = findViewById(R.id.middle_name);
        last_name = findViewById(R.id.last_name);
        dob = findViewById(R.id.dob);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        state = findViewById(R.id.state);
        district = findViewById(R.id.district);
        
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent a = new Intent(EditProfile.this,ProfileActivity.class);
                startActivity(a);
                finish();
            }
        });
        image = findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
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
                                    photoURI = FileProvider.getUriForFile(EditProfile.this,
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
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (first_name.getText().toString().isEmpty()) {
                    first_name.setText("-");
                }
                if (middle_name.getText().toString().isEmpty()) {
                    middle_name.setText("-");

                }
                if (last_name.getText().toString().isEmpty()) {
                    last_name.setText("-");

                }
                if (dob.getText().toString().isEmpty()) {
                    dob.setText("-");

                }
                if (pincode.getText().toString().isEmpty()) {
                    pincode.setText("-");

                }
                if (state.getText().toString().isEmpty()) {
                    state.setText("-");

                }
                if (district.getText().toString().isEmpty()) {
                    district.setText("-");

                }
                if (address.getText().toString().isEmpty()) {
                    address.setText("-");
                }

                    //          Log.d("All Info :", first_name.getText().toString()+last_name.getText().toString()+dates+gname+address.getText().toString());
                    updatedata();
                    // startActivity(new Intent(AddDoctorFormActvity.this,ProfilePhotoActivity.class));

/*                if (first_name.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                } else if (middle_name.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "Enter Middle Number", Toast.LENGTH_SHORT).show();

                } else if (last_name.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "Enter Last Name", Toast.LENGTH_SHORT).show();

                } else if (dob.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "Enter DOB", Toast.LENGTH_SHORT).show();

                }else if (pincode.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "Enter PIN code", Toast.LENGTH_SHORT).show();

                }else if (state.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "Enter State", Toast.LENGTH_SHORT).show();

                }else if (district.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "Enter District", Toast.LENGTH_SHORT).show();

                }else if (address.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "Enter Address", Toast.LENGTH_SHORT).show();
                }
                else {
                    //          Log.d("All Info :", first_name.getText().toString()+last_name.getText().toString()+dates+gname+address.getText().toString());
                    updatedata();
                    // startActivity(new Intent(AddDoctorFormActvity.this,ProfilePhotoActivity.class));
                }*/
            }
        });

        getuserdetails();
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(EditProfile.this, new String[]
                {CAMERA, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }
    private void getuserdetails() {
        final ProgressDialog showMe = new ProgressDialog(EditProfile.this, AlertDialog.THEME_HOLO_LIGHT);
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

                                first_name.setText(namef);
                                middle_name.setText(namem);
                                last_name.setText(namel);
                                dob.setText(sdob);
                                gender=sgender;
                                address.setText(saddres);
                                pincode.setText(spin);
                                state.setText(sstate);
                                district.setText(sdistrict);

                                Glide.with(EditProfile.this).load(url).into(image);

                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(EditProfile.this, msg, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            showMe.dismiss();
                            Toast.makeText(EditProfile.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
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
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(EditProfile.this);
        // Adding the StringRequest object into requestQueue.

        requestQueue.add(stringRequest);
    }
    private void NetworkDialog() {
        final Dialog dialogs = new Dialog(EditProfile.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();

                getuserdetails();
            }
        });
        dialogs.show();
    }
    private void updatedata() {
        final ProgressDialog showMe = new ProgressDialog(EditProfile.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.update_user_profile,
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

                                Toast.makeText(EditProfile.this, msg, Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(EditProfile.this, ProfileActivity.class);
                                startActivity(intent);

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(EditProfile.this, msg, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            showMe.dismiss();
                            Toast.makeText(EditProfile.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {


                        NetworkDialogs();
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
                params.put("first_name", first_name.getText().toString());
                params.put("middle_name", middle_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("dob", dob.getText().toString());
                params.put("address", address.getText().toString());
                params.put("gender", gender);
                params.put("pincode", pincode.getText().toString());
                params.put("state", state.getText().toString());
                params.put("district", district.getText().toString());
                params.put("profile_image", imgs);

                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(EditProfile.this);
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(stringRequest);
    }
    private void NetworkDialogs() {
        final Dialog dialogs = new Dialog(EditProfile.this);
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.networkdialog);
        dialogs.setCanceledOnTouchOutside(false);
        Button done = (Button) dialogs.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();

                updatedata();
            }
        });
        dialogs.show();
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
                        .setQuality(50)
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
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
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

    @Override
    public void onBackPressed() {


        Intent a = new Intent(EditProfile.this,ProfileActivity.class);
        startActivity(a);
        finish();

        super.onBackPressed();
    }
}
