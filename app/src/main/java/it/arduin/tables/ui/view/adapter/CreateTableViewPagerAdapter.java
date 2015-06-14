package it.arduin.tables.ui.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.arduin.tables.ui.view.fragment.CreateTableColumnsFragment;
import it.arduin.tables.ui.view.fragment.CreateTableSettingsFragment;

public class CreateTableViewPagerAdapter  extends FragmentPagerAdapter {
    private static final int FRAGMENT_COUNT = 2;

    public CreateTableViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CreateTableSettingsFragment.newInstance();
            case 1:
                return CreateTableColumnsFragment.newInstance();
            default:
                return CreateTableSettingsFragment.newInstance();
        }
    }

}


