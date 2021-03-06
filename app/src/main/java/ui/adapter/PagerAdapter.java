package ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ui.fragments.UpcomingFragment;
import ui.fragments.ShiftedClassFragment;
import ui.fragments.CancelledClassFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int tabsNumber;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior, int tabs) {
        super(fm, behavior);
        this.tabsNumber = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new UpcomingFragment();
            case 1:
                return new ShiftedClassFragment();
            case 2 :
                return new CancelledClassFragment();
                default: return null;
        }
    }

    @Override
    public int getCount() {
        return tabsNumber;
    }
}
