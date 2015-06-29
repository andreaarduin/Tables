package it.arduin.tables.ui.createTable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import it.arduin.tables.R;

public class CreateTableViewPagerAdapter  extends FragmentPagerAdapter {
    FragmentManager mFragmentManager;
    ArrayList<Fragment> fragments;

    public CreateTableViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.mFragmentManager=fragmentManager;
        fragments = new ArrayList<>();
        fragments.add(CreateTableSettingsFragment.newInstance());
        fragments.add(CreateTableColumnFragment.newInstance());
    }
    @Override
    public Fragment getItem(int position){
        return fragments.get(position);
    }
    @Override
    public int getCount() {
        return fragments.size();
    }


    public void addColumnPage(){
        fragments.add(CreateTableColumnFragment.newInstance());
        notifyDataSetChanged();
    }
    public Fragment getFragmentAt(int position){
        return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.container + ":" + position);
    }

    public void deletePage(int currentItem) {
        fragments.remove(currentItem);
        notifyDataSetChanged();
    }
}


