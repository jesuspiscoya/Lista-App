package com.example.appyadira.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appyadira.fragments.HistorialFragment;
import com.example.appyadira.fragments.NuevoFragment;

public class BarPagerAdapter extends FragmentStateAdapter {
    public BarPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NuevoFragment();
            case 1:
                return new HistorialFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
