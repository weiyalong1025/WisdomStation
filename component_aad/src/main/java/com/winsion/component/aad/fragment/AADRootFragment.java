package com.winsion.component.aad.fragment;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.winsion.component.aad.R;
import com.winsion.component.basic.PlaceHolderFragment;
import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.view.MyIndicator;
import com.winsion.component.basic.view.NoScrollViewPager;

/**
 * Created by 10295 on 2018/3/23.
 * 到发RootFragment
 */

public class AADRootFragment extends BaseFragment {
    private NoScrollViewPager vpContent;
    private MyIndicator mIndicator;

    private final Fragment[] mFragments = {new PlaceHolderFragment(), new PlaceHolderFragment()};
    private final int[] mTitles = {R.string.tab_up, R.string.tab_down};

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.basic_fragment_two_pager, null);
    }

    @Override
    protected void init() {
        initView();
        initAdapter();
    }

    private void initView() {
        vpContent = findViewById(R.id.vp_content);
        mIndicator = findViewById(R.id.mi_container);
    }

    private void initAdapter() {
        vpContent.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        mIndicator.setViewPager(vpContent);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(mTitles[position]);
        }
    }
}
