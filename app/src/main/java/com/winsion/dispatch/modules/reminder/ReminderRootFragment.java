package com.winsion.dispatch.modules.reminder;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.winsion.dispatch.PlaceHolderFragment;
import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseFragment;
import com.winsion.dispatch.common.constants.SystemType;
import com.winsion.dispatch.data.SPDataSource;
import com.winsion.dispatch.data.constants.SPKey;
import com.winsion.dispatch.modules.reminder.fragment.systemremind.SystemRemindFragment;
import com.winsion.dispatch.modules.reminder.fragment.todo.TodoListFragment;
import com.winsion.dispatch.view.BadgeRadioButton;
import com.winsion.dispatch.view.MyIndicator;
import com.winsion.dispatch.view.NoScrollViewPager;

/**
 * Created by 10295 on 2017/12/10 0010
 */

public class ReminderRootFragment extends BaseFragment {
    private NoScrollViewPager vpContent;
    private MyIndicator mIndicator;
    private BadgeRadioButton brb0;
    private BadgeRadioButton brb1;
    private BadgeRadioButton brb2;

    private Fragment[] mFragments = {new PlaceHolderFragment(), new TodoListFragment(), new SystemRemindFragment()};
    private int[] mTitles = {R.string.tab_user_message, R.string.tab_todo, R.string.tab_system_remind};
    private int mCurrentSysType = -1;

    @SuppressLint("InflateParams")
    @Override
    public View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_three_pager, null);
    }

    @Override
    protected void init() {
        initView();
        vpContent.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        mIndicator.setViewPager(vpContent);
        // 预加载所有界面
        vpContent.setOffscreenPageLimit(2);
    }

    private void initView() {
        vpContent = findViewById(R.id.vp_content);
        mIndicator = findViewById(R.id.mi_container);
        brb0 = findViewById(R.id.brb0);
        brb1 = findViewById(R.id.brb1);
        brb2 = findViewById(R.id.brb2);
    }

    @Override
    public void onResume() {
        super.onResume();
        int sysType = (int) SPDataSource.get(mContext, SPKey.KEY_SYS_TYPE, SystemType.OPERATION);
        if (mCurrentSysType != sysType) {
            mCurrentSysType = sysType;
            View left = mIndicator.getChildAt(0);
            View middle = mIndicator.getChildAt(1);
            switch (mCurrentSysType) {
                case SystemType.OPERATION:
                    middle.setBackgroundResource(R.drawable.selector_indicator_bg_middle);
                    left.setVisibility(View.VISIBLE);
                    mIndicator.whichChecked(0);
                    break;
                case SystemType.GRID:
                    middle.setBackgroundResource(R.drawable.selector_indicator_bg_left);
                    left.setVisibility(View.GONE);
                    mIndicator.whichChecked(1);
                    break;
            }
        }
    }

    public BadgeRadioButton getBrbView(int index) {
        return index == 0 ? brb0 : index == 1 ? brb1 : index == 2 ? brb2 : null;
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
