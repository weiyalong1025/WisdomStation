package com.winsion.dispatch.modules.grid.fragment;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseFragment;
import com.winsion.dispatch.modules.grid.fragment.patrolplan.PatrolPlanFragment;
import com.winsion.dispatch.modules.grid.fragment.problemmanage.ProblemManageFragment;
import com.winsion.dispatch.view.MyIndicator;
import com.winsion.dispatch.view.NoScrollViewPager;

import butterknife.BindView;

/**
 * Created by 10295 on 2017/12/10 0010.
 * 网格管理一级界面
 */

public class GridRootFragment extends BaseFragment {
    @BindView(R.id.vp_content)
    NoScrollViewPager vpContent;
    @BindView(R.id.mi_container)
    MyIndicator mIndicator;

    private Fragment[] mFragments = {new PatrolPlanFragment(), new ProblemManageFragment()};
    private int[] mTitles = {R.string.tab_patrol_plan, R.string.tab_problem_manager};

    @SuppressLint("InflateParams")
    @Override
    public View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_two_pager, null);
    }

    @Override
    protected void init() {
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
