package com.winsion.dispatch.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.utils.ConvertUtils;
import com.winsion.dispatch.utils.ViewUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Created by 10295 on 2017/12/15 0015.
 * 下拉选项框
 */

public class SpinnerView extends LinearLayout implements TextWatcher {
    private TextView textFirst;
    private ImageView arrowFirst;
    private TextView textSecond;
    private ImageView arrowSecond;
    private EditText search;

    private static final int FIRST_OPTION_TAG = 0;
    private static final int SECOND_OPTION_TAG = 1;

    public static final int POPUP_SHOW = 0;
    public static final int POPUP_HIDE = 1;

    private int firstOptionSelectPosition = 0;
    private int secondOptionSelectPosition = 0;
    private PopupWindow firstPopupWindow;
    private PopupWindow secondPopupWindow;

    public interface OptionItemClickListener {
        void onItemClick(int position);
    }

    public interface AfterTextChangeListener {
        void afterTextChange(Editable s);
    }

    public interface PopupDisplayChangeListener {
        void popupDisplayChange(int status);
    }

    private OptionItemClickListener firstOptionItemClickListener;
    private OptionItemClickListener secondOptionItemClickListener;
    private AfterTextChangeListener afterTextChangeListener;
    private PopupDisplayChangeListener popupDisplayChangeListener;

    public SpinnerView(Context context) {
        this(context, null);
    }

