package impel.imhealthy.adminapp;

import android.Manifest;
import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import impel.imhealthy.adminapp.HomeActivity.ProfileActivity;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;
import id.zelory.compressor.Compressor;

import static android.Manifest.permission_group.CAMERA;

public class AddDoctorFormActivityNew extends AppCompatActivity {

    EditText first_name, middle_name, last_name, dates, address, mobile_number;
    Button btn_submit;
    private File actualImage, compressedImage;
    String mCurrentPhotoPath;
    int Document = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Bitmap bitmap;
    String imgs;
    String imagePath;
    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    SessionManager session;
    String userid;
    String demodate = "";
    ImageView image;
    String gid, gname = "null", coid = "null", ciid = "null", Date;
    Spinner gender, spncountry, city;
    ArrayList<String> gendername = new ArrayList<String>();
    ArrayList<String> genderid = new ArrayList<String>();
    Button btn_pin_submit;
    ImageView back;
    EditText doctor_name, mobile, hospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor_form_new);

        requestPermission();
        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }
        doctor_name = findViewById(R.id.doctor_name);
        mobile = findViewById(R.id.mobile);
        hospital = findViewById(R.id.hospital);
        address = findViewById(R.id.address);
        back = findViewById(R.id.back);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddDoctorFormActivityNew.this, android.R.layout.simple_list_item_1, gendername);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!gendername.get(position).equals("Select Gender")) {
                    gid = genderid.get(position);
                    if (gid == "1") {
                        gname = "Male";
                    }
                    if (gid == "2") {
                        gname = "Female";
                    }
                    if (gid == "3") {
                        gname = "Other";
                    }

                    Log.d("cid", gid);

                } else {
                    Toast.makeText(AddDoctorFormActivityNew.this, "Select your Gender", Toast.LENGTH_SHORT);
                }
