package impel.imhealthy.adminapp.HomeActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import impel.imhealthy.adminapp.HomeScreen;
import impel.imhealthy.adminapp.R;
import impel.imhealthy.adminapp.Timelines.TimeLineActivity;
import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class SymptomFormActivity extends AppCompatActivity {


    LinearLayout home, history, profile, setting, llinstagram, llwhatsapp, llmessage, llemail;
    RadioButton rFever, rCough, rShortness, rHigh, rDiabetes, rHypertension, rLung, rHeart;
    String sfever = " ", scough = " ", sshortness = " ", shigh = " ", sdiabetes = " ", shypertension = " ", slung = " ", sheart = " ";
    int fever = 0, cough = 0, shortness = 0, high = 0, diabetes = 0, hypertension = 0, lung = 0, heart = 0;
    Button btnshare,btnsumbit;
    String sym = "", his = "", fullstr = "", fullstr2 = "";
    ImageView back, sms;
    String name, mobno, city, pincode;
    StringRequest stringRequest;
    String userid;
    RequestQueue mRequestQueue;
    SessionManager session;
    BottomSheetDialog bottomSheetDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_form);
        checkIfAlreadyhavePermission();

        session = new SessionManager(SymptomFormActivity.this);
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        Log.d("uid", userid);

        getuserdetails();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent a = new Intent(SymptomFormActivity.this, HomeScreen.class);
                startActivity(a);
                finish();

            }
        });
        btnshare = findViewById(R.id.btnshare);
        btnsumbit= findViewById(R.id.btnsumbit);
        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        profile = findViewById(R.id.profile);
        setting = findViewById(R.id.setting);
        rFever = findViewById(R.id.rFever);
        rCough = findViewById(R.id.rcough);
        rShortness = findViewById(R.id.rShortness);
        rHigh = findViewById(R.id.rBp);
        rDiabetes = findViewById(R.id.rDiabetes);
        rHypertension = findViewById(R.id.rHypertension);
        rLung = findViewById(R.id.rLung);
        rHeart = findViewById(R.id.rHeart);

        rFever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fever == 0) {
                    sfever = "Fever, ";
                    rFever.setChecked(true);
                    //             Toast.makeText(SymptomFormActivity.this, ""+sfever, Toast.LENGTH_SHORT).show();
                    fever = 1;
                } else {
                    sfever = "";
                    rFever.setChecked(false);
                    //             Toast.makeText(SymptomFormActivity.this, ""+sfever, Toast.LENGTH_SHORT).show();
                    fever = 0;
                    sfever = " ";
                }
            }
        });
        rCough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cough == 0) {
                    scough = "Cough & Cold, ";
                    rCough.setChecked(true);
                    //             Toast.makeText(SymptomFormActivity.this, ""+scough, Toast.LENGTH_SHORT).show();
                    cough = 1;
                } else {
                    scough = " ";
                    rCough.setChecked(false);
                    //           Toast.makeText(SymptomFormActivity.this, ""+scough, Toast.LENGTH_SHORT).show();
                    cough = 0;

                }
            }
        });
        rShortness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (shortness == 0) {
                    sshortness = "Shortness of Breath, ";
                    rShortness.setChecked(true);
                    //           Toast.makeText(SymptomFormActivity.this, ""+sshortness, Toast.LENGTH_SHORT).show();
                    shortness = 1;
                } else {
                    sshortness = " ";
                    rShortness.setChecked(false);
                    //           Toast.makeText(SymptomFormActivity.this, ""+sshortness, Toast.LENGTH_SHORT).show();
                    shortness = 0;

                }
            }
        });
        rHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (high == 0) {
                    shigh = "High Blood Pressure.";
                    rHigh.setChecked(true);
                    //              Toast.makeText(SymptomFormActivity.this, ""+shigh, Toast.LENGTH_SHORT).show();
                    high = 1;
                } else {
                    shigh = " ";
                    rHigh.setChecked(false);
                    //               Toast.makeText(SymptomFormActivity.this, ""+shigh, Toast.LENGTH_SHORT).show();
                    high = 0;
                }
            }
        });
        rDiabetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (diabetes == 0) {
                    sdiabetes = "Diabetes, ";
                    rDiabetes.setChecked(true);
                    //              Toast.makeText(SymptomFormActivity.this, ""+sdiabetes, Toast.LENGTH_SHORT).show();
                    diabetes = 1;
                } else {
                    sdiabetes = "";
                    rDiabetes.setChecked(false);
                    //             Toast.makeText(SymptomFormActivity.this, ""+sdiabetes, Toast.LENGTH_SHORT).show();
                    diabetes = 0;
                }
            }
        });
        rHypertension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hypertension == 0) {
                    shypertension = "Hypertension, ";
                    rHypertension.setChecked(true);
                    //              Toast.makeText(SymptomFormActivity.this, ""+shypertension, Toast.LENGTH_SHORT).show();
                    hypertension = 1;
                } else {
                    shypertension = "";
                    rHypertension.setChecked(false);
                    //              Toast.makeText(SymptomFormActivity.this, ""+shypertension, Toast.LENGTH_SHORT).show();
                    hypertension = 0;
                }
            }
        });
        rLung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lung == 0) {
                    slung = "Lung Disease";
                    rLung.setChecked(true);
                    //              Toast.makeText(SymptomFormActivity.this, ""+slung, Toast.LENGTH_SHORT).show();
                    lung = 1;
                } else {
                    shypertension = "";
                    rLung.setChecked(false);
                    //              Toast.makeText(SymptomFormActivity.this, ""+slung, Toast.LENGTH_SHORT).show();
                    lung = 0;
                }
            }
        });
        rHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (heart == 0) {
                    sheart = "Heart Disease.";
                    rHeart.setChecked(true);
                    //             Toast.makeText(SymptomFormActivity.this, ""+sheart, Toast.LENGTH_SHORT).show();
                    heart = 1;
                } else {
                    sheart = "";
                    rHeart.setChecked(false);
                    //              Toast.makeText(SymptomFormActivity.this, ""+sheart, Toast.LENGTH_SHORT).show();
                    heart = 0;
                }
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SymptomFormActivity.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SymptomFormActivity.this, TimeLineActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SymptomFormActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(SymptomFormActivity.this, TimeLineActivity.class);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SymptomFormActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sfever.equals(" ")) {
                    fullstr = "Symptoms:" + "\n" + "\n" + scough + sshortness + shigh;
                } else {
                    fullstr = "Symptoms:" + "\n" + "\n" + sfever + scough + sshortness + shigh;
                }

                if (sdiabetes.equals(" ")) {
                    fullstr2 = "History:" + "\n" + "\n" + shypertension + slung + sheart;
                } else {
                    fullstr2 = "History:" + "\n" + "\n" + sdiabetes + shypertension + slung + sheart;
                }

                if (sfever.equals(" ") & scough.equals(" ") & sshortness.equals(" ") & shigh.equals(" ")) {
                    fullstr = "";
                }
                if (sdiabetes.equals(" ") & shypertension.equals(" ") & slung.equals(" ") & sheart.equals(" ")) {
                    fullstr2 = "";
                }

                if (sfever.equals(" ") & scough.equals(" ") & sshortness.equals(" ") & shigh.equals(" ") & sdiabetes.equals(" ") & shypertension.equals(" ") & slung.equals(" ") & sheart.equals(" ") & sdiabetes.equals(" ") & shypertension.equals(" ") & slung.equals(" ") & sheart.equals(" ")) {
                    Toast.makeText(SymptomFormActivity.this, "Select data from\nSymptoms or History", Toast.LENGTH_LONG).show();
                } else {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        checkIfAlreadyhavePermission();
                    }
                    bottomSheetDialog = new BottomSheetDialog(SymptomFormActivity.this, R.style.qrcode);
                    bottomSheetDialog.setContentView(R.layout.bottomsheetsharingdetails);
                    bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    bottomSheetDialog.setCancelable(true);
                    bottomSheetDialog.show();

                    llinstagram = bottomSheetDialog.findViewById(R.id.llinstagram);
                    llwhatsapp = bottomSheetDialog.findViewById(R.id.llwhatsapp);
                    llmessage = bottomSheetDialog.findViewById(R.id.llmessage);
                    llemail = bottomSheetDialog.findViewById(R.id.llemail);

                    Bitmap bitmap = takeScreenshot();
                    saveBitmap(bitmap);

                    llinstagram.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isAppInstalled = appInstalledOrNot("com.instagram.android");
                            if (isAppInstalled) {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.setType("image/*");
                                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                shareIntent.putExtra(Intent.EXTRA_TEXT, "\n\n" + name + "\n" + city + " (" + pincode + ")" + "\n" + mobno + "\n" + "\n" + "Via I'm Healthy Application");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "screenshot.png"));
                                shareIntent.setPackage("com.instagram.android");
                                startActivity(shareIntent);

                            } else {

                                Toast.makeText(SymptomFormActivity.this, "Application is not currently installed", Toast.LENGTH_SHORT).show();
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

                                shareIntent.putExtra(Intent.EXTRA_TEXT, "\n\n" + name + "\n" + city + " (" + pincode + ")" + "\n" + mobno + "\n" + "\n" + "Via I'm Healthy Application");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "screenshot.png"));
                                shareIntent.setPackage("com.whatsapp");
                                startActivity(shareIntent);

                            } else {
                                // Do whatever we want to do if application not installed
                                // For example, Redirect to play store

                                Toast.makeText(SymptomFormActivity.this, "Application is not currently installed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    llemail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Bitmap bitmap = takeScreenshot();
                            saveBitmap(bitmap);

                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                            Intent share = new Intent(Intent.ACTION_SEND);

                            share.setType("text/plain");
                            share.setType("image/*");
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                                    b, "Title", null);
                            Uri imageUri = Uri.parse(path);

                            share.putExtra(Intent.EXTRA_TEXT, "\n\n" + name + "\n" + city + " (" + pincode + ")" + "\n" + mobno + "\n" + "\n" + "Via I'm Healthy Application");
                            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "screenshot.png"));
                            startActivity(Intent.createChooser(share, "Select"));
                        }
                    });

                    llmessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("smsto:"));
                                i.setType("vnd.android-dir/mms-sms");
                                i.putExtra("address", new String(""));
                                i.putExtra("sms_body", fullstr + "\n\n-------------------------------------\n\n" + fullstr2 + "\n\n\n" + "\n\n" + name + "\n" + city + " (" + pincode + ")" + "\n" + mobno + "\n" + "Via I'm Healthy Application");
                                startActivity(Intent.createChooser(i, "Send sms via:"));
                            } catch (Exception e) {
                                Toast.makeText(SymptomFormActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
        btnsumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sfever.equals(" ")) {
                    fullstr = "Symptoms:" + "\n" + "\n" + scough + sshortness + shigh;
                } else {
                    fullstr = "Symptoms:" + "\n" + "\n" + sfever + scough + sshortness + shigh;
                }

                if (sdiabetes.equals(" ")) {
                    fullstr2 = "History:" + "\n" + "\n" + shypertension + slung + sheart;
                } else {
                    fullstr2 = "History:" + "\n" + "\n" + sdiabetes + shypertension + slung + sheart;
                }

                if (sfever.equals(" ") & scough.equals(" ") & sshortness.equals(" ") & shigh.equals(" ")) {
                    fullstr = "";
                }
                if (sdiabetes.equals(" ") & shypertension.equals(" ") & slung.equals(" ") & sheart.equals(" ")) {
                    fullstr2 = "";
                }

                if (sfever.equals(" ") & scough.equals(" ") & sshortness.equals(" ") & shigh.equals(" ") & sdiabetes.equals(" ") & shypertension.equals(" ") & slung.equals(" ") & sheart.equals(" ") & sdiabetes.equals(" ") & shypertension.equals(" ") & slung.equals(" ") & sheart.equals(" ")) {
                    Toast.makeText(SymptomFormActivity.this, "Select data from\nSymptoms or History", Toast.LENGTH_LONG).show();
                } else {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        checkIfAlreadyhavePermission();
                    }
                    bottomSheetDialog = new BottomSheetDialog(SymptomFormActivity.this, R.style.qrcode);
                    bottomSheetDialog.setContentView(R.layout.bottomsheetsharingdetails);
                    bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    bottomSheetDialog.setCancelable(true);
                    bottomSheetDialog.show();

                    llinstagram = bottomSheetDialog.findViewById(R.id.llinstagram);
                    llwhatsapp = bottomSheetDialog.findViewById(R.id.llwhatsapp);
                    llmessage = bottomSheetDialog.findViewById(R.id.llmessage);
                    llemail = bottomSheetDialog.findViewById(R.id.llemail);

                    Bitmap bitmap = takeScreenshot();
                    saveBitmap(bitmap);


                    llinstagram.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isAppInstalled = appInstalledOrNot("com.instagram.android");
                            if (isAppInstalled) {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.setType("image/*");
                                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                shareIntent.putExtra(Intent.EXTRA_TEXT, "\n\n" + name + "\n" + city + " (" + pincode + ")" + "\n" + mobno + "\n" + "\n" + "Via I'm Healthy Application");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "screenshot.png"));
                                shareIntent.setPackage("com.instagram.android");
                                startActivity(shareIntent);

                            } else {

                                Toast.makeText(SymptomFormActivity.this, "Application is not currently installed", Toast.LENGTH_SHORT).show();
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

                                shareIntent.putExtra(Intent.EXTRA_TEXT, "\n\n" + name + "\n" + city + " (" + pincode + ")" + "\n" + mobno + "\n" + "\n" + "Via I'm Healthy Application");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "screenshot.png"));
                                shareIntent.setPackage("com.whatsapp");
                                startActivity(shareIntent);

                            } else {
                                // Do whatever we want to do if application not installed
                                // For example, Redirect to play store

                                Toast.makeText(SymptomFormActivity.this, "Application is not currently installed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    llemail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Bitmap bitmap = takeScreenshot();
                            saveBitmap(bitmap);

                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                            Intent share = new Intent(Intent.ACTION_SEND);

                            share.setType("text/plain");
                            share.setType("image/*");
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                                    b, "Title", null);
                            Uri imageUri = Uri.parse(path);

                            share.putExtra(Intent.EXTRA_TEXT, "\n\n" + name + "\n" + city + " (" + pincode + ")" + "\n" + mobno + "\n" + "\n" + "Via I'm Healthy Application");
                            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "screenshot.png"));
                            startActivity(Intent.createChooser(share, "Select"));
                        }
                    });

                    llmessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("smsto:"));
                                i.setType("vnd.android-dir/mms-sms");
                                i.putExtra("address", new String(""));
                                i.putExtra("sms_body", fullstr + "\n\n-------------------------------------\n\n" + fullstr2 + "\n\n\n" + "\n\n" + name + "\n" + city + " (" + pincode + ")" + "\n" + mobno + "\n" + "Via I'm Healthy Application");
                                startActivity(Intent.createChooser(i, "Send sms via:"));
                            } catch (Exception e) {
                                Toast.makeText(SymptomFormActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
        sms = findViewById(R.id.sms);
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sfever.equals(" ")) {
                    fullstr = "Symptoms:" + "\n" + "\n" + scough + sshortness + shigh;
                } else {
                    fullstr = "Symptoms:" + "\n" + "\n" + sfever + scough + sshortness + shigh;
                }
                if (sdiabetes.equals(" ")) {
                    fullstr2 = "History:" + "\n" + "\n" + shypertension + slung + sheart;
                } else {
                    fullstr2 = "History:" + "\n" + "\n" + sdiabetes + shypertension + slung + sheart;
                }

                if (sfever.equals(" ") & scough.equals(" ") & sshortness.equals(" ") & shigh.equals(" ")) {
                    fullstr = "";
                }
                if (sdiabetes.equals(" ") & shypertension.equals(" ") & slung.equals(" ") & sheart.equals(" ")) {
                    fullstr2 = "";
                }

                if (sfever.equals(" ") & scough.equals(" ") & sshortness.equals(" ") & shigh.equals(" ") & sdiabetes.equals(" ") & shypertension.equals(" ") & slung.equals(" ") & sheart.equals(" ") & sdiabetes.equals(" ") & shypertension.equals(" ") & slung.equals(" ") & sheart.equals(" ")) {
                    Toast.makeText(SymptomFormActivity.this, "Select data from\nSymptoms or History", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("smsto:"));
                        i.setType("vnd.android-dir/mms-sms");
                        i.putExtra("address", new String(""));
                        i.putExtra("sms_body", fullstr + "\n\n-------------------------------------\n\n" + fullstr2 + "\n\n\n" + "\n\n" + name + "\n" + city + " (" + pincode + ")" + "\n" + mobno + "\n" + "Via I'm Healthy Application");
                        startActivity(Intent.createChooser(i, "Send sms via:"));
                    } catch (Exception e) {
                        Toast.makeText(SymptomFormActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkIfAlreadyhavePermission() {

        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            //show dialog to ask permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            return true;
        }

        return false;

    }

    private void getuserdetails() {
        final ProgressDialog showMe = new ProgressDialog(SymptomFormActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();

        stringRequest = new StringRequest(Request.Method.POST, Config.get_user_details,
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
                                String saddres = j.getString("mobile");
                                String spin = j.getString("pincode");
                                String sstate = j.getString("state");
                                String sdistrict = j.getString("district");
                                String url = j.getString("profile_image");

                                name = namef + " " + namem + " " + namel;
                                city = sdistrict;
                                mobno = saddres;
                                pincode = spin;

                                //     Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(SymptomFormActivity.this, msg, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            showMe.dismiss();
                            Toast.makeText(SymptomFormActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                15000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        RequestQueue requestQueue = Volley.newRequestQueue(SymptomFormActivity.this);
        // Adding the StringRequest object into requestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void NetworkDialog() {
        final Dialog dialogs = new Dialog(SymptomFormActivity.this);
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

    @Override
    public void onBackPressed() {

        Intent a = new Intent(SymptomFormActivity.this, HomeScreen.class);
        startActivity(a);

        super.onBackPressed();
    }
}
