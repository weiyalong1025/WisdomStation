package com.winsion.component.remind.fragment;

import android.annotation.SuppressLint;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.billy.cc.core.component.CC;
import com.winsion.component.basic.PlaceHolderFragment;
import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.view.BadgeRadioButton;
import com.winsion.component.basic.view.MyIndicator;
import com.winsion.component.basic.view.NoScrollViewPager;
import com.winsion.component.remind.R;
import com.winsion.component.remind.fragment.systemremind.SystemRemindFragment;
import com.winsion.component.remind.fragment.todo.TodoListFragment;

/**
 * Created by 10295 on 2017/12/10 0010
 */

public class RemindRootFragment extends BaseFragment {
    private NoScrollViewPager vpContent;
    private MyIndicator mIndicator;
    private BadgeRadioButton brb0;
    private BadgeRadioButton brb1;
    private BadgeRadioButton brb2;

    private final Fragment[] mFragments = new Fragment[3];
    private final int[] mTitles = {R.string.tab_user_message, R.string.tab_todo, R.string.tab_system_remind};

    @SuppressLint("InflateParams")
    @Override
    public View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.basic_fragment_three_pager, null);
    }

    @Override
    protected void init() {
        initData();
        initView();
        initAdapter();
    }

    private void initData() {
        Fragment messageListFragment = CC.obtainBuilder("ComponentContact")
                .setActionName("getMessageListFragment")
                .build()
                .call()
                .getDataItem("fragment");
        if (messageListFragment == null) {
            messageListFragment = new PlaceHolderFragment();
        }
        mFragments[0] = messageListFragment;
        mFragments[1] = new TodoListFragment();
        mFragments[2] = new SystemRemindFragment();
    }

    private void initView() {
        vpContent = findViewById(R.id.vp_content);
        mIndicator = findViewById(R.id.mi_container);
        brb0 = findViewById(R.id.brb0);
        brb1 = findViewById(R.id.brb1);
        brb2 = findViewById(R.id.brb2);

        // 预加载所有界面
        vpContent.setOffscreenPageLimit(2);
    }

    private void initAdapter() {
        vpContent.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        mIndicator.setViewPager(vpContent);
    }

    public void showBadge(@IntRange(from = 0, to = 2) int index, int badgeNum) {
        BadgeRadioButton badgeRadioButton = index == 0 ? brb0 : index == 1 ? brb1 : index == 2 ? brb2 : null;
        assert badgeRadioButton != null;
        badgeRadioButton.setNumber(badgeNum);
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
