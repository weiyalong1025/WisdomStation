package com.winsion.component.contact.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.winsion.component.basic.base.BaseFragment;
import com.winsion.component.basic.view.MyIndicator;
import com.winsion.component.basic.view.NoScrollViewPager;
import com.winsion.component.contact.R;
import com.winsion.component.contact.constants.ContactType;
import com.winsion.component.contact.entity.ContactsEntity;
import com.winsion.component.contact.entity.ContactsGroupEntity;
import com.winsion.component.contact.entity.TeamEntity;

import static com.winsion.component.contact.fragment.ContactFragment.CONTACT_TYPE;

/**
 * Created by 10295 on 2017/12/10 0010.
 * 联系人一级界面
 */

public class ContactRootFragment extends BaseFragment {
    private NoScrollViewPager vpContent;
    private MyIndicator mIndicator;

    private final Fragment[] mFragments = new Fragment[3];
    private final int[] mTitles = {R.string.tab_contacts, R.string.tab_team_group, R.string.tab_contact_group};

    @SuppressLint("InflateParams")
    @Override
    public View setContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.basic_fragment_three_pager, null);
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
        Bundle bundle;

        ContactFragment contactFragment = new ContactFragment<ContactsEntity>();
        bundle = new Bundle();
        bundle.putInt(CONTACT_TYPE, ContactType.TYPE_CONTACTS);
        contactFragment.setArguments(bundle);
        mFragments[0] = contactFragment;

        ContactFragment teamFragment = new ContactFragment<TeamEntity>();
        bundle = new Bundle();
        bundle.putInt(CONTACT_TYPE, ContactType.TYPE_TEAM);
        teamFragment.setArguments(bundle);
        mFragments[1] = teamFragment;

        ContactFragment contactsGroupFragment = new ContactFragment<ContactsGroupEntity>();
        bundle = new Bundle();
        bundle.putInt(CONTACT_TYPE, ContactType.TYPE_CONTACTS_GROUP);
        contactsGroupFragment.setArguments(bundle);
        mFragments[2] = contactsGroupFragment;

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
