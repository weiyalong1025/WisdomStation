package com.winsion.dispatch.modules.reminder;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.view.BadgeRadioButton;
import com.winsion.component.basic.view.MyIndicator;
import com.winsion.component.basic.view.NoScrollViewPager;
import com.winsion.dispatch.PlaceHolderFragment;
import com.winsion.dispatch.R;
import com.winsion.dispatch.modules.reminder.fragment.systemremind.SystemRemindFragment;
import com.winsion.dispatch.modules.reminder.fragment.todo.TodoListFragment;

/**
 * Created by 10295 on 2017/12/10 0010
 */

public class ReminderRootFragment extends BaseFragment {
    private NoScrollViewPager vpContent;
    private MyIndicator mIndicator;
    private BadgeRadioButton brb0;
    private BadgeRadioButton brb1;
    private BadgeRadioButton brb2;

    private final Fragment[] mFragments = {new PlaceHolderFragment(), new TodoListFragment(), new SystemRemindFragment()};
    private final int[] mTitles = {R.string.tab_user_message, R.string.tab_todo, R.string.tab_system_remind};

    @SuppressLint("InflateParams")
    @Override
    public View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_three_pager, null);
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
