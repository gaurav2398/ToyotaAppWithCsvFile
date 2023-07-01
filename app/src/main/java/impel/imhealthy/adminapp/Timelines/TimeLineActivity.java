package impel.imhealthy.adminapp.Timelines;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import impel.imhealthy.adminapp.HomeActivity.ProfileActivity;
import impel.imhealthy.adminapp.HomeActivity.SettingActivity;
import impel.imhealthy.adminapp.HomeScreen;
import impel.imhealthy.adminapp.R;

public class TimeLineActivity extends AppCompatActivity  implements ActionBar.TabListener{

    private ViewPager mViewPager;
    TabsAdapterTimeline tabsAdapter;
    ImageView back;

    TextView tab_label;

    private int[] navLabels = {
            R.string.day,
            R.string.week,
            R.string.month
    };

    LinearLayout home, history, profile, setting;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6A5AD6"));
        }
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent a = new Intent(TimeLineActivity.this, HomeScreen.class);
                startActivity(a);
                finish();
            }
        });

        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        profile = findViewById(R.id.profile);
        setting = findViewById(R.id.setting);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TimeLineActivity.this, HomeScreen.class));
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(TimeLineActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(TimeLineActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        tabsAdapter = new TabsAdapterTimeline(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout navigation = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager.setAdapter(tabsAdapter);
        navigation.setupWithViewPager(mViewPager);

        for (int i = 0; i < navigation.getTabCount(); i++) {

            LinearLayout tab = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.customtab, null);

            tab_label = (TextView) tab.findViewById(R.id.nav_label);

            tab_label.setText(getResources().getString(navLabels[i]));

            if (i == 0) {
                tab_label.setTextColor(getResources().getColor(R.color.white));
            } else {

            }
            navigation.getTabAt(i).setCustomView(tab);
        }

        navigation.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                View tabView = tab.getCustomView();

                tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                tab_label.setTextColor(getResources().getColor(R.color.white));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                View tabView = tab.getCustomView();
                tab_label = (TextView) tabView.findViewById(R.id.nav_label);

                // back to the black color
                tab_label.setTextColor(getResources().getColor(R.color.black));
                // and the icon resouce to the old black image
                // also via array that holds the icon resources in order
                // and get the one of this tab's position

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    @Override
    public void onBackPressed() {

        Intent a = new Intent(TimeLineActivity.this, HomeScreen.class);
        startActivity(a);

        super.onBackPressed();
    }
}
