package com.winsion.wisdomstation.operation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.common.constants.SystemType;
import com.winsion.wisdomstation.data.SPDataSource;
import com.winsion.wisdomstation.data.constants.SPKey;
import com.winsion.wisdomstation.operation.fragment.issue.IssueFragment;
import com.winsion.wisdomstation.operation.fragment.mytask.MyTaskFragment;
import com.winsion.wisdomstation.operation.fragment.taskmonitor.TaskMonitorFragment;
import com.winsion.wisdomstation.view.MyIndicator;
import com.winsion.wisdomstation.view.NoScrollViewPager;

import butterknife.BindView;

/**
 * Created by 10295 on 2017/12/10 0010.
 */

public class OperationRootFragment extends BaseFragment {
    @BindView(R.id.vp_content)
    NoScrollViewPager vpContent;
    @BindView(R.id.mi_container)
    MyIndicator mIndicator;

    private Fragment[] mFragments = {new MyTaskFragment(), new TaskMonitorFragment(), new IssueFragment()};
    private int[] mTitles = {R.string.my_task, R.string.task_monitor, R.string.command_and_collaboration};
    private int mCurrentSysType = -1;

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

        public MyPagerAdapter(FragmentManager fm) {
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
