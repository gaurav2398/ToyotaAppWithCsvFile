package impel.imhealthy.adminapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import impel.imhealthy.adminapp.HomeActivity.ProfileActivity;
import impel.imhealthy.adminapp.HomeActivity.SettingActivity;
import impel.imhealthy.adminapp.Timelines.TimeLineActivity;

public class NewsActivity extends AppCompatActivity {

    TextView ministryof,googlenews;
    LinearLayout home, history, profile, setting;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        profile = findViewById(R.id.profile);
        setting = findViewById(R.id.setting);

        googlenews = findViewById(R.id.googlenews);
        ministryof = findViewById(R.id.ministryof);

        googlenews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://news.google.com/covid19/map?hl=en-IN&gl=IN&ceid=IN:en"));
                startActivity(viewIntent);
/*
                Intent intent = new Intent(NewsActivity.this, WebviewActivity.class);
                intent.putExtra("link", "https://news.google.com/covid19/map?hl=en-IN&gl=IN&ceid=IN:en");
                startActivity(intent);
*/
            }
        });
        ministryof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.mohfw.gov.in/"));
                startActivity(viewIntent);
              /*  Intent intent = new Intent(NewsActivity.this, WebviewActivity.class);
                intent.putExtra("link", "https://www.mohfw.gov.in/");
                startActivity(intent);*/
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(NewsActivity.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewsActivity.this, TimeLineActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(NewsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(NewsActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