//                if(gendername.get(position).equals("Select Gender")){
//
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        image = findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddDoctorFormActivityNew.this);
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
                                    photoURI = FileProvider.getUriForFile(AddDoctorFormActivityNew.this,
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
        btn_submit = findViewById(R.id.btn_pin_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             /*if (imgs == null) {
                    Toast.makeText(AddDoctorFormActivityNew.this, "Select Image", Toast.LENGTH_SHORT).show();
                } else*/
                {
                    //          Log.d("All Info :", first_name.getText().toString()+last_name.getText().toString()+dates+gname+address.getText().toString());
                    if (doctor_name.getText().toString().isEmpty()) {
                        doctor_name.setText("-");
                    }
                    if (mobile.getText().toString().isEmpty()) {
                        mobile.setText("-");

                    }
                    if (hospital.getText().toString().isEmpty()) {
                        hospital.setText("-");

                    }
                    if (address.getText().toString().isEmpty()) {
                        address.setText("-");

                    }
                    if (gname == "null") {
                        gname = "-";
                    }
                    if (imgs == null) {
                        imgs = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5OjcBCgoKDQwNGg8PGjclHyU3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3N//AABEIAIsAiwMBIgACEQEDEQH/xAAbAAEAAgMBAQAAAAAAAAAAAAAABAUBAgMGB//EADUQAAICAAQEBAQDCAMAAAAAAAABAgMEBREhEjFBUSIyYXETUoGRQoLRFTRTYnKhsfAUIzP/xAAWAQEBAQAAAAAAAAAAAAAAAAAAAQL/xAAYEQEBAQEBAAAAAAAAAAAAAAAAAREhMf/aAAwDAQACEQMRAD8A+4gAAAAAAAAwayurh57Ir3YG4OH/AC8P/Gh9zaOJolyug/zAdQYTTWqexkAAAAAAAAAAAABpbZGqDlN6JAbNpLVvRepBxGZQi3GpcT+boQsXi54h6LWNfSP6kYuDtbirrfPY/ZbI5GAVAABG8LJ1vWE5R9mTKMynDRXLjXdbMgGQr0FN0Lo8Vck1/dHQ87XZKqanW2pLqXGDxccQtHtYua7+qJYqUACAAAAAAw3otXyKTG4l4ixpPSEeS7+pNzW9wrVUH4pc/YqSwAAVlkm4bL5WJTtfBF8l1ZpltCuv1lvGG/1LolWIqy/DJeRv8zON+Wwa1pk4vs90WAIrztlcqpuE46NGhc5lQrKONeaG/wBCmNIG0JOElKL0a3TNQBfYTELEVKXKS2kvU7lFgb/gXxbfhltIvSVQAEAA0ulwUzl2i2BR4uz4uJnP10XscR0BpkAAFnk2nDb31RZFJl96pv8AFtGWz9C7JWgAEHPEafAs15cD/wAHni4zO9Qp+GvNP+yKcsSgAKgy9wNvxcNCT5rZ/Qoi0yeXgsh2af8Av2FVYgAyocMb+62/0nc5YqPFhrV/KwPPgLdA0yBvQBrUKxqTcLmMqkoWJzguXdHCjC23r/rjt8z5Ev8AZMuHVWri7abASY5nhn+KS/Kcb81glpTBt95dCO8rv7x+jOkMqse05xivTcnFQJ2SnOUpycpPqNUS78ttrTcfGvTn9iI4ac00/UqMmAtkABYZP/62/wBKK8ssnjvbL2QoswAZUMNJpp8mZAHnbYOu2cH0ehoT81p4bValtLZ+5ANIFjgcDxJWXrbpHucstoV1vFPyw307suSWjCSS0WyMgEUAAAi4rB14ha6KM/mJQA87bXOqbhYtGjQucxw8baXNaKUFrr3RTGkC5yuvgw2vzPUqaa3bbGuPOTPQQioQUYrRJaJEpGwAIoAAOeIqjdVKuXJr7MobISqm4TXiT3PREXHYRYiGsdFYls+/oyyiootnTYpwe66dGXeGxEMRDWPNc49iilCUJOMk1Jc0xGUoSUoScWuqLiPRgrKMz02vjr/NH9CZXi6LPLZH2exlXcGqnF8pJ/U1nfVDz2RXuwOhiUlGLlJpJdWQrsyqjtWnN/ZFdiMTbe/HLw9IrkXB3x+Md+sK9VWub7kIE/AYP4jVlq8HNRfUviO2WYZwh8aa8UvKuyLAAyoAAAAAAADhicLXiI+JaSXKS5oqcRg7aG9YuUfmRegujzQL23B0WvVwSfeOxGnlUfwWte61LqKsFh+y5/xY/Y2jlT/Fb9ojTqtN66p2y4a4uT9C2ry6iG74pv1ZKhCMIqMEkl0RNEHC5fGDU7vFJco9EWABFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH/2Q==";
                    }
                    personalinfo();
                    // startActivity(new Intent(AddDoctorFormActivityNew.this,ProfilePhotoActivity.class));
                }
            }
        });
    }

    private void personalinfo() {

        final ProgressDialog showMe = new ProgressDialog(AddDoctorFormActivityNew.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();
        String url = Config.add_doctor;
        mRequestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        showMe.dismiss();

                        Log.d("server personal info: ", ServerResponse);
                        try {
                            JSONObject j = new JSONObject(ServerResponse);

                            String status = j.getString("status");
                            String msg = j.getString("msg");

                            if (status.equals("200")) {

                                startActivity(new Intent(AddDoctorFormActivityNew.this, ProfileActivity.class));
                                Toast.makeText(AddDoctorFormActivityNew.this, msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(AddDoctorFormActivityNew.this, msg, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(AddDoctorFormActivityNew.this, "Week Internet Connection", Toast.LENGTH_SHORT).show();
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
                params.put("doctor_name", doctor_name.getText().toString());
                params.put("mobile", mobile.getText().toString());
                params.put("gender", gname);
                params.put("hospital", hospital.getText().toString());
                params.put("address", address.getText().toString());
                params.put("doctor_image", imgs);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(AddDoctorFormActivityNew.this, new String[]
                {CAMERA, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }
}