    public SpinnerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinnerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinnerView, defStyleAttr, 0);
        boolean showFirstOption = typedArray.getBoolean(R.styleable.SpinnerView_showFirstOption, true);
        boolean showFirstDiv = typedArray.getBoolean(R.styleable.SpinnerView_showFirstDiv, true);
        boolean showSecondOption = typedArray.getBoolean(R.styleable.SpinnerView_showSecondOption, true);
        boolean showSecondDiv = typedArray.getBoolean(R.styleable.SpinnerView_showSecondDiv, true);
        boolean showSearchView = typedArray.getBoolean(R.styleable.SpinnerView_showSearchView, true);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.view_spinner, this);
        LinearLayout containerFirst = findViewById(R.id.ll_container_first);
        textFirst = findViewById(R.id.iv_text_first);
        arrowFirst = findViewById(R.id.iv_arrow_first);
        View divFirst = findViewById(R.id.div_first);

        LinearLayout containerSecond = findViewById(R.id.ll_container_second);
        textSecond = findViewById(R.id.iv_text_second);
        arrowSecond = findViewById(R.id.iv_arrow_second);
        View divSecond = findViewById(R.id.div_second);

        search = findViewById(R.id.et_search);
        RelativeLayout rlSearch = findViewById(R.id.rl_search_view);

        if (showFirstOption) {
            containerFirst.setVisibility(VISIBLE);
        } else {
            containerFirst.setVisibility(GONE);
        }

        if (showFirstDiv) {
            divFirst.setVisibility(VISIBLE);
        } else {
            divFirst.setVisibility(GONE);
        }

        if (showSecondOption) {
            containerSecond.setVisibility(VISIBLE);
        } else {
            containerSecond.setVisibility(GONE);
        }

        if (showSecondDiv) {
            divSecond.setVisibility(VISIBLE);
        } else {
            divSecond.setVisibility(GONE);
        }

        if (showSearchView) {
            rlSearch.setVisibility(VISIBLE);
        } else {
            rlSearch.setVisibility(GONE);
        }

        containerFirst.setOnClickListener((view) -> {
            if (firstPopupWindow != null) {
                firstPopupWindow.showAsDropDown(view);
                arrowFirst.setImageResource(R.drawable.ic_arrow_up_little);
                if (popupDisplayChangeListener != null) {
                    popupDisplayChangeListener.popupDisplayChange(POPUP_SHOW);
                }
            }
        });

        containerSecond.setOnClickListener((view) -> {
            if (secondPopupWindow != null) {
                secondPopupWindow.showAsDropDown(view);
                arrowSecond.setImageResource(R.drawable.ic_arrow_up_little);
                if (popupDisplayChangeListener != null) {
                    popupDisplayChangeListener.popupDisplayChange(POPUP_SHOW);
                }
            }
        });

        search.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (afterTextChangeListener != null) {
            afterTextChangeListener.afterTextChange(s);
        }
    }

    public void setFirstOptionData(List<String> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        firstPopupWindow = initPopupWindow(data, FIRST_OPTION_TAG);
        textFirst.setText(data.get(0));
    }

    public void setFirstOptionItemClickListener(OptionItemClickListener listener) {
        this.firstOptionItemClickListener = listener;
    }

    public void setSecondOptionData(List<String> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        secondPopupWindow = initPopupWindow(data, SECOND_OPTION_TAG);
        textSecond.setText(data.get(0));
    }

    public void setSecondOptionItemClickListener(OptionItemClickListener listener) {
        this.secondOptionItemClickListener = listener;
    }

    public void setAfterTextChangeListener(AfterTextChangeListener listener) {
        this.afterTextChangeListener = listener;
    }

    public void setPopupDisplayChangeListener(PopupDisplayChangeListener listener) {
        this.popupDisplayChangeListener = listener;
    }

    private PopupWindow initPopupWindow(List<String> list, int tag) {
        CommonAdapter<String> adapter = new CommonAdapter<String>(getContext(), R.layout.item_spinner, list) {
            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.tv_text, item);
                boolean isSelect = false;
                switch (tag) {
                    case FIRST_OPTION_TAG:
                        isSelect = position == firstOptionSelectPosition;
                        break;
                    case SECOND_OPTION_TAG:
                        isSelect = position == secondOptionSelectPosition;
                        break;
                }
                viewHolder.setVisible(R.id.iv_select, isSelect);
                int colorWhite = getResources().getColor(R.color.white1);
                int colorGray = getResources().getColor(R.color.gray2);
                viewHolder.setTextColor(R.id.tv_text, isSelect ? colorWhite : colorGray);
                if (position == 0) {
                    View view = viewHolder.getView(R.id.divider);
                    LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.leftMargin = 0;
                    view.setLayoutParams(layoutParams);
                } else {
                    View view = viewHolder.getView(R.id.divider);
                    LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.leftMargin = ConvertUtils.dp2px(getContext(), 15);
                    view.setLayoutParams(layoutParams);
                }
            }
        };

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams")
        View itemView = layoutInflater.inflate(R.layout.item_spinner, null);
        itemView.measure(0, 0);
        int suggestMaxHeight = ViewUtils.getSuggestMaxHeight(getContext(), itemView.getMeasuredHeight());
        ListView listView = new WrapContentListView(getContext(), suggestMaxHeight);
        listView.setBackgroundResource(R.color.gray7);
        listView.setAdapter(adapter);
        listView.setDivider(null);

        PopupWindow popupWindow = new PopupWindow(listView, LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOnDismissListener(() -> {
            arrowFirst.setImageResource(R.drawable.ic_arrow_down_little);
            arrowSecond.setImageResource(R.drawable.ic_arrow_down_little);
            if (popupDisplayChangeListener != null) {
                popupDisplayChangeListener.popupDisplayChange(POPUP_HIDE);
            }
        });
        listView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            switch (tag) {
                case FIRST_OPTION_TAG:
                    firstOptionSelectPosition = position;
                    textFirst.setText(list.get(position));

                    if (firstOptionItemClickListener != null) {
                        firstOptionItemClickListener.onItemClick(position);
                    }
                    break;
                case SECOND_OPTION_TAG:
                    secondOptionSelectPosition = position;
                    textSecond.setText(list.get(position));

                    if (secondOptionItemClickListener != null) {
                        secondOptionItemClickListener.onItemClick(position);
                    }
                    break;
            }
            adapter.notifyDataSetChanged();
            popupWindow.dismiss();
        });
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        return popupWindow;
    }

    /**
     * 设置搜索框内容
     */
    public void setSearchContent(String content) {
        if (content != null) {
            search.setText(content);
            search.setSelection(content.length());
        }
    }
}
