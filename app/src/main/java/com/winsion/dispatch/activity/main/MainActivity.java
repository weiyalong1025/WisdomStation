package com.winsion.dispatch.activity.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.billy.cc.core.component.CC;
import com.winsion.component.basic.PlaceHolderFragment;
import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.utils.ImageLoader;
import com.winsion.component.basic.view.AlphaTabView;
import com.winsion.component.basic.view.AlphaTabsIndicator;
import com.winsion.dispatch.R;

import java.util.ArrayList;

/**
 * Created by 10295 on 2017/12/6 0006
 */

public class MainActivity extends BaseActivity implements MainContract.View, ViewPager.OnPageChangeListener {
    private ViewPager vpContent;
    private AlphaTabView atvOperation;
    private AlphaTabView atvDispatch;
    private AlphaTabView atvContacts;
    private AlphaTabView atvScene;
    private AlphaTabView atvRemind;
    private AlphaTabsIndicator atiIndicator;
    private ImageView ivHead;

    private final ArrayList<Fragment> mFragments = new ArrayList<>();
    private MainContract.Presenter mPresenter;

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void start() {
        if (!CC.hasComponent("ComponentUser")) {
            // 没有登录也没有用户组件
            showToast("没有登录组件");
        } else if (!CacheDataSource.getLoginState()) {
            // 没有登录但有登录组件，先进行登录
            // 跳转用户组件-登录界面
            showToast("请先登录");
            CC.obtainBuilder("ComponentUser")
                    .setActionName("toLoginActivityClearTask")
                    .build()
                    .call();
        } else {
            initView();
            initPresenter();
            initData();
            initAdapter();
            initListener();
            loadUserHead();
        }
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
        atvScene = findViewById(R.id.atv_scene);
        atvRemind = findViewById(R.id.atv_remind);
        atiIndicator = findViewById(R.id.ati_indicator);
        ivHead = findViewById(R.id.iv_head);
    }

    private void initPresenter() {
        mPresenter = new MainPresenter(this);
        mPresenter.start();
    }

    private void initData() {
        mFragments.add(getFragment("ComponentTask", "getOperationRootFragment"));
        mFragments.add(getFragment("ComponentAAD", "getAADRootFragment"));
        mFragments.add(getFragment("ComponentContact", "getContactRootFragment"));
        mFragments.add(getFragment("ComponentScene", "getSceneRootFragment"));
        mFragments.add(getFragment("ComponentRemind", "getRemindRootFragment"));
    }

    /**
     * 根据组件名和动作名获取对应fragment，如果没有加载相应组件返回PlaceholderFragment
     *
     * @param componentName 组件名
     * @param actionName    动作名
     * @return 对应的fragment
     */
    private Fragment getFragment(String componentName, String actionName) {
        Fragment fragment = CC
                .obtainBuilder(componentName)
                .setActionName(actionName)
                .build()
                .call()
                .getDataItem("fragment");
        if (fragment == null) {
            fragment = new PlaceHolderFragment();
        }
        return fragment;
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
        vpContent.setOffscreenPageLimit(4);
    }

    private void initListener() {
        vpContent.setOnPageChangeListener(this);
        addOnClickListeners(R.id.iv_head);
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
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void loadUserHead() {
        ImageLoader.loadAddress(ivHead, CacheDataSource.getUserHeadAddress(), R.drawable.basic_ic_head_single, R.drawable.basic_ic_head_single);
    }

    @Override
    public void onClick(View view) {
        // 跳转用户界面
        CC.obtainBuilder("ComponentUser")
                .setContext(this)
                .setActionName("toUserActivity")
                .build()
                .call();
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
            atvRemind.removeShow();
        } else {
            atvRemind.showPoint();
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
            atvRemind.removeShow();
        } else {
            atvRemind.showPoint();
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
            atvRemind.removeShow();
        } else {
            atvRemind.showPoint();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.exit();
        }
    }
}
