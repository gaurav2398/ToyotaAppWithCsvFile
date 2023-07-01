package impel.imhealthy.adminapp.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import impel.imhealthy.adminapp.SortingFragment.DayFragment;
import impel.imhealthy.adminapp.SortingFragment.MonthFragment;
import impel.imhealthy.adminapp.SortingFragment.WeekFragment;
public class TabsAdapterNew extends FragmentStatePagerAdapter {

    DayFragment tab1;
    WeekFragment tab2;
    MonthFragment tab3;

    public TabsAdapterNew(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if(tab1==null)
                tab1 = new DayFragment();
                return tab1;
            case 1:
                if(tab2==null)
                    tab2 = new WeekFragment();
                return tab2;
            case 2:
                if(tab3==null)
                    tab3 = new MonthFragment();
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
