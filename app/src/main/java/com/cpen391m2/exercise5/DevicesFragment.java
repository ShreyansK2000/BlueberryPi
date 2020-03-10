package com.cpen391m2.exercise5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class DevicesFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    public BluetoothDiscoverFragment discoverFragment;
    public BluetoothPairedFragment pairedFragment;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragement_devices_tab, container, false);
        View mFragment = view;

        mViewPager = mFragment.findViewById(R.id.m_view_pager);
        mTabLayout = mFragment.findViewById(R.id.my_devices_tab_layout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });
    }

    private void setUpViewPager(ViewPager mViewPager) {
        DevicesTabAdapter myEventsTabAdapter = new DevicesTabAdapter(getChildFragmentManager());

        if (discoverFragment == null) {
            discoverFragment = new BluetoothDiscoverFragment();
        }

        if (pairedFragment == null) {
            pairedFragment = new BluetoothPairedFragment();
        }

        myEventsTabAdapter.addFragment(discoverFragment, "DISCOVERED DEVICES");
        myEventsTabAdapter.addFragment(pairedFragment, "PAIRED DEVICES");

        mViewPager.setAdapter(myEventsTabAdapter);
    }

}
