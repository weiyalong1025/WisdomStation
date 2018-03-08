package com.winsion.dispatch.modules.operation.fragment;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.view.MyIndicator;
import com.winsion.component.basic.view.NoScrollViewPager;
import com.winsion.dispatch.R;
import com.winsion.dispatch.modules.operation.fragment.issue.IssueFragment;
import com.winsion.dispatch.modules.operation.fragment.taskmonitor.MonitorTaskListFragment;
import com.winsion.dispatch.modules.operation.fragment.taskoperator.OperatorTaskListFragment;

/**
 * Created by 10295 on 2017/12/10 0010
 */

public class OperationRootFragment extends BaseFragment {
    private NoScrollViewPager vpContent;
    private MyIndicator mIndicator;

    private final Fragment[] mFragments = {new OperatorTaskListFragment(), new MonitorTaskListFragment(), new IssueFragment()};
    private final int[] mTitles = {R.string.tab_my_task, R.string.tab_task_monitor, R.string.tab_command_and_cooperation};

    @SuppressLint("InflateParams")
    @Override
    public View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_three_pager, null);
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
