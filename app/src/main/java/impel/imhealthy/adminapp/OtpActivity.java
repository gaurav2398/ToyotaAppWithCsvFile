package impel.imhealthy.adminapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import impel.imhealthy.adminapp.Utilities.Config;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class OtpActivity extends AppCompatActivity {

    Button verify;
    EditText editText1,editText2,editText3,editText4,editText5,editText6;
    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    SessionManager session;
    ImageView back;
    String no,userid;

    EditText otp;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        session = new SessionManager(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD4"));
        }

        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        editText5 = findViewById(R.id.editText5);
        editText6 = findViewById(R.id.editText6);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        editText1.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText1.getText().toString().length() == 1) {
                    editText1.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bordergrey) );
                    editText2.requestFocus();
                }
            }
        });

        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText2.getText().toString().length() == 0) {
                    editText1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText2.getText().toString().length() == 1) {
                    editText2.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bordergrey) );
                    editText3.requestFocus();
                }
            }
        });

        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText3.getText().toString().length() == 0) {

                    editText2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText3.getText().toString().length() == 1) {
                    editText3.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bordergrey) );
                    editText4.requestFocus();
                }
            }
        });

        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText4.getText().toString().length() == 0) {
                    editText4.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bordergrey) );
                    editText3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText4.getText().toString().length() == 1) {
                    editText4.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bordergrey) );
                    editText5.requestFocus();
                }

                // We can call api to verify the OTP here or on an explicit button click
            }
        });
        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText5.getText().toString().length() == 0) {
                    editText5.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bordergrey) );
                    editText4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText5.getText().toString().length() == 1) {
                    editText5.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bordergrey) );
                    editText6.requestFocus();
                }

                // We can call api to verify the OTP here or on an explicit button click
            }
        });
        editText6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText6.getText().toString().length() == 0) {
                    editText6.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bordergrey) );
                    editText5.requestFocus();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void afterTextChanged(Editable editable) {

                editText6.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bordergrey) );
                editText6.setShowSoftInputOnFocus(false);

                // We can call api to verify the OTP here or on an explicit button click
            }
        });

        mAuth = FirebaseAuth.getInstance();

        no = getIntent().getStringExtra("mobile");

        verify = findViewById(R.id.verify);
        otp("12345");
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText1.getText().toString().isEmpty() || editText2.getText().toString().isEmpty()||editText3.getText().toString().isEmpty()
                ||editText4.getText().toString().isEmpty() || editText5.getText().toString().isEmpty())
                {
                    Toast.makeText(OtpActivity.this, "Enter Valid Otp", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    verifyVerificationCode(editText1.getText().toString()+editText2.getText().toString()+editText3.getText().toString()+
                            editText4.getText().toString()+editText5.getText().toString()+editText6.getText().toString());

                }

            }
        });

    }
    private void otp(final String otp) {

        final ProgressDialog showMe = new ProgressDialog(OtpActivity.this);
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();
        String url = Config.verify_admin_otp;
        mRequestQueue = Volley.newRequestQueue(this);
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


                                 userid = j.getString("user_id");



                                sendVerificationCode(no);
                              /* Intent intent = new Intent(OtpActivity.this,PinCodeActivity.class);
                                startActivity(intent);
                                finish();*/
                            } else {

                                // Showing Echo Response Message Coming From Server.
                                Toast.makeText(OtpActivity.this, msg, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(OtpActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
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
                params.put("mobile", no);
                params.put("otp", otp);
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
    private void sendVerificationCode(String no) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + no,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
           /*     editText1.setText(code.charAt(0));
                editText2.setText(code.charAt(1));
                editText3.setText(code.charAt(2));
                editText4.setText(code.charAt(3));
                editText5.setText(code.charAt(4));
                editText6.setText(code.charAt(5));*/
//                otp.setText(code);
                //verifying the code
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("user_id", userid);

                editor.commit();
                session.createLoginSession(userid);
                verifyVerificationCode(code);

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_id", userid);

        editor.commit();
        session.createLoginSession(userid);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("user_id", userid);

                            editor.commit();
                            session.createLoginSession(userid);
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(OtpActivity.this, PinCodeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            Toast.makeText(OtpActivity.this, "Enter Valid Otp", Toast.LENGTH_SHORT).show();
                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }


                        }
                    }
                });
    }
}
