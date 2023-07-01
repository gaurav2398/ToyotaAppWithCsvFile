package impel.imhealthy.adminapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics
        .Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import impel.imhealthy.adminapp.HomeActivity.MedicalActivity;
import impel.imhealthy.adminapp.HomeActivity.ProfileActivity;
import impel.imhealthy.adminapp.HomeActivity.SettingActivity;
import impel.imhealthy.adminapp.HomeActivity.SymptomFormActivity;
import impel.imhealthy.adminapp.SortingFragment.HistoySortingActivity;
import impel.imhealthy.adminapp.Timelines.TimeLineActivity;
import impel.imhealthy.adminapp.Utilities.SessionManager;

public class HomeScreen extends AppCompatActivity {

    private int[] navLabels = {
            R.string.home,
            R.string.history,
            R.string.profile,
            R.string.setting
    };
    ImageView back;
    RecyclerView menurecycler_v;
    public View decorView;
    androidx.appcompat.widget.Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout Drawer;
    androidx.appcompat.widget.Toolbar toolbars;
    androidx.appcompat.app.ActionBar actionBar;
    RelativeLayout rlnotification,highlight;
    LinearLayout home, history, profile, setting;
    RelativeLayout covid19,medicahelp,rl_test;
    SessionManager session;
    String userid;


    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        checkIfAlreadyhavePermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }
        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> users = session.getUserDetails();
        userid = users.get(session.KEY_USERID);

        Log.d("user id",userid);

        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        profile = findViewById(R.id.profile);
        setting = findViewById(R.id.setting);
        covid19 = findViewById(R.id.rl_covid19);
        rl_test = findViewById(R.id.rl_test);
        medicahelp = findViewById(R.id.medicalhelp);
        highlight = findViewById(R.id.highlight);

        highlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(HomeScreen.this, HistoySortingActivity.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(HomeScreen.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, TimeLineActivity.class));
            }
        });

        covid19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(HomeScreen.this, StudentListActivity.class);
                intent.putExtra("direct","yes");
                startActivity(intent);
//                Intent intent =  new Intent(HomeScreen.this, SymptomFormActivity.class);
//                startActivity(intent);
            }
        });
        rl_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(HomeScreen.this, StudentListActivity.class);
                intent.putExtra("direct","no");
                startActivity(intent);
//                Intent intent =  new Intent(HomeScreen.this, TestScreenActivity.class);
//                startActivity(intent);
            }
        });
        medicahelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent =  new Intent(HomeScreen.this, MedicalActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(HomeScreen.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(HomeScreen.this, TimeLineActivity.class);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(HomeScreen.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkIfAlreadyhavePermission() {

        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if((checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            //show dialog to ask permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            return true;
        }

        return false;

    }
    @Override
    public void onBackPressed() {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

        super.onBackPressed();
    }
}
