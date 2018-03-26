package com.winsion.component.aad.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.winsion.component.aad.R;
import com.winsion.component.aad.constants.AADType;
import com.winsion.component.aad.fragment.aadlist.AADListFragment;
import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.view.MyIndicator;
import com.winsion.component.basic.view.NoScrollViewPager;

import static com.winsion.component.aad.fragment.aadlist.AADListFragment.AAD_TYPE;

/**
 * Created by 10295 on 2018/3/23.
 * 到发RootFragment
 */

public class AADRootFragment extends BaseFragment {
    private NoScrollViewPager vpContent;
    private MyIndicator mIndicator;

    private final Fragment[] mFragments = new Fragment[2];
    private final int[] mTitles = {R.string.tab_up, R.string.tab_down};

    @SuppressLint("InflateParams")
    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.basic_fragment_two_pager, null);
    }

    @Override
    protected void init() {
        initView();
        initData();
        initAdapter();
    }

    private void initData() {
        Bundle bundle;

        AADListFragment aadUpListFragment = new AADListFragment();
        bundle = new Bundle();
        bundle.putInt(AAD_TYPE, AADType.TYPE_UP);
        aadUpListFragment.setArguments(bundle);
        mFragments[0] = aadUpListFragment;

        AADListFragment aadDownListFragment = new AADListFragment();
        bundle = new Bundle();
        bundle.putInt(AAD_TYPE, AADType.TYPE_DOWN);
        aadDownListFragment.setArguments(bundle);
        mFragments[1] = aadDownListFragment;
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
