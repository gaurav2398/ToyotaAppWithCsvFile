package impel.imhealthy.adminapp;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;
import id.zelory.compressor.Compressor;

import static android.Manifest.permission_group.CAMERA;

public class EstablishmenFormActivity extends AppCompatActivity {

    EditText es_name,es_type,es_add;
    ImageView image;
    RequestQueue mRequestQueue;
    SessionManager session;
    Button btn_submit;
    private File actualImage, compressedImage;
    String mCurrentPhotoPath;
    int Document = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Bitmap bitmap;
    String imgs;
    String imagePath;
    StringRequest stringRequest;
    String userid;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishmen_form);

        requestPermission();

        es_name = findViewById(R.id.es_name);
        es_type = findViewById(R.id.es_type);
        es_add = findViewById(R.id.es_address);
        requestPermission();

        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        Log.d("User Id: ", userid);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD4"));
        }

        image = findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(EstablishmenFormActivity.this);
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
                                    photoURI = FileProvider.getUriForFile(EstablishmenFormActivity.this,
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

                /*if (es_name.getText().toString().isEmpty()) {
                    Toast.makeText(EstablishmenFormActivity.this, "Enter Establishment Name", Toast.LENGTH_SHORT).show();
                } else if (es_type.getText().toString().isEmpty()) {
                    Toast.makeText(EstablishmenFormActivity.this, "Enter Establishment Type", Toast.LENGTH_SHORT).show();

                } else if (es_add.getText().toString().isEmpty()) {
                    Toast.makeText(EstablishmenFormActivity.this, "Enter Establishment Address", Toast.LENGTH_SHORT).show();

                }*/

                    if (imgs == null) {
                        imgs="/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5OjcBCgoKDQwNGg8PGjclHyU3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3N//AABEIAIsAiwMBIgACEQEDEQH/xAAbAAEAAgMBAQAAAAAAAAAAAAAABAUBAgMGB//EADUQAAICAAQEBAQDCAMAAAAAAAABAgMEBREhEjFBUSIyYXETUoGRQoLRFTRTYnKhsfAUIzP/xAAWAQEBAQAAAAAAAAAAAAAAAAAAAQL/xAAYEQEBAQEBAAAAAAAAAAAAAAAAAREhMf/aAAwDAQACEQMRAD8A+4gAAAAAAAAwayurh57Ir3YG4OH/AC8P/Gh9zaOJolyug/zAdQYTTWqexkAAAAAAAAAAAABpbZGqDlN6JAbNpLVvRepBxGZQi3GpcT+boQsXi54h6LWNfSP6kYuDtbirrfPY/ZbI5GAVAABG8LJ1vWE5R9mTKMynDRXLjXdbMgGQr0FN0Lo8Vck1/dHQ87XZKqanW2pLqXGDxccQtHtYua7+qJYqUACAAAAAAw3otXyKTG4l4ixpPSEeS7+pNzW9wrVUH4pc/YqSwAAVlkm4bL5WJTtfBF8l1ZpltCuv1lvGG/1LolWIqy/DJeRv8zON+Wwa1pk4vs90WAIrztlcqpuE46NGhc5lQrKONeaG/wBCmNIG0JOElKL0a3TNQBfYTELEVKXKS2kvU7lFgb/gXxbfhltIvSVQAEAA0ulwUzl2i2BR4uz4uJnP10XscR0BpkAAFnk2nDb31RZFJl96pv8AFtGWz9C7JWgAEHPEafAs15cD/wAHni4zO9Qp+GvNP+yKcsSgAKgy9wNvxcNCT5rZ/Qoi0yeXgsh2af8Av2FVYgAyocMb+62/0nc5YqPFhrV/KwPPgLdA0yBvQBrUKxqTcLmMqkoWJzguXdHCjC23r/rjt8z5Ev8AZMuHVWri7abASY5nhn+KS/Kcb81glpTBt95dCO8rv7x+jOkMqse05xivTcnFQJ2SnOUpycpPqNUS78ttrTcfGvTn9iI4ac00/UqMmAtkABYZP/62/wBKK8ssnjvbL2QoswAZUMNJpp8mZAHnbYOu2cH0ehoT81p4bValtLZ+5ANIFjgcDxJWXrbpHucstoV1vFPyw307suSWjCSS0WyMgEUAAAi4rB14ha6KM/mJQA87bXOqbhYtGjQucxw8baXNaKUFrr3RTGkC5yuvgw2vzPUqaa3bbGuPOTPQQioQUYrRJaJEpGwAIoAAOeIqjdVKuXJr7MobISqm4TXiT3PREXHYRYiGsdFYls+/oyyiootnTYpwe66dGXeGxEMRDWPNc49iilCUJOMk1Jc0xGUoSUoScWuqLiPRgrKMz02vjr/NH9CZXi6LPLZH2exlXcGqnF8pJ/U1nfVDz2RXuwOhiUlGLlJpJdWQrsyqjtWnN/ZFdiMTbe/HLw9IrkXB3x+Md+sK9VWub7kIE/AYP4jVlq8HNRfUviO2WYZwh8aa8UvKuyLAAyoAAAAAAADhicLXiI+JaSXKS5oqcRg7aG9YuUfmRegujzQL23B0WvVwSfeOxGnlUfwWte61LqKsFh+y5/xY/Y2jlT/Fb9ojTqtN66p2y4a4uT9C2ry6iG74pv1ZKhCMIqMEkl0RNEHC5fGDU7vFJco9EWABFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH/2Q==";
                    }
                    //          Log.d("All Info :", first_name.getText().toString()+last_name.getText().toString()+dates+gname+address.getText().toString());
                    if (es_name.getText().toString().isEmpty()) {
                        es_name.setText("-");
                    }
                    if (es_type.getText().toString().isEmpty()) {
                        es_type.setText("-");

                    }
                    if (es_add.getText().toString().isEmpty()) {
                        es_add.setText("-");

                    }
                    establishmentinfo();
                    // startActivity(new Intent(gaurav.project.admindhani.EstablishmenFormActivity.this,ProfilePhotoActivity.class));
                }

        });
    }
    private void establishmentinfo() {

        final ProgressDialog showMe = new ProgressDialog(EstablishmenFormActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();
        String url = Config.update_est_details;
        mRequestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onResponse(String ServerResponse) {
                        showMe.dismiss();

                        Log.d("server personal info: ", ServerResponse);
                        try {
                            JSONObject j = new JSONObject(ServerResponse);

                            String status = j.getString("status");
                            String msg = j.getString("msg");

                            if (status.equals("200")) {
                             /*   FragmentManager manager = getSupportFragmentManager();
                                manager.beginTransaction().replace(R.layout.fragment_profile, new Profile()).commit();*/
                             session.createEstablishmentSession(es_name.getText().toString(),es_add.getText().toString());
                                Intent intent = new Intent(EstablishmenFormActivity.this, ProfilePhotoActivity.class);
                                startActivity(intent);


                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(EstablishmenFormActivity.this, msg, Toast.LENGTH_LONG).show();
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
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                15000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                        NetworkDialog();
                        Log.d("Error", String.valueOf(error));
                        Toast.makeText(EstablishmenFormActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
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
                params.put("establishment_name", es_name.getText().toString());
                params.put("establishment_type", es_type.getText().toString());
                params.put("establishment_address", es_add.getText().toString());
                params.put("establishment_image", imgs);

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
        return encodedImage;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(EstablishmenFormActivity.this, new String[]
                {CAMERA, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }
}
