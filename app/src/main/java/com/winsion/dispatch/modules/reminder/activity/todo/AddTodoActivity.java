package com.winsion.dispatch.modules.reminder.activity.todo;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseActivity;
import com.winsion.dispatch.common.biz.CommonBiz;
import com.winsion.dispatch.modules.reminder.entity.TodoEntity;
import com.winsion.dispatch.utils.ConvertUtils;
import com.winsion.dispatch.utils.constants.Formatter;
import com.winsion.dispatch.view.TitleView;

import java.util.Calendar;
import java.util.Date;

import static com.winsion.dispatch.modules.reminder.constants.Intents.Todo.TODO_ID;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 1:53
 */

public class AddTodoActivity extends BaseActivity implements AddTodoContract.View, TextWatcher {
    private TextView tvDate;
    private TextView tvTime;
    private EditText etDesc;
    private TitleView tvTitle;
    private Button btnSave;
    private TextView tvCounter;

    private AddTodoContract.Presenter mPresenter;
    private boolean isUpdate;   // 是否是更新
    private long todoId;

    @Override
    protected int setContentView() {
        return R.layout.activity_add_todo;
    }

    @Override
    protected void start() {
        initPresenter();
        initView();
        initData();
        initListener();
    }

    private void initPresenter() {
        mPresenter = new AddTodoPresenter(this);
        mPresenter.start();
    }

    private void initView() {
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        etDesc = findViewById(R.id.et_desc);
        tvTitle = findViewById(R.id.tv_title);
        btnSave = findViewById(R.id.btn_save);
        tvCounter = findViewById(R.id.tv_counter);
    }

    private void initData() {
        todoId = getIntent().getLongExtra(TODO_ID, 0);
        String planDate;
        if (isUpdate = todoId != 0) {
            TodoEntity todoEntity = mPresenter.getTodoById(todoId);
            planDate = ConvertUtils.formatDate(todoEntity.getPlanDate(), Formatter.DATE_FORMAT1);
            String desc = todoEntity.getContent();
            tvTitle.setTitleText(R.string.title_update_toto);
            btnSave.setText(R.string.btn_update);
            etDesc.setText(desc);
            etDesc.setSelection(desc.length());
        } else {
            long millis = System.currentTimeMillis();
            planDate = ConvertUtils.formatDate(millis, Formatter.DATE_FORMAT1);
        }
        String[] split = planDate.split(" ");
        tvDate.setText(split[0]);
        tvTime.setText(split[1].substring(0, 5));
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
        etDesc.addTextChangedListener(this);
        addOnClickListeners(R.id.tv_date, R.id.tv_time, R.id.btn_save);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date:
                // 显示日期选择器对话框
                showDatePicker();
                break;
            case R.id.tv_time:
                // 显示时间选择器对话框
                showTimePicker();
                break;
            case R.id.btn_save:
                // 必须填写提醒内容
                if (isEmpty(getText(etDesc))) {
                    showToast(getString(R.string.toast_complete_info));
                } else {
                    if (isUpdate) {
                        mPresenter.updateTodo(getText(etDesc), getText(tvDate), getText(tvTime), todoId);
                    } else {
                        mPresenter.addTodo(getText(etDesc), getText(tvDate), getText(tvTime));
                    }
                }
                break;
        }
    }

    /**
     * 显示日期选择器
     */
    private void showDatePicker() {
        // 隐藏键盘
        CommonBiz.hideKeyboard(etDesc);
        String dateStr = getText(tvDate);
        Date currentDate = new Date(ConvertUtils.parseDate(dateStr, Formatter.DATE_FORMAT4));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        TimePickerView.OnTimeSelectListener listener = (Date date, View v) ->
                tvDate.setText(ConvertUtils.formatDate(date.getTime(), Formatter.DATE_FORMAT4));

        TimePickerView datePickerView = CommonBiz.getMyTimePickerBuilder(mContext, listener)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1)
                .setDate(calendar)
                .build();

        CommonBiz.selfAdaptionTopBar(datePickerView);
        datePickerView.show();
    }

    /**
     * 显示时间选择器
     */
    private void showTimePicker() {
        CommonBiz.hideKeyboard(etDesc);
        String time = getText(tvTime);
        String[] split1 = time.split(":");
        Date currentDate = new Date();
        currentDate.setHours(Integer.valueOf(split1[0]));
        currentDate.setMinutes(Integer.valueOf(split1[1]));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        TimePickerView.OnTimeSelectListener listener = (Date date, View v) ->
                tvTime.setText(ConvertUtils.formatDate(date.getTime(), Formatter.DATE_FORMAT7));

        TimePickerView timePickerView = CommonBiz.getMyTimePickerBuilder(mContext, listener)
                .setType(new boolean[]{false, false, false, true, true, false})
                .setRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1)
                .setDate(calendar)
                .build();

        CommonBiz.selfAdaptionTopBar(timePickerView);
        timePickerView.show();
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void updateOrAddSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String note = s.toString();
        tvCounter.setText(String.format("%s/100", String.valueOf(note.length())));
    }
}
