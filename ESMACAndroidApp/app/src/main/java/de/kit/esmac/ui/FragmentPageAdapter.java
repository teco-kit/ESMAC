
package de.kit.esmac.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import de.kit.esmac.domain.screen.ScreenFragment;


public class FragmentPageAdapter extends FragmentPagerAdapter {
    private List<ScreenFragment> fragments;
   

    public FragmentPageAdapter(FragmentManager fm, List<ScreenFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int index) {
        

        return null;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
