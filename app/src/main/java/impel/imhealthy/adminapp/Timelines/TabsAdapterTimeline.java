package impel.imhealthy.adminapp.Timelines;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabsAdapterTimeline extends FragmentStatePagerAdapter {

    DayTFragment tab1;
    WeekTFragment tab2;
    MonthTFragment tab3;

    public TabsAdapterTimeline(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if(tab1==null)
                tab1 = new DayTFragment();
                return tab1;
            case 1:
                if(tab2==null)
                    tab2 = new WeekTFragment();
                return tab2;
            case 2:
                if(tab3==null)
                    tab3 = new MonthTFragment();
                return tab3;


        }
        return null;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "DAY";
            case 1:
                return "WEEK";
            case 2:
                return "MONTH";

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
