package com.winsion.wisdomstation.main.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.SwitchSysActivity;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.common.constants.SystemType;
import com.winsion.wisdomstation.data.CacheDataSource;
import com.winsion.wisdomstation.modules.contacts.ContactsRootFragment;
import com.winsion.wisdomstation.modules.daofa.DaofaRootFragment;
import com.winsion.wisdomstation.modules.grid.GridRootFragment;
import com.winsion.wisdomstation.modules.operation.OperationRootFragment;
import com.winsion.wisdomstation.modules.reminder.ReminderRootFragment;
import com.winsion.wisdomstation.modules.scene.SceneRootFragment;
import com.winsion.wisdomstation.user.UserActivity;
import com.winsion.wisdomstation.utils.ImageLoader;
import com.winsion.wisdomstation.view.AlphaTabView;
import com.winsion.wisdomstation.view.AlphaTabsIndicator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 10295 on 2017/12/6 0006.
 */

public class MainActivity extends BaseActivity implements MainContract.View, ViewPager.OnPageChangeListener {
    @BindView(R.id.vp_content)
    ViewPager vpContent;
    @BindView(R.id.atv_operation)
    AlphaTabView atvOperation;
    @BindView(R.id.atv_dispatch)
    AlphaTabView atvDispatch;
    @BindView(R.id.atv_contacts)
    AlphaTabView atvContacts;
    @BindView(R.id.atv_grid)
    AlphaTabView atvGrid;
    @BindView(R.id.atv_scene)
    AlphaTabView atvScene;
    @BindView(R.id.atv_reminder)
    AlphaTabView atvReminder;
    @BindView(R.id.ati_indicator)
    AlphaTabsIndicator atiIndicator;
    @BindView(R.id.iv_switch)
    ImageView ivSwitch;
    @BindView(R.id.iv_head)
    ImageView ivHead;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private int mCurrentSysType = -1;
    private MainContract.Presenter mPresenter;

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void start() {
        initPresenter();
        initData();
        initAdapter();
        initListener();
        loadUserHead();
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    private void initPresenter() {
        mPresenter = new MainPresenter(this);
    }

    private void initData() {
        mFragments.add(new OperationRootFragment());
        mFragments.add(new DaofaRootFragment());
        mFragments.add(new ContactsRootFragment());
        mFragments.add(new GridRootFragment());
        mFragments.add(new SceneRootFragment());
        mFragments.add(new ReminderRootFragment());
    }

    private void initAdapter() {
        vpContent.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        });
        atiIndicator.setViewPager(vpContent);
    }

    private void initListener() {
        vpContent.setOnPageChangeListener(this);
    }

    /**
     * 根据ViewPager偏移量来显示或隐藏图标
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // 偏移边界
        float offsetBorder = 0.05f;
        // 透明度
        float alpha;
        if (positionOffset <= offsetBorder) {
            alpha = (offsetBorder - positionOffset) * (1 / offsetBorder);
        } else if (positionOffset >= (1 - offsetBorder)) {
            alpha = (offsetBorder - (1 - positionOffset)) * (1 / offsetBorder);
        } else {
            alpha = 0;
        }
        ivHead.setAlpha(alpha);
        ivSwitch.setAlpha(alpha);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void loadUserHead() {
        ImageLoader.loadUrl(ivHead, CacheDataSource.getUserHeadAddress(), R.drawable.ic_head_single, R.drawable.ic_head_single);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 切换生产作业/网格
        int sysType = mPresenter.getCurrentSystemType();
        if (mCurrentSysType != sysType) {
            mCurrentSysType = sysType;
            switchSystem(mCurrentSysType);
        }
    }

    /**
     * 切换系统
     */
    private void switchSystem(int systemType) {
        switch (systemType) {
            case SystemType.OPERATION:
                atvDispatch.setVisibility(View.VISIBLE);
                atvContacts.setVisibility(View.VISIBLE);
                atvGrid.setVisibility(View.GONE);
                atvScene.setVisibility(View.VISIBLE);
                atiIndicator.setTabCurrentItem(0);
                break;
            case SystemType.GRID:
                atvDispatch.setVisibility(View.GONE);
                atvContacts.setVisibility(View.GONE);
                atvGrid.setVisibility(View.VISIBLE);
                atvScene.setVisibility(View.GONE);
                atiIndicator.setTabCurrentItem(0);
                break;
        }
    }

    @OnClick({R.id.iv_switch, R.id.iv_head})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_switch:
                startActivity(SwitchSysActivity.class, false);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                break;
            case R.id.iv_head:
                startActivity(UserActivity.class, false);
                break;
        }
    }

    /**
     * 未读用户消息数
     */
    private int unreadUserMessageCount;
    /**
     * 未读系统提醒数
     */
    private int unreadSysRemindCount;

    /**
     * 未读用户消息数发生改变，更新小红点
     *
     * @param afterChangedCount 改变后的未读用户消息数
     */
    public void notifyUnreadUserMessageCountChanged(int afterChangedCount) {
        unreadUserMessageCount = afterChangedCount;
        if (unreadUserMessageCount + unreadSysRemindCount == 0) {
            atvReminder.removeShow();
        } else {
            atvReminder.showPoint();
        }
    }

    /**
     * 未读系统提醒数发生改变，更新小红点
     *
     * @param afterChangedCount 改变后的未读提醒数
     */
    public void notifyUnreadSysRemindCountChanged(int afterChangedCount) {
        unreadSysRemindCount = afterChangedCount;
        if (unreadUserMessageCount + unreadSysRemindCount == 0) {
            atvReminder.removeShow();
        } else {
            atvReminder.showPoint();
        }
    }
}
