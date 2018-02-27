package com.winsion.dispatch.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by yalong on 2016/6/14.
 * MyIndicator
 */
public class MyIndicator extends RadioGroup implements RadioGroup.OnCheckedChangeListener {
    private ViewPager mViewPager;
    private PagerAdapter pagerAdapter;

    public MyIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnCheckedChangeListener(this);
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        this.pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter.getCount() != getChildCount()) {
            throw new RuntimeException("The child count of ViewPager not equals Indicator's");
        }
        // 默认选中第一个
        whichChecked(0);
        setButtonText();
    }

    public void whichChecked(int which) {
        RadioButton childAt = (RadioButton) getChildAt(which);
        childAt.setChecked(true);
    }

    private void setButtonText() {
        for (int i = 0; i < getChildCount(); i++) {
            RadioButton childAt = (RadioButton) getChildAt(i);
            CharSequence pageTitle = pagerAdapter.getPageTitle(i);
            if (pageTitle == null) {
                throw new NullPointerException("You did'nt override the method getPageTitle() on ViewPager");
            }
            childAt.setText(pageTitle);
        }
    }

    /**
     * 更改RadioGroup中的选项，切换对应的ViewPager页面
     * 判断是哪个RadioGroup的事件
     *
     * @param radioGroup
     * @param i
     */
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int childCount = radioGroup.getChildCount();
        for (int j = 0; j < childCount; j++) {
            if (radioGroup.getChildAt(j).getId() == i) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(j, false);
                }
            }
        }
    }
}
