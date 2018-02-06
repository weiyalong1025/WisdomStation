package com.winsion.dispatch.modules.operation.fragment;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseFragment;
import com.winsion.dispatch.common.constants.SystemType;
import com.winsion.dispatch.data.SPDataSource;
import com.winsion.dispatch.data.constants.SPKey;
import com.winsion.dispatch.modules.operation.fragment.issue.IssueFragment;
import com.winsion.dispatch.modules.operation.fragment.taskmonitor.MonitorTaskListFragment;
import com.winsion.dispatch.modules.operation.fragment.taskoperator.OperatorTaskListFragment;
import com.winsion.dispatch.view.MyIndicator;
import com.winsion.dispatch.view.NoScrollViewPager;

import butterknife.BindView;

/**
 * Created by 10295 on 2017/12/10 0010
 */

public class OperationRootFragment extends BaseFragment {
    @BindView(R.id.vp_content)
    NoScrollViewPager vpContent;
    @BindView(R.id.mi_container)
    MyIndicator mIndicator;

    private Fragment[] mFragments = {new OperatorTaskListFragment(), new MonitorTaskListFragment(), new IssueFragment()};
    private int[] mTitles = {R.string.tab_my_task, R.string.tab_task_monitor, R.string.tab_command_and_cooperation};
    private int mCurrentSysType = -1;

    @SuppressLint("InflateParams")
    @Override
    public View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_three_pager, null);
    }

    @Override
    protected void init() {
        vpContent.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        mIndicator.setViewPager(vpContent);
    }

    @Override
    public void onResume() {
        super.onResume();
        int sysType = (int) SPDataSource.get(mContext, SPKey.KEY_SYS_TYPE, SystemType.OPERATION);
        if (mCurrentSysType != sysType) {
            mCurrentSysType = sysType;
            View right = mIndicator.getChildAt(2);
            View middle = mIndicator.getChildAt(1);
            switch (mCurrentSysType) {
                case SystemType.OPERATION:
                    middle.setBackgroundResource(R.drawable.selector_indicator_bg_middle);
                    right.setVisibility(View.VISIBLE);
                    break;
                case SystemType.GRID:
                    middle.setBackgroundResource(R.drawable.selector_indicator_bg_right);
                    right.setVisibility(View.GONE);
                    break;
            }
            mIndicator.whichChecked(0);
        }
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
