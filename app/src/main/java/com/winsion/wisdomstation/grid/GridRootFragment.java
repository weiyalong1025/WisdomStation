package com.winsion.wisdomstation.grid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseFragment;
import com.winsion.wisdomstation.grid.modules.patrolplan.fragment.PatrolPlanFragment;
import com.winsion.wisdomstation.grid.modules.problemmanage.fragment.ProblemManageFragment;
import com.winsion.wisdomstation.view.MyIndicator;
import com.winsion.wisdomstation.view.NoScrollViewPager;

import butterknife.BindView;

/**
 * Created by 10295 on 2017/12/10 0010.
 */

public class GridRootFragment extends BaseFragment {
    @BindView(R.id.vp_content)
    NoScrollViewPager vpContent;
    @BindView(R.id.mi_container)
    MyIndicator mIndicator;

    private Fragment[] mFragments = {new PatrolPlanFragment(), new ProblemManageFragment()};
    private int[] mTitles = {R.string.patrol_plan, R.string.problem_manager};

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
