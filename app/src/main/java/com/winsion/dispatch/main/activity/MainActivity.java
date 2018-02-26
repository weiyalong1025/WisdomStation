package com.winsion.dispatch.main.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.SwitchSysActivity;
import com.winsion.dispatch.application.AppApplication;
import com.winsion.dispatch.base.BaseActivity;
import com.winsion.dispatch.common.constants.SystemType;
import com.winsion.dispatch.data.CacheDataSource;
import com.winsion.dispatch.data.SPDataSource;
import com.winsion.dispatch.data.constants.SPKey;
import com.winsion.dispatch.modules.contacts.fragment.ContactsRootFragment;
import com.winsion.dispatch.modules.daofa.fragment.DaofaRootFragment;
import com.winsion.dispatch.modules.grid.fragment.GridRootFragment;
import com.winsion.dispatch.modules.operation.fragment.OperationRootFragment;
import com.winsion.dispatch.modules.reminder.ReminderRootFragment;
import com.winsion.dispatch.modules.scene.fragment.SceneRootFragment;
import com.winsion.dispatch.mqtt.MQTTClient;
import com.winsion.dispatch.user.UserActivity;
import com.winsion.dispatch.utils.ImageLoader;
import com.winsion.dispatch.view.AlphaTabView;
import com.winsion.dispatch.view.AlphaTabsIndicator;

import java.util.ArrayList;

/**
 * Created by 10295 on 2017/12/6 0006
 */

public class MainActivity extends BaseActivity implements MainContract.View, ViewPager.OnPageChangeListener {
    private ViewPager vpContent;
    private AlphaTabView atvOperation;
    private AlphaTabView atvDispatch;
    private AlphaTabView atvContacts;
    private AlphaTabView atvGrid;
    private AlphaTabView atvScene;
    private AlphaTabView atvReminder;
    private AlphaTabsIndicator atiIndicator;
    private ImageView ivSwitch;
    private ImageView ivHead;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private int mCurrentSysType = -1;
    private MainContract.Presenter mPresenter;

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void start() {
        initView();
        initPresenter();
        initData();
        initAdapter();
        initListener();
        loadUserHead();
        startMQClient();
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    private void initView() {
        vpContent = findViewById(R.id.vp_content);
        atvOperation = findViewById(R.id.atv_operation);
        atvDispatch = findViewById(R.id.atv_dispatch);
        atvContacts = findViewById(R.id.atv_contacts);
        atvGrid = findViewById(R.id.atv_grid);
        atvScene = findViewById(R.id.atv_scene);
        atvReminder = findViewById(R.id.atv_reminder);
        atiIndicator = findViewById(R.id.ati_indicator);
        ivSwitch = findViewById(R.id.iv_switch);
        ivHead = findViewById(R.id.iv_head);
    }

    private void initPresenter() {
        mPresenter = new MainPresenter(this);
        mPresenter.start();
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
        // 预加载所有界面
        vpContent.setOffscreenPageLimit(5);
    }

    private void initListener() {
        vpContent.setOnPageChangeListener(this);
        addOnClickListeners(R.id.iv_switch, R.id.iv_head);
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

    private void startMQClient() {
        if (AppApplication.TEST_MODE) {
            return;
        }
        String host = (String) SPDataSource.get(mContext, SPKey.KEY_IP, "");
        new MQTTClient.Connector(mContext, host)
                .reconnect(true)
                .connect();
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

    @Override
    public void onClick(View view) {
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
     * 未读代办事项
     */
    private int unreadTodoCount;
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
        if (unreadUserMessageCount + unreadTodoCount + unreadSysRemindCount == 0) {
            atvReminder.removeShow();
        } else {
            atvReminder.showPoint();
        }
    }

    /**
     * 未读待办事项数发生改变，更新小红点
     *
     * @param afterChangedCount 改变后的未读待办事项
     */
    public void notifyUnreadTodoCountChanged(int afterChangedCount) {
        unreadTodoCount = afterChangedCount;
        if (unreadUserMessageCount + unreadTodoCount + unreadSysRemindCount == 0) {
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
        if (unreadUserMessageCount + unreadTodoCount + unreadSysRemindCount == 0) {
            atvReminder.removeShow();
        } else {
            atvReminder.showPoint();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
